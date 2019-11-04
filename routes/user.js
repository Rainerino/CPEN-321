/**
 * @module Group routine.
 */
const router = require('express-promise-router')();
const userController = require('../controllers/user');

// middleware that is specific to this router
router.use((req, res, next) => {
  console.log('Time: ', Date.now());
  next();
});
// define the home page route


router.get('/:userId/account', userController.getUser);
router.get('/:userId/group', userController.getGroup); // get user's group list
router.get('/:userId/friendlist', userController.getFriendList); // get user's firendlist
// TODO
// router.get('/:userId/friendlist/name', user); // get the array of friend list names

router.put('/:userId/friendlist', userController.putFriendList); // add user to user's friendlist
router.put('/:userId/group', userController.putGroup); // add group to user
router.post('/:userId/calendar/:calendarId', userController.addCalendar); // add calendar
router.get('/:userId/calendar/', userController.getCalendar); // get user's calendar list
router.post('/:userId/event/:eventId', userController.addEvent); // add calendar
router.post('/:userId/event/owner/:eventId', userController.addEventOwner); // add calendar
router.get('/:userId/event/', userController.getEvent); // get user's calendar list

router.get('/:userId/suggested-friends', userController.getSuggestedFriends); // get the suggested friend list
router.put('/:userId/suggested-friends', userController.putSuggestedFriends); // add suggested friends
// TODO
router.post('/:userId/suggested-friends/:toUserId', userController.notifySuggestedUser); // create a new suggest new friend notification
router.delete('/:userId/suggested-friends', userController.deleteSuggestedFriends);

// router.get('/:userId/preference', userController. );
// router.put('/:userId/preference', userController. );
// router.get('/:userId/location', userController);
// router.put('/:userId/location', userController);


router.post('/google-calendar', userController.postGoogleCalendar);

// app.delete('/:userId', userController.deleteUser);
module.exports = router;
