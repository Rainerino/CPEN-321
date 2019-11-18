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
  console.log('Time: ', Date.now());
  next();
});

router.post('/login', userController.postLogin);
router.post('/signup', userController.postSignup);
router.get('/secret', passportJWT, userController.secret); // Test for JWT

// define the home page route
router.get('/:userId/account', userController.getUser);
router.post('/:userId/location', userController.postLocation);
router.get('/:userId/group', userController.getGroup); // get user's group list
router.get('/:userId/friendlist', userController.getFriendList); // get user's firendlist
// TODO
// router.get('/:userId/friendlist/name', user); // get the array of friend list names
router.put('/:userId/friendlist', userController.putFriendList); // add user to user's friendlist
router.put('/:userId/group', userController.putGroup); // add group to user
// TODO: test this one out!
router.post('/calendar/add', userController.addCalendar); // add calendar
router.get('/:userId/calendar', userController.getCalendar); // get user's calendar list
router.post('/event/add', userController.addEvent); // add meeting event
router.post('/event/owner', userController.addEventOwner); // add calendar
router.get('/:userId/event', userController.getEvent); // get user's meeting event
router.get('/:userId/suggested-friends', userController.getSuggestedFriends); // get the suggested friend list
router.put('/:userId/suggested-friends', userController.putSuggestedFriends); // add suggested friends
// TODO
router.post('/:userId/suggested-friends/:toUserId', userController.notifySuggestedUser); // create a new suggest new friend notification
router.delete('/:userId/suggested-friends', userController.deleteSuggestedFriends);

router.get('/:userId/event/suggested-meeting-users/:startTime/:endTime', userController.getMeetingSuggestedFriends);
// TODO
router.get('/:userId/preference', userController.getPreferences);
// TODO
router.put('/:userId/preference', userController.putPreferences);
// TODO
router.post('/google-calendar', userController.postGoogleCalendar);

// TODO: user interests
// app.delete('/:userId', userController.deleteUser);
module.exports = router;
