const express = require('express');
const router = express.Router();
const subjectComboController = require('../controllers/subjectComboController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/', authMiddleware, subjectComboController.getAll);
router.post('/', authMiddleware, subjectComboController.create);
router.put('/:id', authMiddleware, subjectComboController.update);
router.delete('/:id', authMiddleware, subjectComboController.delete);

module.exports = router;
