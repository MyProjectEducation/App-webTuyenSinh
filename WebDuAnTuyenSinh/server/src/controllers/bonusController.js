const BonusModel = require('../models/bonusModel');

class BonusController {
    static async getAll(req, res) {
        try {
            const data = await BonusModel.getAll();
            const mappedData = data.map(item => ({
                id: item.iddiemcong.toString(),
                cccd: item.ts_cccd,
                hoTen: ((item.ho || '') + ' ' + (item.ten || '')).trim() || 'Vô danh',
                maNganh: item.manganh || '',
                maToHop: item.matohop || '',
                phuongThuc: item.phuongthuc || '',
                diem: parseFloat(item.diemTong) || 0,
                diemC: parseFloat(item.diemCC) || 0,
                diemUt: parseFloat(item.diemUtxt) || 0,
                ghiChu: item.ghichu || ''
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            // Frontend truyền { cccd, manganh, matohop, phuongthuc, khuvuc, doituong, chungchi }
            const result = await BonusModel.upsertBonusPoint(req.body);
            res.status(201).json({ 
                message: "Upsert điểm cộng thành công!", 
                diemTong: result.diemTong,
                diemC: result.diemCC,
                diemUt: result.diemUtxt,
                ghiChu: result.ghichu
            });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Lỗi máy chủ khi tính điểm cộng' });
        }
    }

    static async update(req, res) {
        try {
            const result = await BonusModel.upsertBonusPoint(req.body);
            res.json({ 
                message: "Upsert điểm cộng thành công!", 
                diemTong: result.diemTong,
                diemC: result.diemCC,
                diemUt: result.diemUtxt,
                ghiChu: result.ghichu
            });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Lỗi máy chủ khi tính điểm cộng' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await BonusModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }
}

module.exports = BonusController;
