const express = require('express');
const router = express.Router();
const bonusController = require('../controllers/bonusController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/', authMiddleware, bonusController.getAll);
router.post('/', authMiddleware, bonusController.create);
router.put('/:id', authMiddleware, bonusController.update);
router.delete('/:id', authMiddleware, bonusController.delete);

module.exports = router;
