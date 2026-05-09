const express = require('express');
const router = express.Router();
const scoreController = require('../controllers/scoreController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/', authMiddleware, scoreController.getAll);
router.post('/', authMiddleware, scoreController.create);
router.post('/import', authMiddleware, require('../middlewares/uploadMiddleware').single('file'), scoreController.importScores);
router.put('/:id', authMiddleware, scoreController.update);
router.delete('/:id', authMiddleware, scoreController.delete);

module.exports = router;
