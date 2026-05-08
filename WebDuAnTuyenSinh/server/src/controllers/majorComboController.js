const MajorComboModel = require('../models/majorComboModel');
const xlsx = require('xlsx');
const db = require('../../config/db');

// Ma trận độ lệch (Trích xuất từ Phase 4)
const DEVIATION_MATRIX = {
    'A00': { 'A00': 0, 'A01': -0.69, 'B00': -1.21, 'C00': 2.32, 'C01': 0.94, 'D01': -0.68, 'D07': -1.62 },
    'A01': { 'A00': 0.69, 'A01': 0, 'B00': -0.52, 'C00': 3.01, 'C01': 1.63, 'D01': 0.01, 'D07': -0.93 },
    'B00': { 'A00': 1.21, 'A01': 0.52, 'B00': 0, 'C00': 3.53, 'C01': 2.15, 'D01': 0.53, 'D07': -0.41 },
    'C00': { 'A00': -2.32, 'A01': -3.01, 'B00': -3.53, 'C00': 0, 'C01': -1.38, 'D01': -3.00, 'D07': -3.94 },
    'C01': { 'A00': -0.94, 'A01': -1.63, 'B00': -2.15, 'C00': 1.38, 'C01': 0, 'D01': -1.62, 'D07': -2.56 },
    'D01': { 'A00': 0.68, 'A01': -0.01, 'B00': -0.53, 'C00': 3.00, 'C01': 1.62, 'D01': 0, 'D07': -0.94 }
};

const EXCEL_MAPPING = {
    'manganh': 'manganh',
    'mã ngành': 'manganh',
    'matohop': 'matohop',
    'ma_to_hop': 'matohop',
    'mã tổ hợp': 'matohop',
    'goc': 'is_goc',
    'gốc': 'is_goc',
    'tổ hợp gốc': 'is_goc'
};

const normalizeHeader = (header) => {
    if (!header) return '';
    return header.toString().toLowerCase().trim();
};

const isBaseCombo = (val) => {
    if (!val) return false;
    const str = val.toString().toLowerCase().trim();
    return ['1', 'x', 'v', 'có', 'co', 'true', 'yes'].includes(str);
};

class MajorComboController {
    static async getAll(req, res) {
        try {
            const data = await MajorComboModel.getAll();
            const mappedData = data.map(item => ({
                id: item.id.toString(),
                maNganh: item.manganh,
                maToHop: item.matohop,
                thMon1: item.th_mon1,
                thMon2: item.th_mon2,
                thMon3: item.th_mon3,
                hsMon1: item.hsmon1,
                hsMon2: item.hsmon2,
                hsMon3: item.hsmon3,
                doLech: item.dolech
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            const result = await MajorComboModel.create(req.body);
            res.status(201).json({ id: result.insertId.toString(), ...req.body });
        } catch (error) {
            console.error(error);
            // Có thể lỗi duplicate key tb_keys
            res.status(400).json({ error: 'Lỗi tạo dữ liệu (Duplicate?)' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await MajorComboModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async importExcel(req, res) {
        if (!req.file) return res.status(400).json({ message: "Vui lòng tải lên file Excel" });

        const connection = await db.getConnection();
        try {
            const workbook = xlsx.read(req.file.buffer, { type: 'buffer' });
            const sheetName = workbook.SheetNames[0];
            const rawData = xlsx.utils.sheet_to_json(workbook.Sheets[sheetName]);

            await connection.beginTransaction();
            let successCount = 0;

            const [nganhRows] = await connection.query('SELECT manganh, n_tohopgoc FROM xt_nganh');
            const nganhMap = nganhRows.reduce((acc, row) => {
                acc[row.manganh] = row.n_tohopgoc;
                return acc;
            }, {});

            for (const row of rawData) {
                let comboData = {};

                for (const [excelHeader, value] of Object.entries(row)) {
                    const dbCol = EXCEL_MAPPING[normalizeHeader(excelHeader)];
                    if (dbCol) comboData[dbCol] = value;
                }

                if (!comboData.manganh || !comboData.matohop) continue;

                const maNganh = comboData.manganh.toString().trim();
                let rawMaToHop = comboData.matohop.toString().trim();
                // If it looks like 'B03(TO-3,VA-3,SI-1)', extract just 'B03'
                const maToHop = rawMaToHop.split('(')[0].trim();
                
                const isGoc = isBaseCombo(comboData.is_goc);

                if (isGoc) {
                    await connection.query(
                        'UPDATE xt_nganh SET n_tohopgoc = ? WHERE manganh = ?', 
                        [maToHop, maNganh]
                    );
                    nganhMap[maNganh] = maToHop; 
                }

                let doLech = 0.0;
                const toHopGocCuaNganh = nganhMap[maNganh];
                
                if (toHopGocCuaNganh && toHopGocCuaNganh !== maToHop) {
                    if (DEVIATION_MATRIX[toHopGocCuaNganh] && DEVIATION_MATRIX[toHopGocCuaNganh][maToHop] !== undefined) {
                        doLech = DEVIATION_MATRIX[toHopGocCuaNganh][maToHop];
                    }
                }

                const tb_keys = `${maNganh}_${maToHop}`;

                // Extract môn học flags mapping (this could be enhanced later, assuming default mapping based on combo config)
                // For now, let's look up tohop_monthi config to fill in th_mon1, th_mon2, th_mon3
                const [thRows] = await connection.query('SELECT mon1, mon2, mon3 FROM xt_tohop_monthi WHERE matohop = ?', [maToHop]);
                let mon1 = null, mon2 = null, mon3 = null;
                if (thRows.length > 0) {
                    mon1 = thRows[0].mon1;
                    mon2 = thRows[0].mon2;
                    mon3 = thRows[0].mon3;
                }

                const query = `
                    INSERT INTO xt_nganh_tohop (manganh, matohop, dolech, tb_keys, th_mon1, th_mon2, th_mon3) 
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE dolech=VALUES(dolech), th_mon1=VALUES(th_mon1), th_mon2=VALUES(th_mon2), th_mon3=VALUES(th_mon3)
                `;
                await connection.query(query, [maNganh, maToHop, doLech, tb_keys, mon1, mon2, mon3]);
                
                successCount++;
            }

            await connection.commit();
            res.status(200).json({ 
                message: `Import thành công! Đã xử lý ${successCount} liên kết Ngành - Tổ hợp.`,
                successCount 
            });

        } catch (error) {
            await connection.rollback();
            console.error("Lỗi Import Ngành - Tổ hợp:", error);
            res.status(500).json({ message: "Lỗi xử lý file Excel", error: error.message });
        } finally {
            if (connection) connection.release();
        }
    }
}

module.exports = MajorComboController;
