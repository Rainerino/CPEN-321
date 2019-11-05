/**
 * @module controller/calendar
 * @desc Contains all routes for calendar models
 */
const Calendar = require("../db/models/calendar");

/**
 * @example GET /calendar/:calendarId
 * @type {Request}
 * @desc get a calendar object from the id. 
 */
exports.getCalendar = (req, res) => {
  Calendar.findById(req.params.calendarId, (err, existingCalendar) => {
    if (err) { res.status(400).send("Calendar not found"); }
    res.status(200).json(existingCalendar);
  });
};

/* Skeleton methods to be filled in later */

/**
 * @example GET /calendar/:calendarId/event/today
 * @type {Request}
 * @desc get all event objects from a calendar
 */
exports.getTodayCalendarEvents = (req, res) => {
  Calendar.findById(req.params);
  res.status(501).send("Not implemented", req);
};
/**
 * @example GET /calendar/:calendarId/event/time-slices
 * @type {Request}
 * @desc get all the events that from one time slices of the calendar
 */
exports.getCalendarTimeSlot = (req, res) => {
  res.status(501).send("Not implemented", req);
};
/**
 * @example GET /calendar/:calendarId/event/all
 * @type {Request}
 * @desc get all event objects from a calendar
 */
exports.getAllCalendarEvents = (req, res) => {
  res.status(501).send("Not implemented", req);
};
/**
 * @example GET /calendar/:calendarId/event/all-id
 * @type {Request}
 * @desc get all event id from a calendar
 */
exports.getAllCalendarEvents = (req, res) => {
  res.status(501).send("Not implemented", req);
};
/**
 * @example PUT /calendar/:calendarId/event/:eventId
 * @param {String} eventId - the id of event
 * @type {Request}
 * @desc add events to calendar
 */
exports.putEvent = (req, res) => {
  Calendar.findByIdAndUpdate(req.params.calendarId,
    { $addToSet: { eventList: req.params.eventId } }, { new: true }, (err, updated) => {
      if (err) { res.status(400).send("Calendar not found"); }
      res.status(200).json(updated);
    });
};
/**
 * @example DELETE /calendar/:calendarId/event
 * @desc delete events
 */
exports.deleteEvent = (req, res) => res.status(501).send("Not implemented", req);
