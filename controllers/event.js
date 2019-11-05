/**
 * @module controller/event
 * @desc Contains all routes for event model
 */
const Event = require('../db/models/event');
/**
 * @example PUT /calendar/:calendarId/event
 * @param {String} eventId - the id of event
 * @type {Request}
 * @desc add events to calendar
 */
/**
 * @example GET /event/:eventId
 * @desc get events objects from the id
 */
exports.getEvent = (req, res) => {
  Event.findById(req.params.eventId, (err, existingEvent) => {
    if (err) { return res.status(400); }
    if (existingEvent) { return res.json(existingEvent); }
    return res.status(404).send("Event with the given event Id doesn't exist.");
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

/**
 * @example POST /event/create/event
 * @description create a calendar event. The type field is not set until it's added.
 */
exports.createEvent = (req, res) => {
  const event = new Event({
    eventName: req.body.eventName,
    eventDescription: req.body.eventDescription,
    startTime: req.body.startTime,
    endTime: req.body.endTime,
    repeatType: req.body.repeatType,
    ownerId: req.body.ownerId,
  });
  event.save((err, createdEvent) => {
    if (err) { return res.status(500).send('Save user/grou[ event failed'); }
    res.status(201).json(createdEvent);
  });
};

/**
 * @example POST /event/create/meeting
 * @param ALOT
 * @description create a meeting event
 */
exports.createMeeting = (req, res) => {
  const event = new Event({
    eventName: req.body.eventName,
    eventDescription: req.body.eventDescription,
    startTime: req.body.startTime,
    endTime: req.body.endTime,
    repeatType: req.body.repeatType,
    ownerId: req.body.ownerId,
    eventType: 'MEETING',
    userList: req.body.userList,
    groupList: req.params.groupList,
    notified: false
  });
  event.save((err, createdEvent) => {
    if (err) { return res.status(500).send('Save meetingevent failed'); }
    res.status(201).json(createdEvent);
  });
};
