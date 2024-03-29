
/**
 * @module models/event
 * @desc data model for events
 *
 */
const mongoose = require('mongoose');
const helper = require('../../controllers/helper');
const timestampPlugin = require('../plugins/timeStampUpdate');

const logger = helper.getMyLogger('Event model');
const eventSchema = new mongoose.Schema({
  eventName: String, // TODO: string will include user name in a regex format
  eventDescription: String,
  startTime: {
    type: Date,
  },
  endTime: {
    type: Date,
  },
  /**
   * we will only have one event instances in the calendar every time.
   * For meeting type, it will have this option as well.
   */
  repeatType: {
    enum: ['NEVER', 'DAILY', 'WEEKLY', 'MONTHLY'],
    type: String
  },
  /**
   * the type of events. can only be calendar event that belongs to
   * one user or one group or meeting.
   * @param {string} calendar - event belongs to a calendar,
   * thus only has the calendarId field filled. Other fields will be empty.
   * @param {string} meeting - a meeting event. It will have a UserList.
   */
  eventType: {
    type: String,
    enum: [null, 'CALENDAR', 'MEETING'],
  },
  // the owner of the event. When merging from user to group, this will be changed to group's id.
  ownerId: {
    type: mongoose.Schema.Types.ObjectId,
    require: false
  },
  /**
   * Meeting event userList: contains the user list to notify to. Owner id will be changed to the creator.
   * USER_CALENDAR: this is empty
   * GROUP_CALENDAR: this is empty
   */
  userList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  ],
  /**
   * For push notification.
   */
  notified: Boolean
});
/**
 * get a list of events
 * @param {Array} eventList - list of events id in an array
 * @return {Array} eventList - array of event objects
 */
eventSchema.statics.id2ObjectList = function (eventList) {
  return new Promise((resolve, reject) => {
    this.find({ _id: eventList }, (err, event) => {
      if (err) {
        return reject(err);
      }
      resolve(event);
    });
  });
};

/**
 * @desc get all events.
 * @returns {Promise<unknown>}
 */
eventSchema.statics.getEvents = function () {
  return new Promise((resolve, reject) => {
    this.find((err, docs) => {
      if (err) {
        console.error(err);
        return reject(err);
      }
      resolve(docs);
    });
  });
};
/**
 * @description check if an event is within the timeslot.
 *
 */
eventSchema.methods.checkEventWithin = function (startTime, endTime) {
  if (typeof (startTime) !== typeof (Date) || typeof (endTime) !== typeof (Date)) {
    return false;
  }
  return (startTime < this.startTime) && (endTime > this.endTime);
};

/**
 * @description check if two events overlapps
 * @returns {Boolean} collide - true if collides, false if not.
 */
eventSchema.statics.checkEventsCollide = function (event, otherEvent) {
  if (!(otherEvent instanceof this && event instanceof this)) {
    throw new Error('Not event type input.');
  }
  const otherStart = new Date(otherEvent.startTime);
  const otherEnd = new Date(otherEvent.endTime);
  const thisStart = new Date(event.startTime);
  const thisEnd = new Date(event.endTime);
  // if the start time or endtime are within the current event, then these two events collides.
  // 7-8 8-9
  const otherOverlapBehind = (otherStart.getTime() > thisStart.getTime()) && (otherStart.getTime() < thisEnd.getTime());
  const otherOverlapAhead = ((otherEnd.getTime() > thisStart.getTime()) && (otherEnd.getTime() < thisEnd.getTime()));
  const otherCompleteOverlap = ((otherStart.getTime() === thisStart.getTime()) && (otherEnd.getTime() === thisEnd.getTime()));
  const otherInclude = (otherStart.getTime() < thisStart.getTime()) && (otherEnd.getTime() > thisEnd.getTime());
  const otherIsIncluded = (otherStart.getTime() > thisStart.getTime()) && (otherEnd.getTime() < thisEnd.getTime());
  return otherOverlapBehind || otherOverlapAhead || otherCompleteOverlap || otherInclude || otherIsIncluded;
};


eventSchema.plugin(timestampPlugin);
const Event = mongoose.model('Event', eventSchema);
module.exports = Event;
