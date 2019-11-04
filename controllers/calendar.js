/**
 * @module controller/calendar
 * @desc Contains all routes for calendar models
 */
const Calendar = require('../models/calendar');

/**
 * GET /calendar/:calendarId
 * get a calendar based on id
 */
exports.getCalendar = (req, res) => {
  Calendar.findById(req.params.calendarId, (err, existingCalendar) => {
    if (err) { res.status(400).send('Calendar not found'); }
    res.status(200).json(existingCalendar);
  });
};
/**
 * PUT /calendar/:calendarId/event
 * add events to calendar
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
 * DELETE /calendar/:calendarId/event
 * delete events
 */
exports.deleteEvent = (req, res) => res.status(501).send('Not implemented');
