/**
 * @module Group routine.
 */
const router = require('express-promise-router')();
const groupController = require('../controllers/group');
const passport = require('passport');

const passportJWT = passport.authenticate('jwt', { session: false });

// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});
// create new group
router.post('/create', passportJWT, groupController.createGroup);
// get group's object
router.get('/:groupId', passportJWT, groupController.getGroup);
// get the userList of a group
router.get('/:groupId/user-list', passportJWT, groupController.getUserList);
// get a list of the name of the user.
router.get('/:groupId/user-name-list', passportJWT, groupController.getUserNameList);
// add one user to group
router.put('/add/user', passportJWT, groupController.addUser);
// remove user from the group
router.delete('/user', passportJWT, groupController.addUser);
// TODO: complete deletion
// router.delete('/:groupId', groupController.deleteGroup);
module.exports = router;
