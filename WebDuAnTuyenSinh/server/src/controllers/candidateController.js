const CandidateModel = require('../models/candidateModel');
// Đã xóa hàm splitFullName do người dùng nhập thẳng họ và tên riêng biệt

class CandidateController {
    static async getAllCandidates(req, res) {
        try {
            const candidates = await CandidateModel.getAll();
            const mapped = candidates.map(c => ({
                id: c.cccd,
                cccd: c.cccd,
                soBaoDanh: c.sobaodanh || '',
                ho: c.ho || '',
                ten: c.ten || '',
                ngaySinh: c.ngay_sinh || '',
                dienThoai: c.dien_thoai || '',
                gioiTinh: c.gioi_tinh || '',
                email: c.email || '',
                noiSinh: c.noi_sinh || '',
                doiTuong: c.doi_tuong || '',
                khuVuc: c.khu_vuc || ''
            }));
            res.json(mapped);
        } catch (err) {
            console.error("Lỗi khi lấy danh sách thí sinh:", err);
            res.status(500).json({ error: 'Lỗi server khi lấy dữ liệu thí sinh' });
        }
    }

    static async getCandidateById(req, res) {
        try {
            const { id } = req.params;
            const c = await CandidateModel.getById(id);
            if (!c) {
                return res.status(404).json({ error: 'Không tìm thấy thí sinh' });
            }
            res.json({
                id: c.cccd,
                cccd: c.cccd,
                soBaoDanh: c.sobaodanh || '',
                ho: c.ho || '',
                ten: c.ten || '',
                ngaySinh: c.ngay_sinh || '',
                dienThoai: c.dien_thoai || '',
                gioiTinh: c.gioi_tinh || '',
                email: c.email || '',
                noiSinh: c.noi_sinh || '',
                doiTuong: c.doi_tuong || '',
                khuVuc: c.khu_vuc || ''
            });
        } catch (err) {
            console.error("Lỗi khi lấy thí sinh theo ID:", err);
            res.status(500).json({ error: 'Lỗi server' });
        }
    }

    static async create(req, res) {
        try {
            const candidateData = req.body;
            await CandidateModel.create(candidateData);
            
            res.status(201).json({ 
                id: candidateData.cccd, 
                ...candidateData 
            });
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Lỗi tạo thí sinh' });
        }
    }

    static async update(req, res) {
        try {
            const { id } = req.params; // cccd
            const candidateData = req.body;

            await CandidateModel.update(id, candidateData);
            res.json({ id, cccd: id, ...candidateData });
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Lỗi cập nhật thí sinh' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await CandidateModel.delete(id);
            res.json({ message: 'Xoá thành công' });
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Lỗi xoá thí sinh' });
        }
    }
}

module.exports = CandidateController;
