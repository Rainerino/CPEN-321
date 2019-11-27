/**
 * @module Group routine.
 */
const router = require('express-promise-router')();
const passport = require('passport');
const userController = require('../controllers/user/user');
const passportConf = require('../config/passport');
const passportJWT = passport.authenticate('jwt', { session: false });

// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});

// login request
router.post('/login', userController.postLogin);
// signup new user request
router.post('/signup', userController.postSignup);
// receive notification token
router.put('/notification-token', passportJWT, userController.notificationToken);
// send user current location
router.put('/location', passportJWT, userController.putLocation);
// get all users in the database
router.get('/all', passportJWT, userController.getAllUser);
// get the userid's user object
router.get('/:userId/account', passportJWT, userController.getUser);

// get user's friend list, return objects list of Users
router.get('/:userId/friendlist', passportJWT, userController.getFriendList);
// add one user to another's friendlist
router.put('/add/friend', passportJWT, userController.putFriendList);
// delete a friend from user
router.delete('/delete/friend', passportJWT, userController.deleteFriend);


// add group to user. This will mutually add the user to group and group to user.
router.put('/add/group', passportJWT, userController.putGroup);
router.delete('/delete/group', passportJWT, userController.deleteGroup);

// add meeting event to user
router.post('/add/event', passportJWT, userController.addEvent);
// delete user from event
router.delete('/delete/event/user', passportJWT, userController.deleteUserFromEvent);
// get user's meeting event and calendar event. Check if there are collision.
router.get('/:userId/event/:date', passportJWT, userController.getEventsOfDay);

// get the suggested friend list
router.get('/:userId/suggested-friends', passportJWT, userController.getSuggestedFriends);
// give an object list of users
router.get('/:userId/event/suggested-meeting-users/:startTime/:endTime', passportJWT, userController.getMeetingSuggestedFriends);
// get the google calendar
router.post('/google-calendar', passportJWT, userController.postGoogleCalendar);
// delete a user from the database
// app.delete('/:userId', userController.deleteUser);
//
module.exports = router;
