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

// Login related methods
router.post('/login', userController.postLogin);
router.post('/signup', userController.postSignup);

// receive notification token
router.put('/notification-token', userController.notificationToken);
router.put('/location', userController.putLocation);

router.get('/secret', passportJWT, userController.secret); // Test for JWT

// define the home page route
router.get('/:userId/account', userController.getUser);

// get user's firendlist, return objects list of Users
router.get('/:userId/friendlist', userController.getFriendList);

// TODO
// router.get('/:userId/friendlist/name', user); // get the array of friend list names
// add one user to another's friendlist
router.put('/add/friend', userController.putFriendList);

// add group to user. This will mutually add the user to group and group to user.
router.put('/add/group', userController.putGroup);

router.post('/add/event', userController.addEvent); // add meeting event
router.get('/:userId/event', userController.getEvent); // get user's meeting event
router.get('/:userId/suggested-friends', userController.getSuggestedFriends); // get the suggested friend list // FIXME get the object list

// FIXME give a object list
router.get('/:userId/event/suggested-meeting-users/:startTime/:endTime', userController.getMeetingSuggestedFriends);

router.post('/google-calendar', userController.postGoogleCalendar);

// app.delete('/:userId', userController.deleteUser);
module.exports = router;
