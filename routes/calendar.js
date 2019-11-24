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
router.delete('/:calendarId/event/delete/:eventId', calendarController.deleteEvent); // delete calendar event
router.get('/:calendarId/event/time-slices', calendarController.getCalendarTimeSlot);
// TODO: date based time slices
router.get('/:calendarId/event/all', calendarController.getAllCalendarEvents);
router.get('/:calendarId/event/today', calendarController.getTodayEvents);
router.post('/create', calendarController.createCalendar); // create a calendar // fIXME remove this
router.put('/:calendarId/combine-calendar', calendarController.combineCalendar); // add a calendar to another. // FIXME remove
// TODO: group ill have different request
// /:calendarId/group/event/all'

module.exports = router;
