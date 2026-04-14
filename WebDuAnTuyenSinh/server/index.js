require('dotenv').config();
const express = require('express');
const cors = require('cors');
const db = require('./config/db');
const bcrypt = require('bcrypt');

const app = express();

app.use(cors());
app.use(express.json());

// API lấy danh sách thí sinh
app.get('/api/candidates', async (req, res) => {
    try {
        const [rows] = await db.query('SELECT * FROM xt_thisinhxettuyen25');
        res.json(rows);
    } catch (err) {
        console.error("Lỗi khi query thí sinh:", err);
        res.status(500).json({ error: 'Database error' });
    }
});

// API login mẫu dùng BCrypt như yêu cầu
app.post('/api/login', async (req, res) => {
    try {
        const { username, password } = req.body;
        // Giả sử tài khoản admin nằm ở sys_users (Sẽ báo lỗi nếu bảng chưa tồn tại)
        const [users] = await db.query('SELECT * FROM sys_users WHERE username = ?', [username]);
        
        if (users.length === 0) {
            return res.status(401).json({ error: 'Tài khoản không tồn tại' });
        }
        
        const user = users[0];
        // Kiểm tra mật khẩu (Giả sử mật khẩu trong DB đã được băm bằng bcrypt)
        const match = await bcrypt.compare(password, user.password);
        
        if (match) {
            res.json({ message: 'Đăng nhập thành công', user: { id: user.id, username: user.username, role: user.role } });
        } else {
            res.status(401).json({ error: 'Sai mật khẩu' });
        }
    } catch (err) {
        console.error("Lỗi khi login:", err);
        res.status(500).json({ error: 'Database error' });
    }
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Node.js Backend is running on http://localhost:${PORT}`);
});
