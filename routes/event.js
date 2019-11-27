/**
 * @module Event routine.
 *
 */
const router = require('express-promise-router')();
const eventController = require('../controllers/event/event');
const notifyController = require('../controllers/event/event_notification');
const passport = require('passport');
const passportJWT = passport.authenticate('jwt', { session: false });

// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});

router.get('/:eventId', passportJWT, eventController.getEvent); // get events
// delete a event
router.delete('/delete', passportJWT, eventController.deleteEvent);
// create a calendar event
router.post('/create/event', passportJWT, eventController.createEvent);
// create a meeting event
router.post('/create/meeting', passportJWT, eventController.createMeeting);
// notify all users that are under the scheduled meeting event.
router.post('/notify/meeting/invite', passportJWT, notifyController.notifyMeetingUsers);
// TODO, only send the notification
router.post('/notify/meeting/accept', passportJWT, notifyController.notifyAccept);
// TODO, only send the notification
router.post('/notify/meeting/reject', passportJWT, notifyController.notifyReject);


module.exports = router;
