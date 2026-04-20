const db = require('../../config/db');

// Ánh xạ từ Frontend (mon) sang Cột SQL dùng cho getAll() (Normalized view)
const subjectColMap = {
    'Toán': 'TO',
    'Lý': 'LI',
    'Hóa': 'HO',
    'Sinh': 'SI',
    'Sử': 'SU',
    'Địa': 'DI',
    'Văn': 'VA',
    'Ngoại ngữ': 'N1_THI',
    'Chứng chỉ NN': 'N1_CC',
    'Công nghệ CN': 'CNCN',
    'Công nghệ NN': 'CNNN',
    'Tin học': 'TI',
    'GD KTPL': 'KTPL',
    'Năng lực': 'NL1',
    'Năng khiếu 1': 'NK1',
    'Năng khiếu 2': 'NK2'
};

class ScoreModel {
    static async getAll() {
        const [rows] = await db.query(`
            SELECT dt.*, ts.ho, ts.ten 
            FROM xt_diemthixettuyen dt
            LEFT JOIN xt_thisinhxettuyen25 ts ON dt.cccd = ts.cccd
        `);
        return rows;
    }
    
    static async upsertCandidateScore(cccd, d_phuongthuc, scores) {
        const connection = await db.getConnection();
        try {
            await connection.beginTransaction();

            // Kiểm tra thí sinh tồn tại không
            const [candidateRows] = await connection.query('SELECT cccd FROM xt_thisinhxettuyen25 WHERE cccd = ?', [cccd]);
            if (candidateRows.length === 0) {
                throw new Error(`Thí sinh với CCCD ${cccd} chưa có hoặc không tồn tại. Vui lòng thêm trong Quản lý Thí sinh!`);
            }

            // Khởi tạo tất cả 15 cột môn học về giá trị mặc định (NULL)
            const allSubjects = {
                TO: null, LI: null, HO: null, SI: null, SU: null, DI: null, VA: null,
                N1_THI: null, N1_CC: null, CNCN: null, CNNN: null, TI: null, KTPL: null,
                NL1: null, NK1: null, NK2: null
            };

            // Ghi đè các điểm được gửi từ Frontend (scores) vào allSubjects
            const finalScores = { ...allSubjects, ...scores };

            const query = `
                INSERT INTO xt_diemthixettuyen 
                (cccd, d_phuongthuc, \`TO\`, LI, HO, SI, SU, DI, VA, N1_THI, N1_CC, CNCN, CNNN, TI, KTPL, NL1, NK1, NK2) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE 
                d_phuongthuc=VALUES(d_phuongthuc),
                \`TO\`=VALUES(\`TO\`), LI=VALUES(LI), HO=VALUES(HO), SI=VALUES(SI), SU=VALUES(SU), 
                DI=VALUES(DI), VA=VALUES(VA), N1_THI=VALUES(N1_THI), N1_CC=VALUES(N1_CC), 
                CNCN=VALUES(CNCN), CNNN=VALUES(CNNN), TI=VALUES(TI), KTPL=VALUES(KTPL), 
                NL1=VALUES(NL1), NK1=VALUES(NK1), NK2=VALUES(NK2)
            `;

            await connection.query(query, [
                cccd, d_phuongthuc,
                finalScores.TO, finalScores.LI, finalScores.HO, finalScores.SI, finalScores.SU,
                finalScores.DI, finalScores.VA, finalScores.N1_THI, finalScores.N1_CC,
                finalScores.CNCN, finalScores.CNNN, finalScores.TI, finalScores.KTPL,
                finalScores.NL1, finalScores.NK1, finalScores.NK2
            ]);

            await connection.commit();
            return true;
        } catch (error) {
            await connection.rollback();
            throw error;
        } finally {
            connection.release();
        }
    }

    static async deleteScore(cccd, d_phuongthuc, monCol) {
        // monCol is dynamic, reset the specific column to NULL instead of 0
        const [result] = await db.query(`UPDATE xt_diemthixettuyen SET \`${monCol}\` = NULL WHERE cccd = ? AND d_phuongthuc = ?`, [cccd, d_phuongthuc]);
        return result;
    }
}

module.exports = { ScoreModel, subjectColMap };
