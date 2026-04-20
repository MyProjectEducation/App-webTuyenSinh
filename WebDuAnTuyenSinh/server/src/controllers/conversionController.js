const ConversionModel = require('../models/conversionModel');

class ConversionController {
    static async getAll(req, res) {
        try {
            const data = await ConversionModel.getAll();
            const mappedData = data.map(item => ({
                id: item.idqd.toString(),
                d_phuongthuc: item.d_phuongthuc || '',
                d_maquydoi: item.d_maquydoi || '',
                d_mon: item.d_mon || '',
                d_diema: parseFloat(item.d_diema) || 0,
                d_diemb: item.d_diemb !== null ? parseFloat(item.d_diemb) : undefined,
                d_diemc: item.d_diemc !== null ? parseFloat(item.d_diemc) : undefined,
                d_diemd: item.d_diemd !== null ? parseFloat(item.d_diemd) : undefined,
                d_phanvi: item.d_phanvi || ''
            }));
            res.json(mappedData);
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async create(req, res) {
        try {
            const result = await ConversionModel.create(req.body);
            res.status(201).json({ id: result.insertId.toString(), ...req.body });
        } catch (error) {
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }

    static async update(req, res) {
        try {
            const { id } = req.params;
            await ConversionModel.update(id, req.body);
            res.json({ id, ...req.body });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }

    static async delete(req, res) {
        try {
            const { id } = req.params;
            await ConversionModel.delete(id);
            res.json({ message: 'Deleted successfully' });
        } catch (error) {
             console.error(error);
             res.status(500).json({ error: 'Server error' });
        }
    }
}

module.exports = ConversionController;
