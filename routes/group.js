/**
 * @module Group routine.
 */
const router = require('express-promise-router')();
const groupController = require('../controllers/group');

// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});
// create new group
router.post('/create', groupController.createGroup);
// get group's object
router.get('/:groupId', groupController.getGroup);
// get the userList of a group
router.get('/:groupId/user-list', groupController.getUserList);
// get a list of the name of the user.
router.get('/:groupId/user-name-list', groupController.getUserNameList);
// add one user to group
router.put('/add/user', groupController.addUser);
// remove user from the group
router.delete('/user', groupController.addUser);
// TODO: complete deletion
// router.delete('/:groupId', groupController.deleteGroup);
module.exports = router;
