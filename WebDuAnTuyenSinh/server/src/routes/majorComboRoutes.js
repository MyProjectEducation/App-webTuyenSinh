const express = require('express');
const router = express.Router();
const majorComboController = require('../controllers/majorComboController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/', authMiddleware, majorComboController.getAll);
router.post('/', authMiddleware, majorComboController.create);
router.delete('/:id', authMiddleware, majorComboController.delete);

module.exports = router;
