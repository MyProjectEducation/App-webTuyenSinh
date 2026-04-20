const db = require('../../config/db');

class UserModel {
    static async findByUsername(username) {
        const [rows] = await db.query('SELECT * FROM sys_users WHERE username = ?', [username]);
        return rows[0];
    }

    static async findById(id) {
        const [rows] = await db.query('SELECT * FROM sys_users WHERE user_id = ?', [id]);
        return rows[0];
    }
}

module.exports = UserModel;
