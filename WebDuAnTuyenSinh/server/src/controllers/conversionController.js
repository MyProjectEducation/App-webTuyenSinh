const ConversionModel = require('../models/conversionModel');
const xlsx = require('xlsx');
const db = require('../../config/db');
const { smartMap } = require('../utils/excelUtils');

class ConversionController {
    static async getAll(req, res) {
        try {
            const data = await ConversionModel.getAll();
            const mappedData = data.map(item => ({
                id: item.idqd.toString(),
                d_phuongthuc: item.d_phuongthuc || '',
                d_maquydoi: item.d_maquydoi || '',
                d_mon: item.d_mon || '',
                d_diema: parseFloat(item.d_diema) || 0,
                d_diemb: item.d_diemb !== null ? parseFloat(item.d_diemb) : undefined,
                d_diemc: item.d_diemc !== null ? parseFloat(item.d_diemc) : undefined,
                d_diemd: item.d_diemd !== null ? parseFloat(item.d_diemd) : undefined,
                d_phanvi: item.d_phanvi || ''
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            const result = await ConversionModel.create(req.body);
            res.status(201).json({ id: result.insertId.toString(), ...req.body });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async update(req, res) {
        try {
            const { id } = req.params;
            await ConversionModel.update(id, req.body);
            res.json({ id, ...req.body });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await ConversionModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async importConversions(req, res) {
        if (!req.file) return res.status(400).json({ message: "Vui lòng tải lên file Excel" });

        const connection = await db.getConnection();

        try {
            const workbook = xlsx.read(req.file.buffer, { type: 'buffer' });
            const sheetName = workbook.SheetNames[0];
            const rawData = xlsx.utils.sheet_to_json(workbook.Sheets[sheetName]);

            if (rawData.length === 0) {
                return res.status(400).json({ message: "File Excel không có dữ liệu" });
            }

            const EXCEL_MAPPING = {
                'maquydoi': 'd_maquydoi',
                'phuongthuc': 'd_phuongthuc',
                'mon': 'd_mon',
                'diema': 'd_diema',
                'diemb': 'd_diemb',
                'diemc': 'd_diemc',
                'diemd': 'd_diemd',
                'phanvi': 'd_phanvi'
            };

            await connection.beginTransaction();
            let successCount = 0;

            for (const row of rawData) {
                const { cleanRow: payload } = smartMap(row, EXCEL_MAPPING);

                if (!payload.d_maquydoi || !payload.d_phuongthuc) continue;

                const diema = parseFloat(payload.d_diema) || 0;
                const diemb = payload.d_diemb !== undefined && payload.d_diemb !== '' ? parseFloat(payload.d_diemb) : null;
                const diemc = payload.d_diemc !== undefined && payload.d_diemc !== '' ? parseFloat(payload.d_diemc) : null;
                const diemd = payload.d_diemd !== undefined && payload.d_diemd !== '' ? parseFloat(payload.d_diemd) : null;

                const [existing] = await connection.query('SELECT idqd FROM xt_bangquydoi WHERE d_maquydoi = ?', [payload.d_maquydoi]);

                if (existing.length > 0) {
                    await connection.query(
                        `UPDATE xt_bangquydoi 
                         SET d_phuongthuc = ?, d_mon = ?, d_diema = ?, d_diemb = ?, d_diemc = ?, d_diemd = ?, d_phanvi = ? 
                         WHERE d_maquydoi = ?`,
                        [payload.d_phuongthuc, payload.d_mon || null, diema, diemb, diemc, diemd, payload.d_phanvi || null, payload.d_maquydoi]
                    );
                } else {
                    await connection.query(
                        `INSERT INTO xt_bangquydoi 
                        (d_phuongthuc, d_maquydoi, d_mon, d_diema, d_diemb, d_diemc, d_diemd, d_phanvi) 
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
                        [payload.d_phuongthuc, payload.d_maquydoi, payload.d_mon || null, diema, diemb, diemc, diemd, payload.d_phanvi || null]
                    );
                }
                successCount++;
            }

            await connection.commit();
            res.status(200).json({ 
                message: `Import thành công! Đã thêm/cập nhật ${successCount} quy tắc quy đổi.`,
                successCount 
            });

        } catch (error) {
            await connection.rollback();
            console.error("Lỗi Import Excel Bảng quy đổi:", error);
            res.status(500).json({ message: "Lỗi xử lý file Excel", error: error.message });
        } finally {
            connection.release();
        }
    }
}

module.exports = ConversionController;
