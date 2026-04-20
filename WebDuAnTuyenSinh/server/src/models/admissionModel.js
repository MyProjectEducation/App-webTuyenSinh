const db = require('../../config/db');

class AdmissionModel {
    static async getAll() {
        const [rows] = await db.query(`
            SELECT nv.*, ts.ho, ts.ten 
            FROM xt_nguyenvongxettuyen nv
            LEFT JOIN xt_thisinhxettuyen25 ts ON nv.nn_cccd = ts.cccd
        `);
        return rows;
    }
    
    static async create(data) {
        const { cccd, thuTuNV, maNganh, maToHop } = data;
        const nv_keys = `${cccd}_${thuTuNV}`;
        const [result] = await db.query(
            'INSERT INTO xt_nguyenvongxettuyen (nn_cccd, nv_tt, nv_manganh, tt_thm, nv_keys) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE nv_manganh=?, tt_thm=?',
            [cccd, thuTuNV, maNganh, maToHop, nv_keys, maNganh, maToHop]
        );
        return result;
    }

    static async update(id, data) {
        const { cccd, thuTuNV, maNganh, maToHop } = data;
        const nv_keys = `${cccd}_${thuTuNV}`;
        const [result] = await db.query(
            'UPDATE xt_nguyenvongxettuyen SET nv_tt=?, nv_manganh=?, tt_thm=?, nv_keys=? WHERE idnv=?',
            [thuTuNV, maNganh, maToHop, nv_keys, id]
        );
        return result;
    }

    static async delete(id) {
        const [result] = await db.query('DELETE FROM xt_nguyenvongxettuyen WHERE idnv = ?', [id]);
        return result;
    }

    static async saveResults(results) {
        // results is an array: [{ cccd, maNganh, ketQua, diemTotal }...]
        const connection = await db.getConnection();
        try {
            await connection.beginTransaction();
            for (const r of results) {
                // Update kết quả xét tuyển và các thuộc tính điểm
                await connection.query(
                    'UPDATE xt_nguyenvongxettuyen SET nv_ketqua = ?, diem_thxt = ?, diem_cong = ?, diem_xettuyen = ? WHERE nn_cccd = ? AND nv_manganh = ?',
                    [r.ketQua, r.diemThi, r.diemCong, r.tongDiem, r.cccd, r.maNganh]
                );
            }
            await connection.commit();
            return { success: true };
        } catch (error) {
            await connection.rollback();
            throw error;
        } finally {
            connection.release();
        }
    }
}

module.exports = AdmissionModel;
