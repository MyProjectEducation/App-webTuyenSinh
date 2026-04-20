const express = require('express');
const router = express.Router();
const dashboardController = require('../controllers/dashboardController');
const authMiddleware = require('../middlewares/authMiddleware');

// Thêm authMiddleware để bảo vệ API
router.get('/overview', authMiddleware, dashboardController.getOverviewStats);
router.get('/top-majors', authMiddleware, dashboardController.getTopMajors);
router.get('/score-distribution', authMiddleware, dashboardController.getScoreDistribution);

module.exports = router;
