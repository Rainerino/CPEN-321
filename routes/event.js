/**
 * @module Event routine.
 */
const router = require("express-promise-router")();
const eventController = require("../controllers/event");

// middleware that is specific to this router
router.use((next) => {
  next();
});

router.get("/:eventId", eventController.getEvent); // get events

router.delete("/:eventId", eventController.deleteEvent);
// create a meeting event
router.post("/create/meeting", eventController.createMeeting);
// create a calendar event
router.post("/create/event", eventController.createEvent);
// create a user calendar event

module.exports = router;
