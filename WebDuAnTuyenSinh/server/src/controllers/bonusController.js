const BonusModel = require('../models/bonusModel');
const xlsx = require('xlsx');
const { smartMap } = require('../utils/excelUtils');

class BonusController {
    static async getAll(req, res) {
        try {
            const data = await BonusModel.getAll();
            const mappedData = data.map(item => ({
                id: item.iddiemcong.toString(),
                cccd: item.ts_cccd,
                hoTen: ((item.ho || '') + ' ' + (item.ten || '')).trim() || 'Vô danh',
                maNganh: item.manganh || '',
                maToHop: item.matohop || '',
                phuongThuc: item.phuongthuc || '',
                diem: parseFloat(item.diemTong) || 0,
                diemC: parseFloat(item.diemCC) || 0,
                diemUt: parseFloat(item.diemUtxt) || 0,
                ghiChu: item.ghichu || ''
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            // Frontend truyền { cccd, manganh, matohop, phuongthuc, khuvuc, doituong, chungchi }
            const result = await BonusModel.upsertBonusPoint(req.body);
            res.status(201).json({ 
                message: "Upsert điểm cộng thành công!", 
                diemTong: result.diemTong,
                diemC: result.diemCC,
                diemUt: result.diemUtxt,
                ghiChu: result.ghichu
            });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Lỗi máy chủ khi tính điểm cộng' });
        }
    }

    static async update(req, res) {
        try {
            const result = await BonusModel.upsertBonusPoint(req.body);
            res.json({ 
                message: "Upsert điểm cộng thành công!", 
                diemTong: result.diemTong,
                diemC: result.diemCC,
                diemUt: result.diemUtxt,
                ghiChu: result.ghichu
            });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Lỗi máy chủ khi tính điểm cộng' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await BonusModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async importBonusPoints(req, res) {
        if (!req.file) return res.status(400).json({ message: "Vui lòng tải lên file Excel" });

        const connection = await require('../../config/db').getConnection();

        try {
            const workbook = xlsx.read(req.file.buffer, { type: 'buffer' });
            
            // --- AUTO DETECT PRIZE FILE ---
            let isPrizeFile = false;
            workbook.SheetNames.forEach(name => {
                const sheetRows = xlsx.utils.sheet_to_json(workbook.Sheets[name], { header: 1 });
                for (let i = 0; i < Math.min(sheetRows.length, 5); i++) {
                    const row = sheetRows[i];
                    if (!Array.isArray(row)) continue;
                    const rowStr = row.join(' ').toLowerCase();
                    if (rowStr.includes('giải') && rowStr.includes('môn') && rowStr.includes('điểm cộng')) {
                        isPrizeFile = true;
                    }
                }
            });

            if (isPrizeFile) {
                if (connection) connection.release();
                return BonusController.importPrizeBonus(req, res);
            }
            
            const EXCEL_MAPPING = {
                'cccd': 'cccd',
                'socccd': 'cccd',
                'cancuoc': 'cccd',
                'cmnd': 'cccd',
                'manganh': 'manganh',
                'nganh': 'manganh',
                'matohop': 'matohop',
                'tohop': 'matohop',
                'phuongthuc': 'phuongthuc',
                'khuvuc': 'khuvuc',
                'kv': 'khuvuc',
                'doituong': 'doituong',
                'dt': 'doituong',
                'chungchi': 'chungchi',
                'cc': 'chungchi'
            };

            const normalizeHeader = (headerName) => {
                if (!headerName) return '';
                return headerName.toString().toLowerCase()
                    .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
                    .replace(/[đđ]/g, 'd').replace(/[^a-z0-9]/g, '');
            };

            let allRawData = [];

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

            // 1. Bulk Check sự tồn tại của CCCD
            const cccdSet = new Set(allRawData.map(r => r.cccd).filter(c => c));
            const allCccds = Array.from(cccdSet);
            const validCccds = new Set();
            
            for (let i = 0; i < allCccds.length; i += 1000) {
                const chunkCccds = allCccds.slice(i, i + 1000);
                const [candRows] = await connection.query(
                    'SELECT cccd FROM xt_thisinhxettuyen25 WHERE cccd IN (?)', 
                    [chunkCccds]
                );
                candRows.forEach(row => validCccds.add(row.cccd));
            }

            // 2. Tải toàn bộ cấu hình Ngành - Tổ hợp (N1) vào bộ nhớ để tính toán nhanh
            const [comboRows] = await connection.query('SELECT manganh, matohop, N1 FROM xt_nganh_tohop');
            const comboN1Map = new Map();
            comboRows.forEach(row => {
                comboN1Map.set(`${row.manganh}_${row.matohop}`, row.N1 === 1);
            });

            // Từ điển tính điểm
            const DIEM_KHU_VUC = { 'KV1': 0.75, 'KV2-NT': 0.5, 'KV2': 0.25, 'KV3': 0.0 };
            const DIEM_DOI_TUONG = {
                'DT01': 2.0, 'DT02': 2.0, 'DT03': 2.0, 'DT04': 2.0,
                'DT05': 1.0, 'DT06': 1.0, 'DT07': 1.0, 'NONE': 0.0
            };
            const DIEM_CHUNG_CHI = {
                'IELTS 5.5': 1.0, 'IELTS 6.0': 1.5, 'IELTS 7.0': 2.0, 'NONE': 0.0
            };

            const insertValues = [];

            for (const row of allRawData) {
                if (!row.cccd || !row.manganh || !row.matohop || !row.phuongthuc) continue;
                if (!validCccds.has(row.cccd)) continue;

                const khuvuc = row.khuvuc || 'KV3';
                const doituong = row.doituong || 'NONE';
                const chungchi = row.chungchi || 'NONE';

                const kvScore = DIEM_KHU_VUC[khuvuc] || 0;
                const dtScore = DIEM_DOI_TUONG[doituong] || 0;
                const diemUtxt = kvScore + dtScore;

                const hasN1 = comboN1Map.get(`${row.manganh}_${row.matohop}`) === true;
                const diemCC = hasN1 ? 0 : (DIEM_CHUNG_CHI[chungchi] || 0);

                const maxScore = 3.0;
                const diemTongRaw = diemUtxt + diemCC;
                const diemTong = Math.min(maxScore, diemTongRaw);

                const noteCC = hasN1 ? 'CC: 0 (Đã dùng N1)' : `CC: ${chungchi} (${diemCC})`;
                const ghichu = `KV: ${khuvuc} (${kvScore}) + ĐT: ${doituong} (${dtScore}) + ${noteCC}. Mức Trần: ${diemTong}`;
                const dc_keys = `${row.cccd}_${row.manganh}_${row.matohop}`;

                insertValues.push([
                    row.cccd, row.manganh, row.matohop, row.phuongthuc, diemCC, diemUtxt, diemTong, ghichu, dc_keys
                ]);
            }

            if (insertValues.length === 0) {
                return res.status(400).json({ message: "Không có dữ liệu điểm cộng hợp lệ (hoặc không tìm thấy thí sinh)." });
            }

            await connection.beginTransaction();
            let successCount = 0;
            const chunkSize = 500;

            const query = `
                INSERT INTO xt_diemcongxetuyen 
                (ts_cccd, manganh, matohop, phuongthuc, diemCC, diemUtxt, diemTong, ghichu, dc_keys) 
                VALUES ?
                ON DUPLICATE KEY UPDATE 
                phuongthuc=VALUES(phuongthuc), diemCC=VALUES(diemCC), diemUtxt=VALUES(diemUtxt), 
                diemTong=VALUES(diemTong), ghichu=VALUES(ghichu)
            `;

            for (let i = 0; i < insertValues.length; i += chunkSize) {
                const chunk = insertValues.slice(i, i + chunkSize);
                await connection.query(query, [chunk]);
                successCount += chunk.length;
            }

            await connection.commit();
            res.status(200).json({ 
                message: `Import thành công! Đã thêm/cập nhật điểm cộng cho ${successCount} bản ghi.`,
                successCount 
            });

        } catch (error) {
            if (connection) await connection.rollback();
            console.error("Lỗi Import Excel Điểm cộng:", error);
            res.status(500).json({ message: "Lỗi xử lý file Excel", error: error.message });
        } finally {
            if (connection) connection.release();
        }
    }

    static async importPrizeBonus(req, res) {
        if (!req.file) return res.status(400).json({ message: "Vui lòng tải lên file Excel" });

        const connection = await require('../../config/db').getConnection();

        try {
            const workbook = xlsx.read(req.file.buffer, { type: 'buffer' });
            
            const EXCEL_MAPPING = {
                'cccd': 'cccd',
                'mondatgiai': 'mon_giai',
                'mamon': 'mon_giai',
                'tennganh': 'tennganh',
                'diemcongchomondatgiai': 'diem_co_mon',
                'diemcongchothxtkokomondatgiai': 'diem_ko_mon',
                'diemcongchothxtkocomondatgiai': 'diem_ko_mon'
            };

            const normalizeHeader = (headerName) => {
                if (!headerName) return '';
                return headerName.toString().toLowerCase()
                    .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
                    .replace(/[đđ]/g, 'd').replace(/[^a-z0-9]/g, '');
            };

            let allRawData = [];

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
                    
                    if (matchCount >= 3) { // Cần ít nhất 3 cột chuẩn
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
                return res.status(400).json({ message: "Không tìm thấy dữ liệu hợp lệ trong file Excel Giải HSG" });
            }

            const cccdSet = new Set(allRawData.map(r => r.cccd).filter(c => c));
            const allCccds = Array.from(cccdSet);
            const nvMap = new Map(); // Key: cccd, Value: mảng các NV (manganh, tennganh, matohop)
            
            // Lấy danh sách Nguyện vọng và Tên ngành từ DB
            for (let i = 0; i < allCccds.length; i += 1000) {
                const chunkCccds = allCccds.slice(i, i + 1000);
                const [nvRows] = await connection.query(`
                    SELECT nv.nn_cccd AS cccd, nv.nv_manganh AS manganh, n.tennganh, nv.tt_thm AS matohop 
                    FROM xt_nguyenvongxettuyen nv
                    LEFT JOIN xt_nganh n ON nv.nv_manganh = n.manganh
                    WHERE nv.nn_cccd IN (?)
                `, [chunkCccds]);
                
                nvRows.forEach(row => {
                    let list = nvMap.get(row.cccd) || [];
                    list.push(row);
                    nvMap.set(row.cccd, list);
                });
            }

            // Tải cấu hình tổ hợp môn (để lấy mon1, mon2, mon3)
            const [thRows] = await connection.query('SELECT matohop, mon1, mon2, mon3 FROM xt_tohop_monthi');
            const tohopMap = new Map();
            thRows.forEach(row => {
                tohopMap.set(row.matohop, [row.mon1, row.mon2, row.mon3].filter(m => m));
            });

            const HSG_SUBJECT_MAP = {
                'toán học': 'TO', 'toán': 'TO', 'ngữ văn': 'VA', 'văn': 'VA',
                'tiếng anh': 'N1', 'vật lý': 'LI', 'vật lí': 'LI',
                'hóa học': 'HO', 'sinh học': 'SI', 'lịch sử': 'SU',
                'địa lí': 'DI', 'địa lý': 'DI', 'tin học': 'TI', 'gdcd': 'GDCD'
            };

            const KHKT_MAJOR_MATCH = ['tâm lý học', 'tâm lí học', 'xã hội học', 'quốc tế học'];

            // Tải điểm hiện tại để cộng dồn
            const [currentPoints] = await connection.query('SELECT dc_keys, diemUtxt, diemCC, ghichu FROM xt_diemcongxetuyen');
            const pointsMap = new Map();
            currentPoints.forEach(row => {
                pointsMap.set(row.dc_keys, row);
            });

            const insertValues = [];

            for (const row of allRawData) {
                if (!row.cccd || !row.mon_giai || !row.tennganh) continue;

                const candidateNvs = nvMap.get(row.cccd);
                if (!candidateNvs) continue;

                const monGiaiNorm = row.mon_giai.toLowerCase().trim();
                const dbSubjectCode = HSG_SUBJECT_MAP[monGiaiNorm];
                
                // Lọc các nguyện vọng có tên ngành khớp với file Excel (so sánh tương đối)
                const targetNvs = candidateNvs.filter(nv => {
                    if (!nv.tennganh) return false;
                    const dbName = normalizeHeader(nv.tennganh);
                    const excelName = normalizeHeader(row.tennganh);
                    return dbName.includes(excelName) || excelName.includes(dbName);
                });

                for (const nv of targetNvs) {
                    let isMatched = false;

                    if (dbSubjectCode) {
                        // Giải Văn hóa: Kiểm tra xem tổ hợp có chứa môn giải không
                        const comboSubjects = tohopMap.get(nv.matohop) || [];
                        if (comboSubjects.includes(dbSubjectCode)) {
                            isMatched = true;
                        }
                    } else if (monGiaiNorm.includes('khoa học xã hội')) {
                        // Giải KHKT: Tuyệt đối không map GDCD. Tra cứu sự phù hợp theo Ngành
                        const nvNameNorm = nv.tennganh.toLowerCase();
                        if (KHKT_MAJOR_MATCH.some(m => nvNameNorm.includes(m))) {
                            isMatched = true;
                        }
                    }

                    const diemThuongRaw = isMatched ? parseFloat(row.diem_co_mon) : parseFloat(row.diem_ko_mon);
                    const diemThuong = isNaN(diemThuongRaw) ? 0 : diemThuongRaw;

                    const dc_keys = `${nv.cccd}_${nv.manganh}_${nv.matohop}`;
                    const phuongthuc = 'THPT'; // Mặc định

                    // Lấy điểm hiện tại từ Map đã tải sẵn
                    const existing = pointsMap.get(dc_keys) || {};
                    const diemUtxt = parseFloat(existing.diemUtxt) || 0;
                    const diemCC = parseFloat(existing.diemCC) || 0;
                    const ghichu = existing.ghichu || '';

                    const diemTongRaw = diemUtxt + diemCC + diemThuong;
                    const diemTong = Math.min(3.0, diemTongRaw);
                    const newGhichu = ghichu ? `${ghichu} + Giải: ${row.mon_giai} (${diemThuong})` : `Giải: ${row.mon_giai} (${diemThuong}). Mức Trần: ${diemTong}`;

                    insertValues.push([
                        nv.cccd, nv.manganh, nv.matohop, phuongthuc, diemCC, diemUtxt, diemThuong, diemTong, newGhichu, dc_keys
                    ]);
                }
            }

            if (insertValues.length === 0) {
                return res.status(400).json({ message: "Không ghép nối được giải với nguyện vọng hợp lệ nào." });
            }

            await connection.beginTransaction();
            let successCount = 0;
            const chunkSize = 500;

            const query = `
                INSERT INTO xt_diemcongxetuyen 
                (ts_cccd, manganh, matohop, phuongthuc, diemCC, diemUtxt, diemThuong, diemTong, ghichu, dc_keys) 
                VALUES ?
                ON DUPLICATE KEY UPDATE 
                diemThuong=VALUES(diemThuong), diemTong=VALUES(diemTong), ghichu=VALUES(ghichu)
            `;

            for (let i = 0; i < insertValues.length; i += chunkSize) {
                const chunk = insertValues.slice(i, i + chunkSize);
                await connection.query(query, [chunk]);
                successCount += chunk.length;
            }

            await connection.commit();
            res.status(200).json({ 
                message: `Import thành công! Đã thêm/cập nhật điểm Giải HSG cho ${successCount} tổ hợp NV.`,
                successCount 
            });

        } catch (error) {
            if (connection) await connection.rollback();
            console.error("Lỗi Import Excel Điểm Giải HSG:", error);
            res.status(500).json({ message: "Lỗi xử lý file Excel", error: error.message });
        } finally {
            if (connection) connection.release();
        }
    }
}

module.exports = BonusController;
