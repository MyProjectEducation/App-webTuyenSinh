const express = require('express');
const router = express.Router();
const subjectComboController = require('../controllers/subjectComboController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/', authMiddleware, subjectComboController.getAll);
router.post('/', authMiddleware, subjectComboController.create);
router.post('/import', authMiddleware, require('../middlewares/uploadMiddleware').single('file'), subjectComboController.importSubjectCombos);
router.put('/:id', authMiddleware, subjectComboController.update);
router.delete('/:id', authMiddleware, subjectComboController.delete);

module.exports = router;
