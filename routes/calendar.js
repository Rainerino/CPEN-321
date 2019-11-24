/**
 * @module Calendar routine.
 */
const router = require('express-promise-router')();
const calendarController = require('../controllers/calendar');

// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});

// get calendar based on id
router.get('/:calendarId', calendarController.getCalendar);
// add events, if event doesn't exist just create one
router.put('/event/add-events', calendarController.putEvent);
// delete calendar event
router.delete('/:calendarId/event/delete/:eventId', calendarController.deleteEvent);
// get events of a certain time slot. This will not update the calendar
router.get('/:calendarId/event/time-slices', calendarController.getCalendarTimeSlot);
// return all of the events
router.get('/:calendarId/event/all', calendarController.getAllCalendarEvents);
// return the calendar events of today, it will update the event
router.get('/:calendarId/event/today', calendarController.getTodayEvents);

router.put('/:calendarId/combine-calendar', calendarController.combineCalendar);
// TODO: group ill have different request
// /:calendarId/group/event/all'

module.exports = router;
