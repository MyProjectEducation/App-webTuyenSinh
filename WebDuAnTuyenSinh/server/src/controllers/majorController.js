const MajorModel = require('../models/majorModel');
const xlsx = require('xlsx');
const db = require('../../config/db');
const { smartMap } = require('../utils/excelUtils');

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

    static async importMajors(req, res) {
        if (!req.file) return res.status(400).json({ message: "Vui lòng tải lên file Excel" });

        const connection = await db.getConnection();

        try {
            const workbook = xlsx.read(req.file.buffer, { type: 'buffer' });
            const EXCEL_MAPPING = {
                'mactdt': 'manganh',
                'maxettuyen': 'manganh',
                'manganh': 'manganh',
                'manganhdaotao': 'manganh',
                'machuongtrinhdaotao': 'manganh',
                'tenctdt': 'tennganh',
                'tennganh': 'tennganh',
                'tennganhchuan': 'tennganh',
                'tohopgoc': 'n_tohopgoc',
                'tentohop': 'tohopmon',
                'matohop': 'tohopmon',
                'tohopmon': 'tohopmon',
                'tohop': 'tohopmon',
                'goc': 'is_goc',
                'chitieuchot': 'n_chitieu',
                'chitieu': 'n_chitieu',
                'nguongdauvao': 'n_diemsan',
                'diemsan': 'n_diemsan',
                'diemchuan': 'n_diemtrungtuyen',
                'diemtrungtuyen': 'n_diemtrungtuyen',
                'tuyenthang': 'n_tuyenthang',
                'xettuyenthang': 'n_tuyenthang',
                'dgnl': 'n_dgnl',
                'diemdgnl': 'n_dgnl',
                'thpt': 'n_thpt',
                'diemthpt': 'n_thpt',
                'vsat': 'n_vsat',
                'diemvsat': 'n_vsat',
                'sltuyenthang': 'sl_xtt',
                'slxtt': 'sl_xtt',
                'sldgnl': 'sl_dgnl',
                'slvsat': 'sl_vsat',
                'slthpt': 'sl_thpt'
            };

            // Hàm chuẩn hóa chuỗi tiêu đề giống excelUtils
            const normalizeHeader = (headerName) => {
                if (!headerName) return '';
                return headerName.toString().toLowerCase()
                    .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
                    .replace(/[đđ]/g, 'd').replace(/[^a-z0-9]/g, '');
            };

            let allRawData = [];

            // Quét tất cả các Sheet trong file Excel và TỰ ĐỘNG TÌM DÒNG TIÊU ĐỀ
            workbook.SheetNames.forEach(name => {
                const sheetRows = xlsx.utils.sheet_to_json(workbook.Sheets[name], { header: 1 }); // Đọc theo mảng 2 chiều
                let headerRowIndex = -1;
                let recognizedHeaders = [];

                // Quét 20 dòng đầu tiên để tìm dòng nào chứa tiêu đề (dòng có ít nhất 2 cột khớp với từ điển)
                for (let i = 0; i < Math.min(sheetRows.length, 20); i++) {
                    const row = sheetRows[i];
                    if (!Array.isArray(row)) continue;

                    let matchCount = 0;
                    let tempHeaders = [];
                    for (let j = 0; j < row.length; j++) {
                        const cellStr = row[j] !== undefined && row[j] !== null ? String(row[j]) : '';
                        const norm = normalizeHeader(cellStr);
                        tempHeaders.push(norm);
                        if (EXCEL_MAPPING[norm]) {
                            matchCount++;
                        }
                    }

                    if (matchCount >= 2) {
                        headerRowIndex = i;
                        recognizedHeaders = tempHeaders;
                        break; // Đã tìm thấy dòng tiêu đề
                    }
                }

                // Nếu tìm thấy dòng tiêu đề, tiến hành map các dòng dữ liệu bên dưới
                if (headerRowIndex !== -1) {
                    for (let i = headerRowIndex + 1; i < sheetRows.length; i++) {
                        const row = sheetRows[i];
                        if (!Array.isArray(row) || row.length === 0) continue;

                        let cleanRow = {};
                        for (let j = 0; j < recognizedHeaders.length; j++) {
                            const dbCol = EXCEL_MAPPING[recognizedHeaders[j]];
                            // Chỉ lấy những cột có giá trị
                            if (dbCol && row[j] !== undefined && row[j] !== null && row[j] !== '') {
                                cleanRow[dbCol] = String(row[j]).trim();
                            }
                        }

                        if (Object.keys(cleanRow).length > 0) {
                            allRawData.push(cleanRow);
                        }
                    }
                }
            });

            if (allRawData.length === 0) {
                return res.status(400).json({ message: "Không tìm thấy dữ liệu hợp lệ trong file Excel" });
            }

            await connection.beginTransaction();
            let successCount = 0;

            for (const majorData of allRawData) {

                // Xử lý logic cột "Gốc" đối với Tổ hợp môn
                // Nếu có cột "Tổ hợp môn" và "Gốc", ta xét xem nó có phải gốc không
                if (majorData.tohopmon !== undefined) {
                    let isGoc = false;
                    if (majorData.is_goc !== undefined) {
                        const val = String(majorData.is_goc).trim().toLowerCase();
                        // Các giá trị được coi là đánh dấu gốc
                        if (['x', '1', 'có', 'co', 'true', 'v', 'yes', 'y', '+', 'gốc', 'goc'].includes(val)) {
                            isGoc = true;
                        }
                    }

                    // Nếu là gốc, ghi đè vào n_tohopgoc để chuẩn bị Import/Update
                    if (isGoc) {
                        majorData.n_tohopgoc = majorData.tohopmon;
                    }
                }

                // Bỏ qua nếu dòng không có Mã ngành (trường bắt buộc)
                if (!majorData.manganh) continue;

                const [existing] = await connection.query('SELECT idnganh FROM xt_nganh WHERE manganh = ?', [majorData.manganh]);

                if (existing.length > 0) {
                    // CẬP NHẬT TỪNG PHẦN (PARTIAL UPDATE): Chỉ update những cột có trong file Excel
                    const updateFields = [];
                    const updateValues = [];

                    if (majorData.tennganh !== undefined) { updateFields.push('tennganh=?'); updateValues.push(majorData.tennganh); }
                    if (majorData.n_tohopgoc !== undefined) { updateFields.push('n_tohopgoc=?'); updateValues.push(majorData.n_tohopgoc); }
                    if (majorData.n_chitieu !== undefined) { updateFields.push('n_chitieu=?'); updateValues.push(majorData.n_chitieu); }
                    if (majorData.n_diemsan !== undefined) { updateFields.push('n_diemsan=?'); updateValues.push(majorData.n_diemsan); }
                    if (majorData.n_diemtrungtuyen !== undefined) { updateFields.push('n_diemtrungtuyen=?'); updateValues.push(majorData.n_diemtrungtuyen); }
                    if (majorData.n_tuyenthang !== undefined) { updateFields.push('n_tuyenthang=?'); updateValues.push(majorData.n_tuyenthang); }
                    if (majorData.n_dgnl !== undefined) { updateFields.push('n_dgnl=?'); updateValues.push(majorData.n_dgnl); }
                    if (majorData.n_thpt !== undefined) { updateFields.push('n_thpt=?'); updateValues.push(majorData.n_thpt); }
                    if (majorData.n_vsat !== undefined) { updateFields.push('n_vsat=?'); updateValues.push(majorData.n_vsat); }
                    if (majorData.sl_xtt !== undefined) { updateFields.push('sl_xtt=?'); updateValues.push(majorData.sl_xtt); }
                    if (majorData.sl_dgnl !== undefined) { updateFields.push('sl_dgnl=?'); updateValues.push(majorData.sl_dgnl); }
                    if (majorData.sl_vsat !== undefined) { updateFields.push('sl_vsat=?'); updateValues.push(majorData.sl_vsat); }
                    if (majorData.sl_thpt !== undefined) { updateFields.push('sl_thpt=?'); updateValues.push(majorData.sl_thpt); }

                    if (updateFields.length > 0) {
                        const updateQuery = `UPDATE xt_nganh SET ${updateFields.join(', ')} WHERE manganh=?`;
                        updateValues.push(majorData.manganh);
                        await connection.query(updateQuery, updateValues);
                    }
                } else {
                    const chiTieu = majorData.n_chitieu || 0;
                    const tenNganh = majorData.tennganh || '';
                    const toHop = majorData.n_tohopgoc || null;

                    const insertQuery = `
                        INSERT INTO xt_nganh 
                        (manganh, tennganh, n_tohopgoc, n_chitieu, n_diemsan, n_diemtrungtuyen, n_tuyenthang, n_dgnl, n_thpt, n_vsat, sl_xtt, sl_dgnl, sl_vsat, sl_thpt)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    `;
                    await connection.query(insertQuery, [
                        majorData.manganh, tenNganh, toHop, chiTieu, majorData.n_diemsan || null, majorData.n_diemtrungtuyen || null,
                        majorData.n_tuyenthang || '0', majorData.n_dgnl || '0', majorData.n_thpt || '0', majorData.n_vsat || '0',
                        majorData.sl_xtt || 0, majorData.sl_dgnl || 0, majorData.sl_vsat || 0, majorData.sl_thpt || 0
                    ]);
                }
                successCount++;
            }

            await connection.commit();
            res.status(200).json({
                message: `Import thành công! Đã thêm/cập nhật ${successCount} ngành.`,
                successCount
            });

        } catch (error) {
            await connection.rollback();
            console.error("Lỗi Import Excel Ngành:", error);
            res.status(500).json({ message: "Lỗi xử lý file Excel", error: error.message });
        } finally {
            connection.release();
        }
    }
}

module.exports = MajorController;
