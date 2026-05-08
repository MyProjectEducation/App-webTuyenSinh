const { ScoreModel, subjectColMap } = require('../models/scoreModel');
const xlsx = require('xlsx');
const db = require('../../config/db');
const { smartMap } = require('../utils/excelUtils');

class ScoreController {
    static async getAll(req, res) {
        try {
            const data = await ScoreModel.getAll();
            // Dữ liệu SQL đang là 1 dòng N cột. Giao diện cần mảng normalize: 1 dòng 1 điểm để hiện trên danh sách cũ.
            // Nhưng thiết kế mới cần danh sách nhóm gọn lại theo thí sinh?
            // User requirement: Bảng hiển thị xuất hiện 2 record môn? Hay lưới?
            // "Chuẩn hóa ngược (Denormalize): Chuyển 1 dòng trong CSDL thành nhiều dòng (Object) nhỏ khi trả về Frontend."
            // Vậy getAll vẫn giữ nguyên logic mảng normalized 1 dòng = 1 môn.
            let normalizedScores = [];

            data.forEach(row => {
                const hoTen = (row.ho || '') + ' ' + (row.ten || '');
                const loaiDiem = row.d_phuongthuc || 'THPT';

                // Quét qua các môn được map để kiểm tra xem có điểm > 0 không
                Object.entries(subjectColMap).forEach(([monName, colName]) => {
                    const diem = parseFloat(row[colName]);
                    if (!isNaN(diem) && diem > 0) {
                        normalizedScores.push({
                            id: `${row.cccd}_${loaiDiem}_${colName}`,
                            cccd: row.cccd,
                            hoTen: hoTen.trim() || 'Thí sinh vô danh',
                            loaiDiem: loaiDiem,
                            mon: monName,
                            colName: colName, // Frontend sẽ cần colName để biết cột nào
                            diem: diem
                        });
                    }
                });
            });

            res.json(normalizedScores);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            // Frontend gửi nguyên khối: { cccd, loaiDiem, scores: { TO: 8, LI: 7, ... } }
            const { cccd, loaiDiem, scores } = req.body;
            await ScoreModel.upsertCandidateScore(cccd, loaiDiem, scores);
            res.status(201).json({ message: "Upsert khối điểm thành công" });
        } catch (error) {
            console.error(error);
            const status = error.message.includes('không tồn tại') ? 400 : 500;
            res.status(status).json({ error: error.message || 'Lỗi khi cập nhật điểm' });
        }
    }

    static async update(req, res) {
        try {
            // Upsert gộp cả create và update nên xử lý giống nhau
            const { cccd, loaiDiem, scores } = req.body;
            await ScoreModel.upsertCandidateScore(cccd, loaiDiem, scores);
            res.json({ message: "Update khối điểm thành công" });
        } catch (error) {
            console.error(error);
            const status = error.message.includes('không tồn tại') ? 400 : 500;
            res.status(status).json({ error: error.message || 'Server error' });
        }
    }

    static async delete(req, res) {
        try {
            // Nhạn id có cấu trúc `${cccd}_${loaiDiem}_${colName}`
            const { id } = req.params;
            const parts = id.split('_');
            const colName = parts.pop();
            const loaiDiem = parts.pop();
            const cccd = parts.join('_');

            await ScoreModel.deleteScore(cccd, loaiDiem, colName);
            res.json({ message: 'Đã xóa điểm thành công' });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async importScores(req, res) {
        if (!req.file) return res.status(400).json({ message: "Vui lòng tải lên file Excel" });

        const connection = await db.getConnection();

        try {
            const workbook = xlsx.read(req.file.buffer, { type: 'buffer' });

            const EXCEL_MAPPING = {
                'cccd': 'cccd',
                'socccd': 'cccd',
                'cancuoc': 'cccd',
                'cmnd': 'cccd',
                'phuongthuc': 'd_phuongthuc',
                'to': 'TO',
                'toan': 'TO',
                'li': 'LI',
                'vatli': 'LI',
                'ho': 'HO',
                'hoahoc': 'HO',
                'si': 'SI',
                'sinhhoc': 'SI',
                'su': 'SU',
                'lichsu': 'SU',
                'di': 'DI',
                'diali': 'DI',
                'va': 'VA',
                'nguvan': 'VA',
                'van': 'VA',
                'gdcd': 'GDCD',
                'nn': 'N1_THI',
                'ngoainguthi': 'N1_THI',
                'tienganh': 'N1_THI',
                'ngoainguquydoi': 'N1_CC',
                'cncn': 'CNCN',
                'cnnn': 'CNNN',
                'ti': 'TI',
                'ktpl': 'KTPL',
                'dgnl': 'NL1',
                'diemdgnl': 'NL1',
                'nk1': 'NK1',
                'nk2': 'NK2'
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

            // Danh sách các cột điểm cố định trong Database
            const scoreCols = [
                'TO', 'LI', 'HO', 'SI', 'SU', 'DI', 'VA', 'N1_THI', 'N1_CC',
                'CNCN', 'CNNN', 'TI', 'KTPL', 'NL1', 'NK1', 'NK2'
            ];

            // 1. Lọc dữ liệu hợp lệ và Gom nhóm theo CCCD
            // Nếu thí sinh có trên 2 dòng (ví dụ 1 dòng điểm THPT, 1 dòng điểm ĐGNL), ta gộp lại
            const candidateMap = new Map();

            for (const row of allRawData) {
                let cccd = row.cccd ? String(row.cccd).trim() : null;
                if (!cccd) continue;

                let data = candidateMap.get(cccd) || { cccd, d_phuongthuc: 'THPT' };
                if (row.d_phuongthuc) {
                    data.d_phuongthuc = String(row.d_phuongthuc).trim();
                }

                for (const col of scoreCols) {
                    if (row[col] !== undefined && row[col] !== null) {
                        const scoreVal = parseFloat(row[col]);
                        if (!isNaN(scoreVal)) {
                            data[col] = scoreVal;
                        }
                    }
                }

                candidateMap.set(cccd, data);
            }

            const insertValues = Array.from(candidateMap.values());
            if (insertValues.length === 0) {
                return res.status(400).json({ message: "Không có dữ liệu điểm hợp lệ để import." });
            }

            // KHI BULK INSERT, NẾU 1 DÒNG SAI FOREIGN KEY THÌ CẢ CHUNK SẼ LỖI.
            // Do đó, ta phải Bulk Check sự tồn tại của CCCD trong hệ thống trước!
            const allCccds = Array.from(candidateMap.keys());
            const validCccds = new Set();

            // Chia nhỏ check IN clause để không vượt quá giới hạn của MySQL (thường là 1000-2000)
            for (let i = 0; i < allCccds.length; i += 1000) {
                const chunkCccds = allCccds.slice(i, i + 1000);
                const [candRows] = await connection.query(
                    'SELECT cccd FROM xt_thisinhxettuyen25 WHERE cccd IN (?)',
                    [chunkCccds]
                );
                candRows.forEach(row => validCccds.add(row.cccd));
            }

            // Chỉ giữ lại những thí sinh thực sự tồn tại trong DB
            const finalInsertValues = insertValues.filter(v => validCccds.has(v.cccd));

            if (finalInsertValues.length === 0) {
                return res.status(400).json({ message: "Không có dữ liệu điểm hợp lệ (hoặc không tìm thấy CCCD nào trong hệ thống)." });
            }

            await connection.beginTransaction();
            let successCount = 0;

            // 2. Tối ưu I/O bằng Bulk Insert chia Chunk (500 dòng/lần)
            const chunkSize = 500;
            for (let i = 0; i < finalInsertValues.length; i += chunkSize) {
                const chunk = finalInsertValues.slice(i, i + chunkSize);

                const queryValues = chunk.map(v => [
                    v.cccd,
                    v.d_phuongthuc,
                    ...scoreCols.map(col => v[col] !== undefined ? v[col] : null)
                ]);

                // Xây dựng câu lệnh ON DUPLICATE KEY UPDATE tự động cho tất cả các môn
                const updateClauses = scoreCols.map(col => `\`${col}\`=IF(VALUES(\`${col}\`) IS NOT NULL, VALUES(\`${col}\`), \`${col}\`)`).join(', ');

                const query = `
                    INSERT INTO xt_diemthixettuyen 
                    (cccd, d_phuongthuc, ${scoreCols.map(col => `\`${col}\``).join(', ')})
                    VALUES ?
                    ON DUPLICATE KEY UPDATE
                    d_phuongthuc=VALUES(d_phuongthuc), ${updateClauses}
                `;

                await connection.query(query, [queryValues]);
                successCount += chunk.length;
            }

            await connection.commit();
            res.status(200).json({
                message: `Import thành công! Đã thêm/cập nhật điểm cho ${successCount} thí sinh.`,
                successCount
            });

        } catch (error) {
            await connection.rollback();
            console.error("Lỗi Import Excel Điểm thí sinh:", error);
            res.status(500).json({ message: "Lỗi xử lý file Excel", error: error.message });
        } finally {
            connection.release();
        }
    }
}

module.exports = ScoreController;
