/**
 * @module Calendar routine.
 */
const router = require('express-promise-router')();
const passport = require('passport');
const calendarController = require('../controllers/calendar/calendar');

const passportJWT = passport.authenticate('jwt', { session: false });

// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});

// get calendar based on id
router.get('/:calendarId', passportJWT, calendarController.getCalendar);
// add events, if event doesn't exist just create one
router.put('/create/events', passportJWT, calendarController.putEvent);
// delete calendar event
router.delete('/event/delete', passportJWT, calendarController.deleteEvent);
// get events of a certain time slot. This will not update the calendar
router.get('/:calendarId/event/time-slices', passportJWT, calendarController.getCalendarTimeSlot);
// return all of the events
router.get('/:calendarId/event/all', passportJWT, calendarController.getAllCalendarEvents);
// return the calendar events of day, it will update the event
router.get('/:calendarId/event/:date', passportJWT, calendarController.getEventsOfDay);

/* TODO: how we are getting the group calendar events are:
*  get through each calendar list item and return events from it.
*/
// add one calendar to another.
router.put('/:calendarId/combine-calendar', passportJWT, calendarController.combineCalendar);
// delete a calendar's event
router.delete('/calendar/event/delete', passportJWT, calendarController.deleteEvent);
// delete a calendar's event
router.delete('/calendar/delete',passportJWT,  calendarController.deleteEvent);

module.exports = router;
