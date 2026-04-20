const jwt = require('jsonwebtoken');

const authMiddleware = (req, res, next) => {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ error: 'Không tìm thấy token xác thực' });
    }

    const token = authHeader.split(' ')[1];

    try {
        // Sử dụng một secret key mặc định hoặc từ biến môi trường
        const decoded = jwt.verify(token, process.env.JWT_SECRET || 'tuyensinh_secret_key');
        req.user = decoded;
        next();
    } catch (err) {
        return res.status(401).json({ error: 'Token không hợp lệ hoặc đã hết hạn' });
    }
};

module.exports = authMiddleware;
