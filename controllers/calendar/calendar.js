/**
 * @module controller/calendar
 * @desc Contains all routes for calendar models
 */
const Calendar = require('../../db/models/calendar');
const Event = require('../../db/models/event');
const helper = require('../helper');

const logger = helper.getMyLogger('Calendar Controller');
const calendarHelper = require('../calendar/calendar_helper');
/**
 * @example GET /calendar/:calendarId
 * @type {Request}
 * @desc get a calendar object from the id.
 */
exports.getCalendar = (req, res) => {
  Calendar.findById(req.params.calendarId, (err, existingCalendar) => {
    if (err) {
      logger.error(err.toString());
      return res.status(500).send(err);
    }
    if (!existingCalendar) {
      logger.warn('No calendar found');
      return res.status(404).send('No calendar found');
    }
    logger.info(`retrive calendar ${existingCalendar.calendarName}`);
    res.status(200).json(existingCalendar);
  });
};
/**
 * @example PUT /calendar/create/events
 * @param {ObjectId}
 * @param {ObjectId}
 * @type {Request}
 * @desc add events to calendar
 */
exports.putEvent = (req, res) => {
  // TODO:
  Calendar.findById(req.body.calendarId, (err, existingCalendar) => {
    if (err) return res.status(500);
    Event.findById(req.body.eventId, (err, addingEvent) => {
      if (err) return res.status(500);
      if (existingCalendar.checkEventCollideCalendar(addingEvent)) {
        return res.status(400).send('Event collide with calendar');
      }
      // not collding the current calendar, add to the calendar
      Calendar.findByIdAndUpdate(req.body.calendarId,
        { $addToSet: { eventList: req.body.eventId } },
        { new: true }, (err, updated) => {
          if (err) { res.status(400).send('Calendar not found'); }
          res.status(200).json(updated);
        });
    });
  });
};
/**
 * @example GET /calendar/:calendarId/event/time-slices
 * @type {Request}
 * @param {Date} startTime - the starting time
 * @param {Date} endTime - the ending time
 * @desc get all the events that from one time slices of the calendar
 */
exports.getCalendarTimeSlot = (req, res) => {
  if (!(Date.parse(req.body.startTime) && Date.parse(req.body.endTime))) {
    return res.status(400).send('Invalid date input');
  }
  Calendar.findById(req.params.calendarId, (_err, currentCalendar) => {
    currentCalendar.eventList.array.forEach((eventId) => {
      Event.findById(eventId, (err, eventObject) => {
        if (err) return res.status(500);
        if (eventObject.checkEventWithin(new Date(req.body.startTime),
          new Date(req.body.endTime))) {
          res.status(200).json(eventObject);
        }
      });
    });
  });
};
/**
 * @example GET /calendar/:calendarId/event/all
 * @type {Request}
 * @desc get all event objects from a calendar
 */
exports.getAllCalendarEvents = async (req, res) => {
  Calendar.findById(req.params.calendarId, (err, existingCalendar) => {
    if (err) { return res.status(500); }
    if (existingCalendar) {
      Calendar.eventList(existingCalendar.eventList)
        .then((event) => {
          res.status(200).json(event);
        })
        .catch((err) => {
          res.status(400).send('get event list errors');
          console.error(err);
        });
    } else {
      return res.status(404).send("Account with that calendar id doesn't exist.");
    }
  });
};
/**
 * @example GET /calendar/:calendarId/event/:date
 * @type {Request}
 * @desc get today's calendar event, and updates all the events that are repeated to
 * today. Return a list of events that will be none repeated.
 */
exports.getEventsOfDay = async (req, res) => {
  // check if the inputs are null or not
  if (!helper.checkNullArgument(2, req.params.calendarId, req.params.date)) {
    logger.warn('Null input');
    return res.status(400).send('Null inputs');
  }
  // check if date is valid or not
  const date = new Date(req.params.date);
  if (!(date instanceof Date && !isNaN(date))) {
    logger.warn(`Bad input with ${req.params.date}`);
    return res.status(400).send('Bad date input');
  }

  logger.debug(`Date to return is ${date.toDateString()}`);

  // check if the calendar is valid or not
  let calendar;
  try {
    calendar = await Calendar.findById(req.params.calendarId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(400).send(e.toString());
  }

  // get the event list of the date, and update all the events.
  try {
    const eventList = await calendarHelper.getEventsOfDay(calendar.eventList, date);
    logger.info(`get eventList length of ${eventList.length}`);
    return res.status(200).json(eventList);
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
};
/**
 * @example POST /create
 * @desc create a new calendar
 */
exports.createCalendar = (req, res) => {
  const calendar = new Calendar({
    calendarName: req.body.calendarName,
    calendarDescription: req.body.calendarDescription,
  });
  calendar.save((err, createdCalendar) => {
    if (err) { return res.status(500).send('Save calendar failed'); }
    res.status(201).json(createdCalendar);
  });
};
/**
 * @example PUT /calendar/:calendarId/combine-calendar
 * @param calendarId - ObjectId
 * @desc combine calednar into calendar. Return combined calendar
 */
exports.combineCalendar = async (req, res) => {
  // check if both calendar are valid
  const fromCalendar = await Calendar.findById(req.body.calendarId, (err, calendar) => {
    if (err) {
      return res.status(500).send(err);
    }
    if (!calendar) {
      return res.status(400).send('Calendar not found');
    }
  });
  const toCalendar = await Calendar.findById(req.params.calendarId, (err, calendar) => {
    if (err) {
      return res.status(500).send(err);
    }
    if (!calendar) {
      return res.status(400).send('Calendar not found');
    }
  });
  await Calendar.combineCalendarIntoCalendar(fromCalendar, toCalendar).then((result) => {
    console.log(result);
    return res.status(200).json(result);
  }, (err) => {
    console.log(err);
    return res.status(400).send(err);
  });
};
/**
 * @example DELETE /calendar/event/delete
 * @param {ObjectId} calendarId
 * @param {ObjectId} eventId
 * @desc delete events
 */
exports.deleteEvent = async (req, res) => {
  if (!helper.checkNullArgument(2, req.params.calendarId, req.params.eventId)) {
    return res.status(400).send('Null input');
  }

  let event;
  let calendar;
  try {
    event = await Event.findById(req.params.eventId);
    calendar = await Calendar.findById(req.params.calendarId);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }

  // remove event entry from calendar
  try {
    await calendar.update({ $pull: { eventList: event._id }});
    await calendar.save();
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }

  // remove event from database
  try {
    await Event.findByIdAndDelete(event._id);
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
  const msg = `event: ${event.firstName} is delete from calendar: ${calendar.calendarName}`;
  logger.info(msg);
  return res.status(200).send(msg);
};
/**
 * @example DELETE /calendar/delete
 * @desc delete events
 */
// TODO: complete this
exports.deleteCalendar = (req, res) => res.status(501).send('Not implemented');
