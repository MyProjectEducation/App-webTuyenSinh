const express = require('express');
const router = express.Router();
const admissionController = require('../controllers/admissionController');
const authMiddleware = require('../middlewares/authMiddleware');
const multer = require('multer');

const upload = multer({ storage: multer.memoryStorage() });

router.get('/', authMiddleware, admissionController.getAll);
router.post('/', authMiddleware, admissionController.create);
router.put('/:id', authMiddleware, admissionController.update);
router.delete('/:id', authMiddleware, admissionController.delete);

router.post('/save-results', authMiddleware, admissionController.saveResults);
router.post('/import', authMiddleware, upload.single('file'), admissionController.importPreferences);

module.exports = router;
