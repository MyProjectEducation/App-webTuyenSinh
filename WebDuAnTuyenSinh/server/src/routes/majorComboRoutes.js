const express = require('express');
const router = express.Router();
const majorComboController = require('../controllers/majorComboController');
const authMiddleware = require('../middlewares/authMiddleware');

const multer = require('multer');
const upload = multer({ storage: multer.memoryStorage() });

router.get('/', authMiddleware, majorComboController.getAll);
router.post('/', authMiddleware, majorComboController.create);
router.delete('/:id', authMiddleware, majorComboController.delete);
router.post('/import', upload.single('file'), authMiddleware, majorComboController.importExcel);

module.exports = router;
