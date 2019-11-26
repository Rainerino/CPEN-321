/**
 * @module models/calendar
 * @desc data model for calendar
 *
 */
const mongoose = require('mongoose');
const Event = require('./event');

const timestampPlugin = require('../plugins/timeStampUpdate');


const calendarSchema = new mongoose.Schema({
  calendarName: {
    type: String,
    required: true
  },
  calendarDescription: String,
  /**
   * get the timezone offset of the client from UTC. If it's a user calendar,
   * it will be the user's timezone offset.
   * otherwise it will be group's timezone offset.
   *
   * FIXME: we agreed to ignore timezone for now.
   */
  // timezoneOffset: Date,
  /**
   * ownerId can be User or group. We will check this with findOne. Since objectId are unique.
   */
  ownerId: {
    type: mongoose.Schema.Types.ObjectId,
  },
  eventList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Event'
    }
  ],
});

/**
 * @description check if the event collide with the calendar events
 * A calendar should have unique events.
 * @return collide - true if collide
 */
calendarSchema.methods.checkEventCollideCalendar = function (eventToBeAdded) {
  this.eventList.forEach((eventId) => {
    Event.findById(eventId, (err, event) => {
      if (Event.checkEventsCollide(event, eventToBeAdded)) { return true; }
    });
  });
  return false;
};
/**
 * @param {Array} eventList - list of event id
 * return an array of event objects
 */
calendarSchema.statics.eventList = function (eventIdList) {
  return new Promise((resolve, reject) => {
    Event.find({ _id: eventIdList }, (err, event) => {
      if (err) {
        return reject(err);
      }
      resolve(event);
    });
  });
};


calendarSchema.statics.addEventToCalendar = function (calendar, event) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate(calendar._id,
      { $addToSet: { eventList: event._id } },
      { new: true, useFindAndModify: false },
      async (err, updatedCal) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedCal);
        await Event.findByIdAndUpdate(event._id,
          { $set: { ownerId: calendar._id, eventType: 'CALENDAR' } },
          { new: true, useFindAndModify: false },
          async (err, updatedEvent) => {
            if (err) {
              console.log(err);
              return reject(err);
            }
            resolve(updatedEvent);
          });
      });
  });
};

/**
 * @desc combine from calendar into to calendar. Does not modify from calendar
 * return combined calendar
 */
calendarSchema.statics.combineCalendarIntoCalendar = function (fromCalendar, toCalendar) {
  return new Promise((resolve, reject) => {
    this.findById(fromCalendar._id,
      async (err, updatedCal) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        if (!updatedCal) {
          return reject(err);
        }
        resolve(updatedCal);
        await this.findByIdAndUpdate(toCalendar._id,
          { $addToSet: { eventList: updatedCal.eventList } },
          { new: true, useFindAndModify: false },
          async (err, updatedEvent) => {
            if (err) {
              console.log(err);
              return reject(err);
            }
            if (!updatedEvent) {
              return reject(err);
            }
            resolve(updatedEvent);
          });
      });
  });
};


calendarSchema.plugin(timestampPlugin);

const Calendar = mongoose.model('Calendar', calendarSchema, 'calendars');
module.exports = Calendar;
