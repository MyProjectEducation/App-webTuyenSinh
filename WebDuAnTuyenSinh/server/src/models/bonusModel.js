const db = require('../../config/db');

// Bảng tra cứu điểm Ưu tiên Khu vực (Thang 30)
const DIEM_KHU_VUC = { 'KV1': 0.75, 'KV2-NT': 0.5, 'KV2': 0.25, 'KV3': 0.0 };

// Bảng tra cứu điểm Ưu tiên Đối tượng (Thang 30)
const DIEM_DOI_TUONG = {
    'DT01': 2.0, 'DT02': 2.0, 'DT03': 2.0, 'DT04': 2.0,
    'DT05': 1.0, 'DT06': 1.0, 'DT07': 1.0, 'NONE': 0.0
};

// Bảng tra cứu điểm Chứng chỉ Ngoại ngữ (Đã quy về thang 30 tương đương THPT)
// Lưu ý: Chỉ cộng khi tổ hợp KHÔNG CÓ môn Tiếng Anh (N1)
const DIEM_CHUNG_CHI = {
    'IELTS 5.5': 1.0,
    'IELTS 6.0': 1.5,
    'IELTS 7.0': 2.0,
    'NONE': 0.0
};

class BonusModel {
    static async getAll() {
        const [rows] = await db.query(`
            SELECT dc.*, ts.ho, ts.ten 
            FROM xt_diemcongxetuyen dc
            LEFT JOIN xt_thisinhxettuyen25 ts ON dc.ts_cccd = ts.cccd
        `);
        return rows;
    }
    
    static async upsertBonusPoint(payload) {
        const { cccd, manganh, matohop, phuongthuc, khuvuc, doituong, chungchi } = payload;
        const connection = await db.getConnection();

        try {
            await connection.beginTransaction();

            // 1. Tính điểm Ưu tiên gốc (Thang 30)
            const kvScore = DIEM_KHU_VUC[khuvuc] || 0;
            const dtScore = DIEM_DOI_TUONG[doituong] || 0;
            let diemUtxt = kvScore + dtScore;

            // 3. Tính điểm Chứng chỉ
            // Truy vấn kiểm tra tổ hợp cấu hình ở bảng xt_nganh_tohop xem có Tiếng Anh (N1) không
            let hasN1 = false;
            const [comboRows] = await connection.query(
                'SELECT N1 FROM xt_nganh_tohop WHERE manganh = ? AND matohop = ?', 
                [manganh, matohop]
            );
            if (comboRows.length > 0 && comboRows[0].N1 === 1) {
                hasN1 = true;
            }

            // Nếu tổ hợp lấy Anh Văn xét thi (N1 = 1) -> Không cộng điểm CC nữa
            const diemCC = hasN1 ? 0 : (DIEM_CHUNG_CHI[chungchi] || 0);

            // 4. Tính Tổng có áp Trần (Cap 3.0)
            const maxScore = 3.0; // Trần mặc định thang 30
            const diemTongRaw = diemUtxt + diemCC;
            const diemTong = Math.min(maxScore, diemTongRaw);

            // 5. Sinh chuỗi Ghi chú
            const noteCC = hasN1 ? 'CC: 0 (Đã dùng N1)' : `CC: ${chungchi} (${diemCC})`;
            const ghichu = `KV: ${khuvuc} (${kvScore}) + ĐT: ${doituong} (${dtScore}) + ${noteCC}. Mức Trần: ${diemTong}`;

            // 6. Sinh Khóa và Upsert
            const dc_keys = `${cccd}_${manganh}_${matohop}`;
            
            const query = `
                INSERT INTO xt_diemcongxetuyen 
                (ts_cccd, manganh, matohop, phuongthuc, diemCC, diemUtxt, diemTong, ghichu, dc_keys) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE 
                phuongthuc=VALUES(phuongthuc), diemCC=VALUES(diemCC), diemUtxt=VALUES(diemUtxt), 
                diemTong=VALUES(diemTong), ghichu=VALUES(ghichu)
            `;

            await connection.query(query, [
                cccd, manganh, matohop, phuongthuc, diemCC, diemUtxt, diemTong, ghichu, dc_keys
            ]);

            await connection.commit();
            return { success: true, diemTong, diemCC, diemUtxt, ghichu, dc_keys };
        } catch (error) {
            await connection.rollback();
            throw error;
        } finally {
            connection.release();
        }
    }

    static async delete(id) {
        const [result] = await db.query('DELETE FROM xt_diemcongxetuyen WHERE iddiemcong = ?', [id]);
        return result;
    }
}

module.exports = BonusModel;
