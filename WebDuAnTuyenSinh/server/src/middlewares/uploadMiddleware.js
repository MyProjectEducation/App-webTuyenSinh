const multer = require('multer');

// Lưu file tạm vào RAM (Memory) để đọc luôn, không ghi xuống ổ cứng
const storage = multer.memoryStorage();
const upload = multer({ 
    storage: storage,
    fileFilter: (req, file, cb) => {
        // Hỗ trợ kiểm tra cả định dạng .xls và .xlsx
        if (file.mimetype.includes('excel') || file.mimetype.includes('spreadsheetml') || file.originalname.match(/\.(xlsx|xls)$/)) {
            cb(null, true);
        } else {
            cb(new Error('Chỉ chấp nhận định dạng file Excel (.xls, .xlsx)'), false);
        }
    }
});

module.exports = upload;
