/**
 * @module controller/event
 * @desc Contains all routes for event model
 */
const Event = require('../models/event');

/**
 * GET /event/:eventId
 */
exports.getEvent = (req, res) => {
  Event.findById(req.params.eventId, (err, existingEvent) => {
    if (err) { return res.status(400); }
    if (existingEvent) { return res.json(existingEvent); }
    return res.status(404).send("Event with the given event Id doesn't exist.");
  });
};
/**
 * POST /event/:eventName:date:duration
 */
exports.createEvent = (req, res) => {
  const event = new Event({
    eventName: req.params.eventName,
    startTime: req.params.date,
    duration: req.params.duration
  });
  event.save((err, createdEvent) => {
    if (err) { return res.status(500).send('Save group failed'); }
    res.status(201).json(createdEvent);
  });
};
/**
 * This is a function.
 *
 * @param {string} n - A string param
 * @return {string} A good string
 *
 * @example
 *
 *     foo('hello')
 */
exports.deleteEvent = (req, res) => {
  res.status(501).send('Not implemented');
};
