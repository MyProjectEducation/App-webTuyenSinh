const { ScoreModel, subjectColMap } = require('../models/scoreModel');

class ScoreController {
    static async getAll(req, res) {
        try {
            const data = await ScoreModel.getAll();
            // Dữ liệu SQL đang là 1 dòng N cột. Giao diện cần mảng normalize: 1 dòng 1 điểm để hiện trên danh sách cũ.
            // Nhưng thiết kế mới cần danh sách nhóm gọn lại theo thí sinh?
            // User requirement: Bảng hiển thị xuất hiện 2 record môn? Hay lưới?
            // "Chuẩn hóa ngược (Denormalize): Chuyển 1 dòng trong CSDL thành nhiều dòng (Object) nhỏ khi trả về Frontend."
            // Vậy getAll vẫn giữ nguyên logic mảng normalized 1 dòng = 1 môn.
            let normalizedScores = [];
            
            data.forEach(row => {
                const hoTen = (row.ho || '') + ' ' + (row.ten || '');
                const loaiDiem = row.d_phuongthuc || 'THPT';
                
                // Quét qua các môn được map để kiểm tra xem có điểm > 0 không
                Object.entries(subjectColMap).forEach(([monName, colName]) => {
                    const diem = parseFloat(row[colName]);
                    if (!isNaN(diem) && diem > 0) {
                        normalizedScores.push({
                            id: `${row.cccd}_${loaiDiem}_${colName}`,
                            cccd: row.cccd,
                            hoTen: hoTen.trim() || 'Thí sinh vô danh',
                            loaiDiem: loaiDiem,
                            mon: monName,
                            colName: colName, // Frontend sẽ cần colName để biết cột nào
                            diem: diem
                        });
                    }
                });
            });

            res.json(normalizedScores);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            // Frontend gửi nguyên khối: { cccd, loaiDiem, scores: { TO: 8, LI: 7, ... } }
            const { cccd, loaiDiem, scores } = req.body;
            await ScoreModel.upsertCandidateScore(cccd, loaiDiem, scores);
            res.status(201).json({ message: "Upsert khối điểm thành công" });
        } catch (error) {
            console.error(error);
            const status = error.message.includes('không tồn tại') ? 400 : 500;
            res.status(status).json({ error: error.message || 'Lỗi khi cập nhật điểm' });
        }
    }

    static async update(req, res) {
        try {
            // Upsert gộp cả create và update nên xử lý giống nhau
            const { cccd, loaiDiem, scores } = req.body;
            await ScoreModel.upsertCandidateScore(cccd, loaiDiem, scores);
            res.json({ message: "Update khối điểm thành công" });
        } catch (error) {
             console.error(error);
             const status = error.message.includes('không tồn tại') ? 400 : 500;
             res.status(status).json({ error: error.message || 'Server error' });
        }
    }

    static async delete(req, res) {
        try {
            // Nhạn id có cấu trúc `${cccd}_${loaiDiem}_${colName}`
            const { id } = req.params;
            const parts = id.split('_');
            const colName = parts.pop();
            const loaiDiem = parts.pop();
            const cccd = parts.join('_');
            
            await ScoreModel.deleteScore(cccd, loaiDiem, colName);
            res.json({ message: 'Đã xóa điểm thành công' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }
}

module.exports = ScoreController;
