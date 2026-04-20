require('dotenv').config();
const express = require('express');
const cors = require('cors');

// Import Routes
const authRoutes = require('./src/routes/authRoutes');
const candidateRoutes = require('./src/routes/candidateRoutes');
const majorRoutes = require('./src/routes/majorRoutes');
const subjectComboRoutes = require('./src/routes/subjectComboRoutes');
const majorComboRoutes = require('./src/routes/majorComboRoutes');
const conversionRoutes = require('./src/routes/conversionRoutes');
const scoreRoutes = require('./src/routes/scoreRoutes');
const bonusRoutes = require('./src/routes/bonusRoutes');
const admissionRoutes = require('./src/routes/admissionRoutes');
const dashboardRoutes = require('./src/routes/dashboardRoutes');

const app = express();

// Cấu hình CORS chặt chẽ để frontend truyền và nhận cookie/header authorization
app.use(cors({
    origin: 'http://localhost:5173', // Chỉ Web React truy cập
    credentials: true, // Cho phép truyền Authorization header
}));
app.use(express.json());

// Main Routes
app.use('/api/auth', authRoutes);
app.use('/api/candidates', candidateRoutes);
app.use('/api/majors', majorRoutes);
app.use('/api/subject-combos', subjectComboRoutes);
app.use('/api/major-combos', majorComboRoutes);
app.use('/api/conversions', conversionRoutes);
app.use('/api/scores', scoreRoutes);
app.use('/api/bonus', bonusRoutes);
app.use('/api/admissions', admissionRoutes);
app.use('/api/dashboard', dashboardRoutes);

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Node.js Backend is running on http://localhost:${PORT}`);
});
