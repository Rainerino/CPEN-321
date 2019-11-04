/**
 * @module Event routine.
 */
const router = require('express-promise-router')();
const eventController = require('../controllers/event');

// middleware that is specific to this router
router.use((req, res, next) => {
  console.log('Time: ', Date.now());
  next();
});

router.get('/:eventId', eventController.getEvent); // get events
router.post('/:eventName/:date/:duration', eventController.createEvent); // create event
router.delete('/:eventId', eventController.deleteEvent);


module.exports = router;
