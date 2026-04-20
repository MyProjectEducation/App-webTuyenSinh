const MajorComboModel = require('../models/majorComboModel');

class MajorComboController {
    static async getAll(req, res) {
        try {
            const data = await MajorComboModel.getAll();
            const mappedData = data.map(item => ({
                id: item.id.toString(),
                maNganh: item.manganh,
                maToHop: item.matohop,
                thMon1: item.th_mon1,
                thMon2: item.th_mon2,
                thMon3: item.th_mon3,
                hsMon1: item.hsmon1,
                hsMon2: item.hsmon2,
                hsMon3: item.hsmon3,
                doLech: item.dolech
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            const result = await MajorComboModel.create(req.body);
            res.status(201).json({ id: result.insertId.toString(), ...req.body });
        } catch (error) {
            console.error(error);
            // Có thể lỗi duplicate key tb_keys
            res.status(400).json({ error: 'Lỗi tạo dữ liệu (Duplicate?)' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await MajorComboModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }
}

module.exports = MajorComboController;
