const db = require('../../config/db');

class MajorModel {
    static async getAll() {
        const [rows] = await db.query('SELECT * FROM xt_nganh');
        return rows;
    }
    
    static async create(data) {
        const { maNganh, tenNganh, toHopGoc, chiTieu, diemSan, diemTrungTuyen, tuyenThang, dgnl, thpt, vsat, slXtt, slDgnl, slThpt, slVsat } = data;
        const [result] = await db.query(
            `INSERT INTO xt_nganh 
            (manganh, tennganh, n_tohopgoc, n_chitieu, n_diemsan, n_diemtrungtuyen, n_tuyenthang, n_dgnl, n_thpt, n_vsat, sl_xtt, sl_dgnl, sl_thpt, sl_vsat) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
            [maNganh, tenNganh, toHopGoc || null, chiTieu || 0, diemSan || null, diemTrungTuyen || null, tuyenThang || '0', dgnl || '0', thpt || '0', vsat || '0', slXtt || 0, slDgnl || 0, slThpt || 0, slVsat || 0]
        );
        return result;
    }

    static async update(id, data) {
        const { maNganh, tenNganh, toHopGoc, chiTieu, diemSan, diemTrungTuyen, tuyenThang, dgnl, thpt, vsat, slXtt, slDgnl, slThpt, slVsat } = data;
        const [result] = await db.query(
            `UPDATE xt_nganh 
            SET manganh = ?, tennganh = ?, n_tohopgoc = ?, n_chitieu = ?, n_diemsan = ?, n_diemtrungtuyen = ?, n_tuyenthang = ?, n_dgnl = ?, n_thpt = ?, n_vsat = ?, sl_xtt = ?, sl_dgnl = ?, sl_thpt = ?, sl_vsat = ? 
            WHERE idnganh = ?`,
            [maNganh, tenNganh, toHopGoc || null, chiTieu, diemSan, diemTrungTuyen, tuyenThang, dgnl, thpt, vsat, slXtt, slDgnl, slThpt, slVsat, id]
        );
        return result;
    }

    static async delete(id) {
        const [result] = await db.query('DELETE FROM xt_nganh WHERE idnganh = ?', [id]);
        return result;
    }
}

module.exports = MajorModel;
