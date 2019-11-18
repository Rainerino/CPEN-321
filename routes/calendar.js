/**
 * @module Calendar routine.
 */
const router = require('express-promise-router')();
const calendarController = require('../controllers/calendar');

// middleware that is specific to this router
router.use((req, res, next) => {
  console.log('Time: ', Date.now());
  next();
});

router.get('/:calendarId', calendarController.getCalendar); // get calendar based on id
router.put('/event/add-events', calendarController.putEvent); // add events, if event doesn't exist just create one
router.delete('/:calendarId/event/delete/:eventId', calendarController.deleteEvent); // delete calendar event
router.get('/:calendarId/event/time-slices', calendarController.getCalendarTimeSlot);
router.get('/:calendarId/event/all', calendarController.getAllCalendarEvents);
router.get('/:calendarId/event/today', calendarController.getTodayEvents);
router.post('/create', calendarController.createCalendar); // create a calendar
router.put('/:calendarId/combine-calendar', calendarController.combineCalendar); // add a calendar to another.

module.exports = router;
