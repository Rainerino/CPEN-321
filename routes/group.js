/**
 * @module Group routine.
 */
const router = require('express-promise-router')();
const groupController = require('../controllers/group');

// middleware that is specific to this router
router.use((req, res, next) => {
  console.log('Time: ', Date.now());
  next();
});
router.post('/:groupName', groupController.createGroup); // create new group
router.get('/:groupId', groupController.getGroup); // get group
// TODO
// router.get('/:groupId/name', groupController.getGroupName);
router.put('/:groupId/userlist', groupController.addUserList); // add user to group
router.post('/calendar', groupController.createCalendar);
router.put('/calendar/:calendarId', groupController.putCalendar);
// router.delete('/:groupId', groupController.deleteGroup);
// router.delete('/userlist/:userId', groupController.deleteGroupUser);

module.exports = router;
