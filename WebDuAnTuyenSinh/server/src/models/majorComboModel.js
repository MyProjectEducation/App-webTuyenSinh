const db = require('../../config/db');

class MajorComboModel {
    static async getAll() {
        const [rows] = await db.query('SELECT * FROM xt_nganh_tohop');
        return rows;
    }
    
    static async create(data) {
        const { maNganh, maToHop } = data;
        const connection = await db.getConnection();
        
        try {
            await connection.beginTransaction();

            // 1. Truy xuất thông tin 3 môn từ bảng danh mục gốc
            const [comboRows] = await connection.query(
                'SELECT mon1, mon2, mon3 FROM xt_tohop_monthi WHERE matohop = ?', 
                [maToHop]
            );
            
            if (comboRows.length === 0) throw new Error("Mã tổ hợp không tồn tại!");
            const { mon1, mon2, mon3 } = comboRows[0];

            // 2. Khởi tạo mảng gán giá trị Boolean (0) cho tất cả các môn
            const ALL_SUBJECT_COLUMNS = ['N1', 'TO', 'LI', 'HO', 'SI', 'VA', 'SU', 'DI', 'TI', 'KHAC', 'KTPL'];
            let subjectFlags = {};
            ALL_SUBJECT_COLUMNS.forEach(col => subjectFlags[col] = 0);

            // 3. Bật cờ (1) cho 3 môn có trong tổ hợp
            if (subjectFlags[mon1] !== undefined) subjectFlags[mon1] = 1;
            if (subjectFlags[mon2] !== undefined) subjectFlags[mon2] = 1;
            if (subjectFlags[mon3] !== undefined) subjectFlags[mon3] = 1;

            // 4. Sinh khóa tb_keys
            const tb_keys = `${maNganh}_${maToHop}`;

            // 5. Insert vào Database với đầy đủ các cột tự động điền
            const insertQuery = `
                INSERT INTO xt_nganh_tohop 
                (manganh, matohop, th_mon1, th_mon2, th_mon3, hsmon1, hsmon2, hsmon3, dolech, tb_keys, N1, TO, LI, HO, SI, VA, SU, DI, TI, KHAC, KTPL) 
                VALUES (?, ?, ?, ?, ?, 1, 1, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE 
                    th_mon1=VALUES(th_mon1), th_mon2=VALUES(th_mon2), th_mon3=VALUES(th_mon3), dolech=VALUES(dolech),
                    N1=VALUES(N1), \`TO\`=VALUES(\`TO\`), LI=VALUES(LI), HO=VALUES(HO), SI=VALUES(SI), VA=VALUES(VA),
                    SU=VALUES(SU), DI=VALUES(DI), TI=VALUES(TI), KHAC=VALUES(KHAC), KTPL=VALUES(KTPL)
            `;

            // Fix SQL reserved keyword TO by wrapping it in backticks if necessary, 
            // query string allows `TO` as long as it's not a syntax keyword position, but it's safer with backticks:
            const safeInsertQuery = insertQuery.replace(/ TO,/g, ' `TO`,').replace(/TO=/g, '`TO`=');
            
            const dolechVal = data.doLech !== undefined ? data.doLech : 0;
            const [result] = await connection.query(safeInsertQuery, [
                maNganh, maToHop, mon1, mon2, mon3, dolechVal, tb_keys,
                subjectFlags['N1'], subjectFlags['TO'], subjectFlags['LI'], subjectFlags['HO'], 
                subjectFlags['SI'], subjectFlags['VA'], subjectFlags['SU'], subjectFlags['DI'], 
                subjectFlags['TI'], subjectFlags['KHAC'], subjectFlags['KTPL']
            ]);
            
            await connection.commit();
            return result;
        } catch (error) {
            await connection.rollback();
            throw error;
        } finally {
            connection.release();
        }
    }

    static async delete(id) {
        const [result] = await db.query('DELETE FROM xt_nganh_tohop WHERE id = ?', [id]);
        return result;
    }
}

module.exports = MajorComboModel;
