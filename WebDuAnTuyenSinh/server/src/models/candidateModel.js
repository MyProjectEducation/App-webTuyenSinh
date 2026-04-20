const db = require('../../config/db');

class CandidateModel {
    static async getAll() {
        const [rows] = await db.query('SELECT * FROM xt_thisinhxettuyen25');
        return rows;
    }

    static async getById(id) {
        const [rows] = await db.query('SELECT * FROM xt_thisinhxettuyen25 WHERE idthisinh = ?', [id]);
        return rows[0];
    }
    
    static async create(candidateData) {
        const { cccd, soBaoDanh, ho, ten, ngaySinh, dienThoai, password, gioiTinh, email, noiSinh, doiTuong, khuVuc } = candidateData;
        const [result] = await db.query(
            'INSERT INTO xt_thisinhxettuyen25 (cccd, sobaodanh, ho, ten, ngay_sinh, dien_thoai, password, gioi_tinh, email, noi_sinh, doi_tuong, khu_vuc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
            [cccd, soBaoDanh, ho, ten, ngaySinh, dienThoai, password || 'P123456', gioiTinh, email, noiSinh, doiTuong, khuVuc]
        );
        return result;
    }

    static async update(cccd, candidateData) {
        const { soBaoDanh, ho, ten, ngaySinh, dienThoai, password, gioiTinh, email, noiSinh, doiTuong, khuVuc } = candidateData;
        let query = 'UPDATE xt_thisinhxettuyen25 SET sobaodanh = ?, ho = ?, ten = ?, ngay_sinh = ?, dien_thoai = ?, gioi_tinh = ?, email = ?, noi_sinh = ?, doi_tuong = ?, khu_vuc = ?, updated_at = NOW()';
        let params = [soBaoDanh, ho, ten, ngaySinh, dienThoai, gioiTinh, email, noiSinh, doiTuong, khuVuc];

        if (password) {
            query += ', password = ?';
            params.push(password);
        }

        query += ' WHERE cccd = ?';
        params.push(cccd);

        const [result] = await db.query(query, params);
        return result;
    }

    static async delete(cccd) {
        const [result] = await db.query('DELETE FROM xt_thisinhxettuyen25 WHERE cccd = ?', [cccd]);
        return result;
    }
}

module.exports = CandidateModel;
