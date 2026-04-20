const db = require('../../config/db');

class ConversionModel {
    static async getAll() {
        const [rows] = await db.query('SELECT * FROM xt_bangquydoi ORDER BY d_phuongthuc, d_mon, d_diema');
        return rows;
    }
    
    static async create(data) {
        const { d_phuongthuc, d_maquydoi, d_mon, d_diema, d_diemb, d_diemc, d_diemd, d_phanvi } = data;
        const [result] = await db.query(
            `INSERT INTO xt_bangquydoi 
            (d_phuongthuc, d_maquydoi, d_mon, d_diema, d_diemb, d_diemc, d_diemd, d_phanvi) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
            [d_phuongthuc, d_maquydoi, d_mon || null, d_diema, d_diemb || null, d_diemc || null, d_diemd || null, d_phanvi || null]
        );
        return result;
    }

    static async update(id, data) {
        const { d_phuongthuc, d_maquydoi, d_mon, d_diema, d_diemb, d_diemc, d_diemd, d_phanvi } = data;
        const [result] = await db.query(
            `UPDATE xt_bangquydoi 
             SET d_phuongthuc = ?, d_maquydoi = ?, d_mon = ?, d_diema = ?, d_diemb = ?, d_diemc = ?, d_diemd = ?, d_phanvi = ? 
             WHERE idqd = ?`,
            [d_phuongthuc, d_maquydoi, d_mon || null, d_diema, d_diemb || null, d_diemc || null, d_diemd || null, d_phanvi || null, id]
        );
        return result;
    }

    static async delete(id) {
        const [result] = await db.query('DELETE FROM xt_bangquydoi WHERE idqd = ?', [id]);
        return result;
    }
}

module.exports = ConversionModel;
