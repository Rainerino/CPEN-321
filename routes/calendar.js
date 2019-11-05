/**
 * @module Calendar routine.
 */
const router = require("express-promise-router")();
const calendarController = require("../controllers/calendar");

// middleware that is specific to this router
router.use((next) => {
  next();
});

router.get("/:calendarId", calendarController.getCalendar); // get calendar based on id
router.put("/:calendarId/event", calendarController.putEvent); // add events, if event doesn"t exist just create one
router.delete("/:calendarId/event", calendarController.deleteEvent); // delete calendar

router.get("/:calendarId/event/today", calendarController.getTodayCalendarEvents);
router.get("/:calendarId/event/time-slices", calendarController.getCalendarTimeSlot);
router.get("/:calendarId/event/all", calendarController.getAllCalendarEvents);

module.exports = router;
