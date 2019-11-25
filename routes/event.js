/**
 * @module Event routine.
 */
const router = require('express-promise-router')();
const eventController = require('../controllers/event');

// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});

router.get('/:eventId', eventController.getEvent); // get events
// delete a event
router.delete('/:eventId', eventController.deleteEvent);
// create a meeting event
router.post('/create/meeting', eventController.createMeeting);
// notify all users that are under the scheduled meeting event.
router.post('/notify/meeting', eventController.notifyMeetingUsers);
// delete user from scheduled meeting
router.put('/delete/meeting/user', eventController.removeUserFromMeeting);
// create a calendar event
router.post('/create/event', eventController.createEvent);


module.exports = router;