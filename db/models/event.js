
/**
 * @module models/event
 * @desc data model for events
 *
 */
const mongoose = require("mongoose");

const timestampPlugin = require("../plugins/timeStampUpdate");

const eventSchema = new mongoose.Schema({
  eventName: String,
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
    enum: [null, "DAILY", "WEEKLY", "MONTHLY"],
    type: String
  },
  /**
   * The calendar that event belongs to.
   */
  calendarId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Calendar"
  },
  /**
   * the type of events. can only be calendar event that belongs to one user or one group or meeting
   * @param {string} calendar - event belongs to a calendar, thus only has the calendarId field filled. Other fields will be empty.
   * @param {string} meeting - a meeting event. It will have a UserList.
   */
  eventType: {
    type: String,
    enum: [null, "USER_CALENDAR_EVENT", "GROUP_CALENDAR_EVENT", "MEETING"],
  },
  // the owner of the event. When merging from user to group, this will be changed to group's id.
  ownerId: {
    type: mongoose.Schema.Types.ObjectId,
  },
  /**
   * Meeting event userList: contains the user list to notify to. Owner id will be changed to the creator.
   * USER_CALENDAR: this is empty
   * GROUP_CALENDAR: this is empty
   */
  userList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User"
    }
  ],
  /**
   * Meeting event groupList: contains the user list to notify to.
   * USER_CALENDAR: this is empty
   * GROUP_CALENDAR: this is empty
   */
  groupList: [
    {
      type: mongoose.Schema.Types.ObjectId, ref: "Group"
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

eventSchema.statics.eventList = function (eventList) {
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
 * @description check if an event is within the timeslot. 
 *
 */
eventSchema.methods.checkEventWithin = function (startTime, endTime) {
  if (typeof (startTime) !== typeof (Date) || typeof (endTime) !== typeof (Date)) {
    return false;
  }
  return (startTime < this.startTime ) && (endTime > this.endTime);
};

/**
 * @description Check if events overlap, helper function for 
 * checkEventColldeCalendar()
 */

function checkTimes (startTime1, startTime2, endTime1, endTime2) {
  return ((startTime2 > startTime1) && (startTime2 < endTime1))
  || ((endTime2 > startTime1) && (endTime2 < endTime1));
}


eventSchema.plugin(timestampPlugin);
const Event = mongoose.model("Event", eventSchema);
/**
 * @description check if two events overlapps
 * @returns {Boolean} collide - true if collides, false if not. 
 */
eventSchema.methods.checkEventsCollide = function (otherEvent) {
  if (typeof (otherEvent) !== typeof (Event)) {
    return false;
  }
  // if the start time or endtime are within the current event, then these two events collides. 
  return checkTimes(this.startTime, otherEvent.startTime, this.endTime, otherEvent.endTime);
};



module.exports = Event;
