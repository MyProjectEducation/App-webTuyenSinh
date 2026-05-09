const AdmissionModel = require('../models/admissionModel');
const xlsx = require('xlsx');
const db = require('../../config/db');
const { smartMap } = require('../utils/excelUtils');

const PREF_MAPPING = {
    'cccd': 'cccd',
    'socccd': 'cccd',
    'cancuoccongdan': 'cccd',
    'thutunv': 'thuTuNV',
    'thutunguyenvong': 'thuTuNV',
    'nvtt': 'thuTuNV',
    'maxettuyen': 'maNganh',
    'manganh': 'maNganh',
    'nvmn': 'maNganh',
    'phuongthuc': 'phuongThuc',
    'phuongthucxettuyen': 'phuongThuc'
};

class AdmissionController {
    static async getAll(req, res) {
        try {
            const data = await AdmissionModel.getAll();
            const mappedData = data.map(item => ({
                id: item.idnv.toString(),
                cccd: item.nn_cccd,
                hoTen: ((item.ho || '') + ' ' + (item.ten || '')).trim() || 'Vô danh',
                thuTuNV: parseInt(item.nv_tt) || 1,
                maNganh: item.nv_manganh || '',
                maToHop: item.tt_thm || ''
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            const result = await AdmissionModel.create(req.body);
            res.status(201).json({ id: result.insertId ? result.insertId.toString() : Date.now().toString(), ...req.body });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async update(req, res) {
        try {
            const { id } = req.params;
            await AdmissionModel.update(id, req.body);
            res.json({ id, ...req.body });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await AdmissionModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async saveResults(req, res) {
        try {
            const results = req.body; // Expect an array
            if (!Array.isArray(results)) {
                return res.status(400).json({ error: 'Invalid data format' });
            }
            await AdmissionModel.saveResults(results);
            res.json({ message: 'Saved successfully' });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async importPreferences(req, res) {
        if (!req.file) return res.status(400).json({ message: "Vui lòng tải lên file Excel" });

        const connection = await db.getConnection();
        try {
            const workbook = xlsx.read(req.file.buffer, { type: 'buffer' });
            let allRawData = [];

            workbook.SheetNames.forEach(name => {
                const sheet = workbook.Sheets[name];
                // Thử tìm header ở các dòng đầu tiên (0-10)
                // Nếu sheet_to_json mặc định không ra đúng, ta có thể dùng header: 1 để lấy mảng và tìm dòng chứa 'CCCD'
                const json = xlsx.utils.sheet_to_json(sheet, { header: 1 });
                let headerRowIndex = -1;
                
                for(let i = 0; i < Math.min(json.length, 10); i++) {
                    const row = json[i];
                    if (Array.isArray(row) && row.some(cell => cell && cell.toString().includes('CCCD'))) {
                        headerRowIndex = i;
                        break;
                    }
                }

                let data;
                if (headerRowIndex !== -1) {
                    data = xlsx.utils.sheet_to_json(sheet, { range: headerRowIndex });
                } else {
                    data = xlsx.utils.sheet_to_json(sheet);
                }

                if (data.length > 0) {
                    allRawData = allRawData.concat(data);
                }
            });

            if (allRawData.length === 0) {
                return res.status(400).json({ message: "File Excel không có dữ liệu hoặc không tìm thấy cột 'CCCD'" });
            }

            await connection.beginTransaction();

            // 1. Tải danh sách Ngành lên RAM để tra cứu Tổ hợp gốc
            const [nganhRows] = await connection.query('SELECT manganh, n_tohopgoc FROM xt_nganh');
            const nganhMap = nganhRows.reduce((acc, row) => {
                acc[row.manganh] = row.n_tohopgoc || 'A00';
                return acc;
            }, {});

            const bulkValues = [];

            for (const row of allRawData) {
                const { cleanRow: prefData } = smartMap(row, PREF_MAPPING);
                
                const cccd = prefData.cccd;
                const thuTuNV = prefData.thuTuNV;
                const maNganh = prefData.maNganh;
                const phuongThuc = prefData.phuongThuc || 'THPT'; 

                if (!cccd || !maNganh || !thuTuNV) continue;

                const cleanCccd = cccd.toString().trim();
                const cleanMaNganh = maNganh.toString().trim();
                const cleanThuTuNV = parseInt(thuTuNV, 10);

                // 2. LOGIC TỰ ĐỘNG GÁN TỔ HỢP MẶC ĐỊNH
                const toHopMacDinh = nganhMap[cleanMaNganh] || 'A00';
                const nv_keys = `${cleanCccd}_${cleanThuTuNV}`;

                bulkValues.push([
                    cleanCccd, 
                    cleanThuTuNV, 
                    cleanMaNganh, 
                    toHopMacDinh, 
                    nv_keys
                ]);
            }

            // 3. BULK UPSERT
            if (bulkValues.length > 0) {
                const chunkSize = 500;
                let successCount = 0;

                for (let i = 0; i < bulkValues.length; i += chunkSize) {
                    const chunk = bulkValues.slice(i, i + chunkSize);
                    const query = `
                        INSERT INTO xt_nguyenvongxettuyen (nn_cccd, nv_tt, nv_manganh, tt_thm, nv_keys)
                        VALUES ?
                        ON DUPLICATE KEY UPDATE
                        nv_manganh = VALUES(nv_manganh),
                        tt_thm = VALUES(tt_thm),
                        nv_keys = VALUES(nv_keys)
                    `;
                    await connection.query(query, [chunk]);
                    successCount += chunk.length;
                }

                await connection.commit();
                res.status(200).json({ message: `Import thành công! Đã xử lý ${successCount} nguyện vọng.` });
            } else {
                await connection.rollback();
                res.status(400).json({ message: "Không tìm thấy dữ liệu hợp lệ (Cần CCCD, Thứ tự NV, Mã ngành)." });
            }
        } catch (error) {
            if (connection) await connection.rollback();
            console.error("Lỗi Import Excel Preferences:", error);
            res.status(500).json({ message: "Lỗi xử lý file Excel", error: error.message });
        } finally {
            if (connection) connection.release();
        }
    }
}

module.exports = AdmissionController;
