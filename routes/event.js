/**
 * @module Event routine.
 */
const router = require('express-promise-router')();
const eventController = require('../controllers/event');
const passport = require('passport');

const passportJWT = passport.authenticate('jwt', { session: false });

// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});

router.get('/:eventId', passportJWT, eventController.getEvent); // get events
// delete a event
router.delete('/:eventId', passportJWT, eventController.deleteEvent);
// create a meeting event
router.post('/create/meeting', passportJWT, eventController.createMeeting);
// notify all users that are under the scheduled meeting event.
router.post('/notify/meeting', passportJWT, eventController.notifyMeetingUsers);
// delete user from scheduled meeting
router.put('/delete/meeting/user', passportJWT, eventController.removeUserFromMeeting);
// create a calendar event
router.post('/create/event', passportJWT, eventController.createEvent);


module.exports = router;
