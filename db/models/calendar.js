/**
 * @module models/calendar
 * @desc data model for calendar
 *
 */
const mongoose = require("mongoose");
//const Event = require("./event");

const timestampPlugin = require("../plugins/timeStampUpdate");


const calendarSchema = new mongoose.Schema({
  calendarName: String,
  calendarDescription: String,
  /**
   * get the timezone offset of the client from UTC. If it's a user calendar, it will be the user's timezone offset.
   * otherwise it will be group's timezone offset.
   */
  timezoneOffset: Date,
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User"
  },
  groupId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Group"
  },
  /**
   * @param {string} USER - the calendar belongs to a user. It's created or it's imported from google calendar.
   * @param {string} GROUP - the calendar belongs to a group. Group calendar is created from syncing everyone's given calendar when joining the group.
   */
  calendarType: {
    enum: [null, "USER", "GROUP"],
    type: String
  },
  eventList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Event"
    }
  ],
});

/**
 * @description check if the event collide with the calendar events
 * A calendar should have unique events. 
 *
 */
calendarSchema.methods.checkEventCollideCalendar = function (eventToBeAdded) {
  this.eventList.array.forEach((event) => {
    if (event.checkEventsCollide(eventToBeAdded)) { return false; }
  });
  return true;
};

calendarSchema.plugin(timestampPlugin);
calendarSchema.plugin(require("mongoose-deep-populate")(mongoose));

const Calendar = mongoose.model("Calendar", calendarSchema, "calendars");
module.exports = Calendar;
