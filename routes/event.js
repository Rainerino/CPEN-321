/**
 * @module Event routine.
 *
 */
const router = require('express-promise-router')();
const eventController = require('../controllers/event/event');
const notifyController = require('../controllers/event/event_notification');
const passport = require('passport');
const eventController = require('../controllers/event/event');
const notifyController = require('../controllers/event/event_notification');

const passportJWT = passport.authenticate('jwt', { session: false });

// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});

router.get('/:eventId', eventController.getEvent); // get events
// delete a event
router.delete('/delete', eventController.deleteEvent);
// create a calendar event
router.post('/create/event', eventController.createEvent);
// create a meeting event
router.post('/create/meeting', eventController.createMeeting);
// notify all users that are under the scheduled meeting event.
router.post('/notify/meeting/invite', notifyController.notifyMeetingUsers);
// TODO, only send the notification
router.post('/notify/meeting/accept', notifyController.notifyAccept);
// TODO, only send the notification
router.post('/notify/meeting/reject', notifyController.notifyReject);


module.exports = router;
