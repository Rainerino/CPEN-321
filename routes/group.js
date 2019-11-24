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
router.post('/create', groupController.createGroup); // create new group
router.get('/:groupId', groupController.getGroup); // get group
router.get('/:groupId/user-list', groupController.getUserList); // get the userList of a group
router.get('/:groupId/user-name-list', groupController.getUserNameList); // get group member's name list
router.put('/:groupId/add-user', groupController.addUser); // add one user to group
// FIXME remove this
router.get('/:groupId/calendar', groupController.getCalendarId); // get group's calendar. It will also be refreshed.
// FIXME remote this.
router.put('/set-calendar', groupController.setCalendar); // set the group calendar for group

// TODO: complete deletion
// router.delete('/:groupId', groupController.deleteGroup);
// router.delete('/userlist/:userId', groupController.deleteGroupUser);
module.exports = router;
