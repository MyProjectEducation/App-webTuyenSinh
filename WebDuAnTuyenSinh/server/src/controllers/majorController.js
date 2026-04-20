const MajorModel = require('../models/majorModel');

class MajorController {
    static async getAll(req, res) {
        try {
            const data = await MajorModel.getAll();
            // Mapping sang cấu trúc của AppContext
            const mappedData = data.map(item => ({
                id: item.idnganh.toString(),
                maNganh: item.manganh,
                tenNganh: item.tennganh,
                toHopGoc: item.n_tohopgoc || '',
                chiTieu: item.n_chitieu || 0,
                diemSan: item.n_diemsan || 0,
                diemTrungTuyen: item.n_diemtrungtuyen || 0,
                tuyenThang: item.n_tuyenthang || '0',
                dgnl: item.n_dgnl || '0',
                thpt: item.n_thpt || '0',
                vsat: item.n_vsat || '0',
                slXtt: item.sl_xtt || 0,
                slDgnl: item.sl_dgnl || 0,
                slThpt: item.sl_thpt || 0,
                slVsat: item.sl_vsat || 0
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            const body = req.body;
            const result = await MajorModel.create(body);
            res.status(201).json({ id: result.insertId.toString(), ...body });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async update(req, res) {
        try {
            const { id } = req.params;
            await MajorModel.update(id, req.body);
            res.json({ id, ...req.body });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await MajorModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }
}

module.exports = MajorController;
