const express = require('express');
const router = express.Router();
const CandidateController = require('../controllers/candidateController');
const authMiddleware = require('../middlewares/authMiddleware');

// Chỉ dùng middleware xác thực khi cần bảo mật
// Ở đây danh sách thí sinh có thể yêu cầu đăng nhập mới được xem
router.get('/', authMiddleware, CandidateController.getAllCandidates);
router.get('/:id', authMiddleware, CandidateController.getCandidateById);

router.post('/', authMiddleware, CandidateController.create);
router.put('/:id', authMiddleware, CandidateController.update);
router.delete('/:id', authMiddleware, CandidateController.delete);

module.exports = router;
