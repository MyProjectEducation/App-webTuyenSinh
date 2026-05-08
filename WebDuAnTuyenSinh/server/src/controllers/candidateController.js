const CandidateModel = require('../models/candidateModel');
const xlsx = require('xlsx');
const db = require('../../config/db');
const bcrypt = require('bcrypt');

const { smartMap, splitFullName, formatDateToPassword } = require('../utils/excelUtils');

// Từ điển ánh xạ (Dictionary) - Mapping Header Excel đã chuẩn hóa sang Column Database
const EXCEL_MAPPING = {
    'cccd': 'cccd',
    'socccd': 'cccd',
    'cancuoccongdan': 'cccd',
    'sbd': 'sobaodanh',
    'sobaodanh': 'sobaodanh',
    'hoten': 'ho_ten',
    'hovaten': 'ho_ten',
    'holot': 'ho',
    'hovantendem': 'ho',
    'ten': 'ten',
    'ngaysinh': 'ngay_sinh',
    'dienthoai': 'dien_thoai',
    'sdt': 'dien_thoai',
    'gioitinh': 'gioi_tinh',
    'email': 'email',
    'noisinh': 'noi_sinh',
    'kvut': 'khu_vuc',
    'khuvuc': 'khu_vuc',
    'makhuvuc': 'khu_vuc',
    'dtut': 'doi_tuong',
    'doituong': 'doi_tuong',
    'madoituong': 'doi_tuong'
};

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

    static async importCandidates(req, res) {
        if (!req.file) return res.status(400).json({ message: "Vui lòng tải lên file Excel" });

        const connection = await db.getConnection();

        try {
            // 1. Đọc file từ bộ nhớ đệm (Buffer)
            // Bật cellDates: true để thư viện tự động parse định dạng ngày tháng
            const workbook = xlsx.read(req.file.buffer, { type: 'buffer', cellDates: true });
            let allRawData = [];

            // THAY ĐỔI: Quét tất cả các Sheet trong file Excel
            workbook.SheetNames.forEach(name => {
                const sheetData = xlsx.utils.sheet_to_json(workbook.Sheets[name]);
                if (sheetData.length > 0) {
                    allRawData = allRawData.concat(sheetData);
                }
            });

            if (allRawData.length === 0) {
                return res.status(400).json({ message: "File Excel không có dữ liệu" });
            }

            // 2. Xử lý từng dòng dữ liệu (Chỉ chuẩn bị dữ liệu thô, không truy vấn DB)
            const timestamp = Date.now();
            let index = 0;
            const insertValues = [];

            for (const row of allRawData) {
                index++;
                const { cleanRow: candidateData } = smartMap(row, EXCEL_MAPPING);

                if (!candidateData.cccd) continue;

                if (candidateData.ho_ten && (!candidateData.ho || !candidateData.ten)) {
                    const { ho, ten } = splitFullName(candidateData.ho_ten);
                    candidateData.ho = candidateData.ho || ho;
                    candidateData.ten = candidateData.ten || ten;
                }

                if (!candidateData.sobaodanh) {
                    candidateData.sobaodanh = `SBD${timestamp}${index}`;
                }

                let plainPassword = null;
                if (candidateData.ngay_sinh) {
                    plainPassword = formatDateToPassword(candidateData.ngay_sinh);
                }

                let cleanNgaySinh = candidateData.ngay_sinh || null;
                if (cleanNgaySinh instanceof Date) {
                    cleanNgaySinh = cleanNgaySinh.toISOString().split('T')[0];
                }

                insertValues.push({
                    cccd: candidateData.cccd,
                    sobaodanh: candidateData.sobaodanh,
                    ho: candidateData.ho || null,
                    ten: candidateData.ten || null,
                    ngay_sinh: cleanNgaySinh,
                    dien_thoai: candidateData.dien_thoai || null,
                    gioi_tinh: candidateData.gioi_tinh || null,
                    email: candidateData.email || null,
                    noi_sinh: candidateData.noi_sinh || null,
                    doi_tuong: candidateData.doi_tuong || 'NONE',
                    khu_vuc: candidateData.khu_vuc || 'KV3',
                    plainPassword: plainPassword
                });
            }

            if (insertValues.length === 0) {
                return res.status(400).json({ message: "Không tìm thấy dữ liệu hợp lệ để import." });
            }

            // 3. Lazy Hashing (Chính sách Mật khẩu Tạm)
            // Lời khuyên cực kỳ đắt giá: Không băm mật khẩu tại đây để tránh thắt cổ chai CPU.
            // Chúng ta lưu tạm password thô (plain text). 
            // Khi thí sinh Login lần đầu, hệ thống sẽ tự động hash lại bằng Bcrypt và yêu cầu đổi mật khẩu.
            insertValues.forEach(v => {
                v.password = v.plainPassword; // Lưu trực tiếp chuỗi DDMMYYYY
            });

            // 4. Bulk Insert theo từng Chunk để tăng tốc Database và tránh ER_LOCK_WAIT_TIMEOUT
            const chunkSize = 500;
            let successCount = 0;

            await connection.beginTransaction();

            for (let i = 0; i < insertValues.length; i += chunkSize) {
                const chunk = insertValues.slice(i, i + chunkSize);

                // Chuyển object thành mảng 2 chiều để MySQL2 tự động build SQL query "VALUES (?), (?)..."
                const queryValues = chunk.map(v => [
                    v.cccd, v.sobaodanh, v.ho, v.ten, v.ngay_sinh, v.dien_thoai,
                    v.gioi_tinh, v.email, v.noi_sinh, v.doi_tuong, v.khu_vuc, v.password
                ]);

                const query = `
                    INSERT INTO xt_thisinhxettuyen25 
                    (\`cccd\`, \`sobaodanh\`, \`ho\`, \`ten\`, \`ngay_sinh\`, \`dien_thoai\`, \`gioi_tinh\`, \`email\`, \`noi_sinh\`, \`doi_tuong\`, \`khu_vuc\`, \`password\`) 
                    VALUES ?
                    ON DUPLICATE KEY UPDATE 
                    \`sobaodanh\`=VALUES(\`sobaodanh\`), \`ho\`=VALUES(\`ho\`), \`ten\`=VALUES(\`ten\`), \`ngay_sinh\`=VALUES(\`ngay_sinh\`), 
                    \`dien_thoai\`=VALUES(\`dien_thoai\`), \`gioi_tinh\`=VALUES(\`gioi_tinh\`), \`email\`=VALUES(\`email\`), 
                    \`noi_sinh\`=VALUES(\`noi_sinh\`), \`doi_tuong\`=VALUES(\`doi_tuong\`), \`khu_vuc\`=VALUES(\`khu_vuc\`),
                    \`password\` = IF(VALUES(\`password\`) IS NOT NULL, VALUES(\`password\`), \`password\`)
                `;

                await connection.query(query, [queryValues]);
                successCount += chunk.length;
            }

            await connection.commit();
            res.status(200).json({
                message: `Import thành công! Đã thêm/cập nhật ${successCount} bản ghi.`,
                successCount
            });

        } catch (error) {
            if (connection) await connection.rollback();
            console.error("Lỗi Import Excel CHI TIẾT:", error);
            res.status(500).json({ message: "Lỗi xử lý file Excel", error: error.message });
        } finally {
            connection.release();
        }
    }
}

module.exports = CandidateController;
