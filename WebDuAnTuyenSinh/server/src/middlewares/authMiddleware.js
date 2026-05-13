const jwt = require('jsonwebtoken');

// Auth middleware can be disabled for local testing by setting DISABLE_AUTH=true
// in the server .env or environment. When disabled, requests are allowed through.
const authMiddleware = (req, res, next) => {
    if (process.env.DISABLE_AUTH === 'true') {
        return next();
    }

    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ error: 'Không tìm thấy token xác thực' });
    }

    const token = authHeader.split(' ')[1];

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET || 'tuyensinh_secret_key');
        req.user = decoded;
        next();
    } catch (err) {
        return res.status(401).json({ error: 'Token không hợp lệ hoặc đã hết hạn' });
    }
};

module.exports = authMiddleware;
