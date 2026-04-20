const express = require('express');
const router = express.Router();
const admissionController = require('../controllers/admissionController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/', authMiddleware, admissionController.getAll);
router.post('/', authMiddleware, admissionController.create);
router.put('/:id', authMiddleware, admissionController.update);
router.delete('/:id', authMiddleware, admissionController.delete);

router.post('/save-results', authMiddleware, admissionController.saveResults);

module.exports = router;
