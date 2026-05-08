const SubjectComboModel = require('../models/subjectComboModel');
const xlsx = require('xlsx');
const db = require('../../config/db');
const { smartMap } = require('../utils/excelUtils');

class SubjectComboController {
    static async getAll(req, res) {
        try {
            const data = await SubjectComboModel.getAll();
            const mappedData = data.map(item => ({
                id: item.idtohop.toString(),
                maToHop: item.matohop,
                tenToHop: item.tentohop || '',
                mon1: item.mon1 || '',
                mon2: item.mon2 || '',
                mon3: item.mon3 || ''
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            const result = await SubjectComboModel.create(req.body);
            res.status(201).json({ id: result.insertId.toString(), ...req.body });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async update(req, res) {
        try {
            const { id } = req.params;
            await SubjectComboModel.update(id, req.body);
            res.json({ id, ...req.body });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await SubjectComboModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async importSubjectCombos(req, res) {
        if (!req.file) return res.status(400).json({ message: "Vui lòng tải lên file Excel" });

        const connection = await db.getConnection();

        try {
            const workbook = xlsx.read(req.file.buffer, { type: 'buffer' });
            
            const EXCEL_MAPPING = {
                'matohop': 'chuoi_tohop',   // Ví dụ: B03(TO-3,VA-3,SI-1)
                'tentohop': 'matohop',      // Ví dụ: B03
                'tohop': 'matohop',
                'mon1': 'mon1',
                'mon2': 'mon2',
                'mon3': 'mon3',
                'ten': 'tentohop'
            };

            const normalizeHeader = (headerName) => {
                if (!headerName) return '';
                return headerName.toString().toLowerCase()
                    .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
                    .replace(/[đđ]/g, 'd').replace(/[^a-z0-9]/g, '');
            };

            let allRawData = [];

            // Quét đa Sheet và tự động tìm dòng Header (Smart Scan)
            workbook.SheetNames.forEach(name => {
                const sheetRows = xlsx.utils.sheet_to_json(workbook.Sheets[name], { header: 1 });
                let headerRowIndex = -1;
                let recognizedHeaders = [];

                for (let i = 0; i < Math.min(sheetRows.length, 20); i++) {
                    const row = sheetRows[i];
                    if (!Array.isArray(row)) continue;
                    
                    let matchCount = 0;
                    let tempHeaders = [];
                    for (let j = 0; j < row.length; j++) {
                        const cellStr = row[j] !== undefined && row[j] !== null ? String(row[j]) : '';
                        const norm = normalizeHeader(cellStr);
                        tempHeaders.push(norm);
                        if (EXCEL_MAPPING[norm]) {
                            matchCount++;
                        }
                    }
                    
                    if (matchCount >= 2) {
                        headerRowIndex = i;
                        recognizedHeaders = tempHeaders;
                        break;
                    }
                }

                if (headerRowIndex !== -1) {
                    for (let i = headerRowIndex + 1; i < sheetRows.length; i++) {
                        const row = sheetRows[i];
                        if (!Array.isArray(row) || row.length === 0) continue;

                        let cleanRow = {};
                        for (let j = 0; j < recognizedHeaders.length; j++) {
                            const dbCol = EXCEL_MAPPING[recognizedHeaders[j]];
                            if (dbCol && row[j] !== undefined && row[j] !== null && row[j] !== '') {
                                cleanRow[dbCol] = String(row[j]).trim();
                            }
                        }
                        
                        if (Object.keys(cleanRow).length > 0) {
                            allRawData.push(cleanRow);
                        }
                    }
                }
            });

            if (allRawData.length === 0) {
                return res.status(400).json({ message: "Không tìm thấy dữ liệu hợp lệ trong file Excel" });
            }

            // Dùng Map để khử trùng lặp (Deduplicate) vì 1 file có thể có nhiều ngành xài chung 1 Tổ hợp
            const comboMap = new Map();

            const SUBJECT_NAMES = {
                'TO': 'Toán',
                'VA': 'Ngữ văn',
                'LI': 'Vật lý',
                'HO': 'Hóa học',
                'SI': 'Sinh học',
                'SU': 'Lịch sử',
                'DI': 'Địa lý',
                'N1': 'Tiếng Anh',
                'N2': 'Tiếng Nga',
                'N3': 'Tiếng Pháp',
                'N4': 'Tiếng Trung',
                'N5': 'Tiếng Đức',
                'N6': 'Tiếng Nhật',
                'N7': 'Tiếng Hàn',
                'KTPL': 'Giáo dục KT và PL',
                'TI': 'Tin học',
                'CNCN': 'Công nghệ (Công nghiệp)',
                'CNNN': 'Công nghệ (Nông nghiệp)',
                'GDCD': 'GDCD',
                'NK1': 'Năng khiếu 1',
                'NK2': 'Năng khiếu 2'
            };

            for (const comboData of allRawData) {
                // Nếu matohop không có trực tiếp, thử lấy từ chuoi_tohop nếu nó có dạng B03(...)
                let maToHop = comboData.matohop;
                
                let m1 = comboData.mon1 || null;
                let m2 = comboData.mon2 || null;
                let m3 = comboData.mon3 || null;

                // Nếu có chuỗi dạng B03(TO-3,VA-3,SI-1), ta trích xuất môn học ra
                if (comboData.chuoi_tohop) {
                    // Nếu maToHop chưa có, thử lấy phần trước dấu ngoặc đơn
                    if (!maToHop) {
                        maToHop = comboData.chuoi_tohop.split('(')[0].trim();
                    }

                    const match = comboData.chuoi_tohop.match(/\((.*?)\)/);
                    if (match && match[1]) {
                        const subjects = match[1].split(',').map(s => s.split('-')[0].trim());
                        if (!m1 && subjects[0]) m1 = subjects[0];
                        if (!m2 && subjects[1]) m2 = subjects[1];
                        if (!m3 && subjects[2]) m3 = subjects[2];
                    }
                }

                // FIX BUG: Bỏ qua nếu không có mã tổ hợp hoặc không tách được môn 1 (tránh lỗi null database)
                if (!maToHop || !m1) continue;

                let ten = comboData.tentohop || null;
                // TỰ ĐỘNG TẠO TÊN TỔ HỢP TỪ MÃ MÔN HỌC (nếu chưa có)
                if (!ten && m1) {
                    const ten1 = SUBJECT_NAMES[m1] || m1;
                    const ten2 = m2 ? (SUBJECT_NAMES[m2] || m2) : '';
                    const ten3 = m3 ? (SUBJECT_NAMES[m3] || m3) : '';
                    
                    const names = [ten1, ten2, ten3].filter(n => n !== '');
                    ten = names.join(', ');
                }

                comboMap.set(maToHop, {
                    mon1: m1,
                    mon2: m2,
                    mon3: m3,
                    tentohop: ten
                });
            }

            await connection.beginTransaction();
            let successCount = 0;

            const query = `
                INSERT INTO xt_tohop_monthi (matohop, mon1, mon2, mon3, tentohop)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                mon1=IF(VALUES(mon1) IS NOT NULL, VALUES(mon1), mon1),
                mon2=IF(VALUES(mon2) IS NOT NULL, VALUES(mon2), mon2),
                mon3=IF(VALUES(mon3) IS NOT NULL, VALUES(mon3), mon3),
                tentohop=IF(VALUES(tentohop) IS NOT NULL, VALUES(tentohop), tentohop)
            `;

            for (const [matohop, data] of comboMap.entries()) {
                await connection.query(query, [
                    matohop, data.mon1, data.mon2, data.mon3, data.tentohop
                ]);
                successCount++;
            }

            await connection.commit();
            res.status(200).json({ 
                message: `Import thành công! Đã thêm/cập nhật ${successCount} tổ hợp môn duy nhất.`,
                successCount 
            });

        } catch (error) {
            await connection.rollback();
            console.error("Lỗi Import Excel Tổ hợp môn:", error);
            res.status(500).json({ message: "Lỗi xử lý file Excel", error: error.message });
        } finally {
            connection.release();
        }
    }
}

module.exports = SubjectComboController;
