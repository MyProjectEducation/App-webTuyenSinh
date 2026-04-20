const AdmissionModel = require('../models/admissionModel');

class AdmissionController {
    static async getAll(req, res) {
        try {
            const data = await AdmissionModel.getAll();
            const mappedData = data.map(item => ({
                id: item.idnv.toString(),
                cccd: item.nn_cccd,
                hoTen: ((item.ho || '') + ' ' + (item.ten || '')).trim() || 'Vô danh',
                thuTuNV: parseInt(item.nv_tt) || 1,
                maNganh: item.nv_manganh || '',
                maToHop: item.tt_thm || ''
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            const result = await AdmissionModel.create(req.body);
            res.status(201).json({ id: result.insertId ? result.insertId.toString() : Date.now().toString(), ...req.body });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async update(req, res) {
        try {
            const { id } = req.params;
            await AdmissionModel.update(id, req.body);
            res.json({ id, ...req.body });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await AdmissionModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async saveResults(req, res) {
        try {
            const results = req.body; // Expect an array
            if (!Array.isArray(results)) {
                return res.status(400).json({ error: 'Invalid data format' });
            }
            await AdmissionModel.saveResults(results);
            res.json({ message: 'Saved successfully' });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }
}

module.exports = AdmissionController;
