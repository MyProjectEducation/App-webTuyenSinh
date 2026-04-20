const db = require('../../config/db');

class SubjectComboModel {
    static async getAll() {
        const [rows] = await db.query('SELECT * FROM xt_tohop_monthi');
        return rows;
    }
    
    static async create(data) {
        const { maToHop, tenToHop, mon1, mon2, mon3 } = data;
        const [result] = await db.query(
            'INSERT INTO xt_tohop_monthi (matohop, tentohop, mon1, mon2, mon3) VALUES (?, ?, ?, ?, ?)',
            [maToHop, tenToHop, mon1, mon2, mon3]
        );
        return result;
    }

    static async update(id, data) {
        const { maToHop, tenToHop, mon1, mon2, mon3 } = data;
        const [result] = await db.query(
            'UPDATE xt_tohop_monthi SET matohop=?, tentohop=?, mon1=?, mon2=?, mon3=? WHERE idtohop=?',
            [maToHop, tenToHop, mon1, mon2, mon3, id]
        );
        return result;
    }

    static async delete(id) {
        const [result] = await db.query('DELETE FROM xt_tohop_monthi WHERE idtohop = ?', [id]);
        return result;
    }
}

module.exports = SubjectComboModel;
