const express = require('express');
const router = express.Router();
const conversionController = require('../controllers/conversionController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/', authMiddleware, conversionController.getAll);
router.post('/', authMiddleware, conversionController.create);
router.post('/import', authMiddleware, require('../middlewares/uploadMiddleware').single('file'), conversionController.importConversions);
router.put('/:id', authMiddleware, conversionController.update);
router.delete('/:id', authMiddleware, conversionController.delete);

module.exports = router;
