const express = require('express');
const router = express.Router();
const majorController = require('../controllers/majorController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/', authMiddleware, majorController.getAll);
router.post('/', authMiddleware, majorController.create);
router.put('/:id', authMiddleware, majorController.update);
router.delete('/:id', authMiddleware, majorController.delete);

module.exports = router;
