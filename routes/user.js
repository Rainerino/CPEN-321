/**
 * @module Group routine.
 */
const router = require('express-promise-router')();
const passport = require('passport');
const userController = require('../controllers/user');
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
router.put('/notification-token', userController.notificationToken);
// send user current location
router.put('/location', userController.putLocation);
// FIXME remove this
router.get('/secret', passportJWT, userController.secret); // Test for JWT
// get all users in the database
router.get('/all', userController.getAllUser);
// get the userid's user object
router.get('/:userId/account', userController.getUser);
// get user's firend list, return objects list of Users
router.get('/:userId/friendlist', userController.getFriendList);
// add one user to another's friendlist
router.put('/add/friend', userController.putFriendList);
// add group to user. This will mutually add the user to group and group to user.
router.put('/add/group', userController.putGroup);
// add meeting event to user
router.post('/add/event', userController.addEvent);
// get user's meeting event and calendar event. Check if there are collision.
router.get('/:userId/event/:date', userController.getEventsOfDay);
// get the suggested friend list
router.get('/:userId/suggested-friends', userController.getSuggestedFriends);
// give an object list of users
router.get('/:userId/event/suggested-meeting-users/:startTime/:endTime',
  userController.getMeetingSuggestedFriends);
// get the google calendar
router.post('/google-calendar', userController.postGoogleCalendar);
// delete a user from the database
// app.delete('/:userId', userController.deleteUser);
//
module.exports = router;
