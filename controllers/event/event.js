/**
 * @module controller/event
 * @desc Contains all routes for event model
 */

const Event = require('../../db/models/event');
const User = require('../../db/models/user');
const helper = require('../helper');

const logger = helper.getMyLogger('Event Controller');

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
 * @example DELETE /event/delete
 * @param {ObjectId} eventId - the event to delete
 *
 */
exports.deleteEvent = async (req, res) => {
  if (!helper.checkNullArgument(1, req.body.eventId)) {
    logger.warn('Null input');
    return res.status(400).send('Null input');
  }

  // get the event
  let event;
  try {
    event = await Event.findById(req.body.eventId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }

  // const
  // if () {
  //
  // }
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
    eventType: 'CALENDAR',
    ownerId: req.body.ownerId,
  });
  event.save((err, createdEvent) => {
    if (err) { return res.status(500).send('Save event failed'); }
    res.status(201).json(createdEvent);
  });
};
/**
 * @example POST /event/create/meeting
 * @param {String} eventName
 * @param {String} eventDescription
 * @param {Date} startTime
 * @param {Date} endTime
 * @param {String} repeatType
 * @param {ObjectId} ownerId
 * @param {List} userList
 *
 * @description create a meeting event
 */
exports.createMeeting = async (req, res) => {
  if (!helper.checkNullArgument(7,
    req.body.eventName,
    req.body.eventDescription,
    req.body.startTime,
    req.body.endTime,
    req.body.repeatType,
    req.body.ownerId,
    req.body.userList)) {
    logger.warn('Null input');
    return res.status(400).send('Null input');
  }
  let event;
  let userList;
  let owner;
  try {
    event = await Event.create({
      eventName: req.body.eventName,
      eventDescription: req.body.eventDescription,
      startTime: req.body.startTime,
      endTime: req.body.endTime,
      repeatType: req.body.repeatType,
      ownerId: req.body.ownerId,
      eventType: 'MEETING',
      userList: req.body.userList,
      notified: false
    });
  } catch (e) {
    logger.warn(e.toString());
    return res.status(400).send(e.toString());
  }
  // add event to users. that are not the owner
  try {
    userList = await Promise.all(event.userList.map((userId) => User.findById(userId).orFail()));
    await Promise.all(userList.map((user) => User.addMeetingToUser(user, event, false)));
  } catch (e) {
    logger.error(e.toString());
    return res.status(404).send(e.toString());
  }

  // add event to owner
  try {
    owner = await User.findById(event.ownerId).orFail();
    await User.addMeetingToUser(owner, event, true);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }
  const msg = `Meeting event ${event.eventName} by ${owner.firstName} with ${userList.length} other users`;
  logger.info(msg);
  return res.status(200).send(msg);
};
