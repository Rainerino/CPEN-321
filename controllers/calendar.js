/**
 * @module controller/calendar
 * @desc Contains all routes for calendar models
 */
const Calendar = require('../db/models/calendar');

/**
 * @example GET /calendar/:calendarId
 * @type {Request}
 * @desc get a calendar based on id
 */
exports.getCalendar = (req, res) => {
  Calendar.findById(req.params.calendarId, (err, existingCalendar) => {
    if (err) { res.status(400).send('Calendar not found'); }
    res.status(200).json(existingCalendar);
  });
};
/**
 * @example GET /calendar/:calendarId/event/today
 * @type {Request}
 * @desc get all event objects from a calendar
 */

/**
 * @example GET /calendar/:calendarId/event/all
 * @type {Request}
 * @desc get all event objects from a calendar
 */

/**
 * @example PUT /calendar/:calendarId/event
 * @param {String} eventId - the id of event
 * @type {Request}
 * @desc add events to calendar
 */
exports.putEvent = (req, res) => {
  console.log(JSON.parse(JSON.stringify(req.body)));
  Calendar.findByIdAndUpdate(req.params.calendarId,
    { $addToSet: { eventList: req.body.eventId } }, { new: true }, (err, updated) => {
      if (err) { res.status(400).send('Calendar not found'); }
      res.status(200).json(updated);
    });
};
/**
 * @example DELETE /calendar/:calendarId/event
 * @desc delete events
 */
exports.deleteEvent = (req, res) => res.status(501).send('Not implemented');
