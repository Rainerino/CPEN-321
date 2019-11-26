/**
 * @module Group routine.
 */
const router = require('express-promise-router')();
const groupController = require('../controllers/group');
const userController = require('../controllers/user/user');
// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});

// create new group
router.post('/create', groupController.createGroup);
// get group's object
router.get('/:groupId', groupController.getGroup);
// add one user to group
router.put('/add/user', userController.putGroup);
// remove user from the group
router.delete('/delete/user', userController.deleteGroup);

module.exports = router;
