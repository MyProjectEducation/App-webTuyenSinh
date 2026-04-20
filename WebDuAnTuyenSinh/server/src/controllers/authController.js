const UserModel = require('../models/userModel');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

class AuthController {
    static async login(req, res) {
        try {
            const { username, password } = req.body;
            
            if (!username || !password) {
                return res.status(400).json({ error: 'Vui lòng cung cấp username và password' });
            }

            const user = await UserModel.findByUsername(username);
            
            if (!user) {
                return res.status(401).json({ error: 'Tài khoản không tồn tại' });
            }
            
            // Kiểm tra mật khẩu
            const match = await bcrypt.compare(password, user.password);
            
            if (match) {
                // Tạo JWT Token
                const token = jwt.sign(
                    { id: user.user_id, username: user.username, role: user.role },
                    process.env.JWT_SECRET || 'tuyensinh_secret_key',
                    { expiresIn: '24h' }
                );

                res.json({ 
                    message: 'Đăng nhập thành công', 
                    token, 
                    user: { id: user.user_id, username: user.username, role: user.role } 
                });
            } else {
                res.status(401).json({ error: 'Sai mật khẩu' });
            }
        } catch (err) {
            console.error("Lỗi khi login:", err);
            res.status(500).json({ error: 'Database error' });
        }
    }
}

module.exports = AuthController;
