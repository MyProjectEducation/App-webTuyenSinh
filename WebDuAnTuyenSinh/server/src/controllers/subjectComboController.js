const SubjectComboModel = require('../models/subjectComboModel');

class SubjectComboController {
    static async getAll(req, res) {
        try {
            const data = await SubjectComboModel.getAll();
            const mappedData = data.map(item => ({
                id: item.idtohop.toString(),
                maToHop: item.matohop,
                tenToHop: item.tentohop || '',
                mon1: item.mon1 || '',
                mon2: item.mon2 || '',
                mon3: item.mon3 || ''
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            const result = await SubjectComboModel.create(req.body);
            res.status(201).json({ id: result.insertId.toString(), ...req.body });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async update(req, res) {
        try {
            const { id } = req.params;
            await SubjectComboModel.update(id, req.body);
            res.json({ id, ...req.body });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await SubjectComboModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }
}

module.exports = SubjectComboController;
