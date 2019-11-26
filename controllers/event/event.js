/**
 * @module controller/event
 * @desc Contains all routes for event model
 */

const Event = require('../../db/models/event');
const Calendar = require('../../db/models/calendar');
const User = require('../../db/models/user');
const helper = require('../helper');

const logger = helper.getMyLogger('Event Controller');
/**
 * @example GET /event/:eventId
 * @desc get events objects from the id
 * @return event
 */
exports.getEvent = (req, res) => {
  Event.findById(req.params.eventId, (err, existingEvent) => {
    if (err) { return res.status(400); }
    if (existingEvent) { return res.status(200).json(existingEvent); }
    return res.status(404).send("Event with the given event Id doesn't exist.");
  });
};
/**
 * @example DELETE /event/delete
 * @param {ObjectId} eventId - the event to delete
 * @desc delete a event. The event can be meeting or calendar event
 * @return event
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

  const type = await event.eventType;
  if (type === 'CALENDAR') {
    let calendar;
    let event;
    try {
      event = await Event.findById(req.body.eventId);
      calendar = await Calendar.findById(event.ownerId);
    } catch (e) {
      logger.warn('Event not set up');
      return res.status(400).send('Event not set up');
    }
    try {
      await calendar.updateOne({ $pull: { eventList: event._id }});
      await calendar.save();
    } catch (e) {
      logger.error(e.toString());
      return res.status(500).send(e.toString());
    }

    try {
      await Event.findByIdAndDelete(event._id);
    } catch (e) {
      logger.error(e.toString());
      return res.status(500).send(e.toString());
    }
    const msg = `Deleted calendar event ${event.eventName} in ${calendar.calendarName} `;
    logger.info(msg);
    res.status(200).json(event);
  } else if (type === 'MEETING') {
    let userList;
    let owner;
    // delete meeting
    // add event to users. that are not the owner
    // remove event from each users that are not the owner
    try {
      userList = await Promise.all(event.userList.map((userId) => User.findById(userId).orFail()));
      await Promise.all(userList.map((user) => User.removeMeetingFromUser(user, event, false)));
    } catch (e) {
      logger.error(e.toString());
      return res.status(404).send(e.toString());
    }

    // remove the event in owner
    try {
      owner = await User.findById(event.ownerId).orFail();
      await User.removeMeetingFromUser(owner, event, true);
    } catch (e) {
      logger.warn(e.toString());
      return res.status(404).send(e.toString());
    }
    const msg = `Deleted meeting event ${event.eventName} by ${owner.firstName} with ${userList.length} other users`;
    logger.info(msg);
    res.status(200).json(event);
  } else {
    // remove event from calendar
    logger.warn('Event not set up');
    res.status(400).send('Event not set up');
  }
};
/**
 * @example POST /event/create/event
 * @param {String} eventName
 * @param {String} eventDescription
 * @param {Date} startTime
 * @param {Date} endTime
 * @param {String} repeatType
 * @param {ObjectId} ownerId
 * @description create a calendar event. The type field is not set until it's added.
 * @return event
 */
exports.createEvent = async (req, res) => {
  logger.debug(req.body);
  if (!helper.checkNullArgument(6,
    req.body.eventName,
    req.body.eventDescription,
    req.body.startTime,
    req.body.endTime,
    req.body.repeatType,
    req.body.ownerId)) {
    logger.warn('Null input');
    return res.status(400).send('Null input');
  }
  let calendar;
  let event;
  try {
    calendar = await Calendar.findById(req.body.ownerId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }

  // create the event
  try {
    event = await Event.create({
      eventName: req.body.eventName,
      eventDescription: req.body.eventDescription,
      startTime: req.body.startTime,
      endTime: req.body.endTime,
      repeatType: req.body.repeatType,
      eventType: 'CALENDAR',
      ownerId: req.body.ownerId,
    });
  } catch (e) {
    logger.warn(e.toString());
    return res.status(500).send(e.toString());
  }

  // add the event to the calendar
  try {
    calendar = await Calendar.findByIdAndUpdate(calendar._id,
      { $addToSet: { eventList: event._id } },
      { new: true, useFindAndModify: false });
    const msg = `Create event ${event.eventName} in ${calendar.calendarName}`;
    logger.info(msg);
    return res.status(201).json(event);
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
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
  // add event to each users that are not the owner
  try {
    userList = await Promise.all(event.userList.map((userId) => User.findById(userId).orFail()));
    await Promise.all(userList.map((user) => User.addMeetingToUser(user, event, false)));
  } catch (e) {
    logger.error(e.toString());
    return res.status(404).send(e.toString());
  }

  // add the event to owner
  try {
    owner = await User.findById(event.ownerId).orFail();
    await User.addMeetingToUser(owner, event, true);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }

  const msg = `Meeting event ${event.eventName} by ${owner.firstName} with ${userList.length} other users`;
  logger.info(msg);
  return res.status(201).send(event);
};
