/**
 * @module models/user
 * @desc data model for users
 * @requires validator
 * @requires mongoose
 */
const mongoose = require("mongoose");
const validator = require("validator");
//const ct = require("countries-and-timezones");
// const GeoJSON = require("geojson");
const timestampPlugin = require("../plugins/timeStampUpdate");
const Calendar = require("./calendar");
const Event = require("./event");

const userSchema = new mongoose.Schema({
  email: {
    type: String,
    unique: true,
    validate: (value) => validator.isEmail(value)
  },
  password: String,
  firstName: String,
  lastName: String,
  interests: {
    enum: ["STUDY", "STUDY", "PLAY"],
    type: String
  },
  jobPosition: {
    enum: ["JOKER"],
    type: String
  },
  timeZoneOffset: Number,
  location: {
    type: { type: String },
    coordinate: [{ type: Number }],
    city: String,
    country: String
  },
  /**
   * Preference setting for Users
   */
  suggestionRadius: Number,
  meetingNotification: Boolean,
  /**
   * User's list of relations to other models.
   */
  calendarList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Calendar",
      validate: (value) => Calendar.findOne({ _id: value }, (err, calendar) => {
        if (err) { return false; }
        if (calendar.calendarType === "USER") {
          return true;
        }
        return false;
      })
    }
  ],
  groupList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Group",
      // autopoluate: true
    }
  ],
  /**
   * User"s current friendlist.
   */
  friendList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
    }
  ],
  /**
   * Suggest friend list are list of users that are suggested to the user
   * based on the complex logic.
   */
  suggestedFriendList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
    }
  ],
  scheduleEventList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Event",
      validate: (value) => Event.findOne({ _id: value }, (err, event) => {
        if (err) { return false; }
        else if (event.eventType === "MEETING") {
          return true;
        } else {
        return false;
        }
      })
    }
  ]
});

/**
 * @description Get the user name of the user object
 *
 */
userSchema.methods.getName = function () {
  return (`${this.firstName} ${this.lastName}`);
};
/**
 * @description get all the users in the database. Use with caution
 * there will be a lot of data.
 */
userSchema.statics.getUsers = function () {
  return new Promise((resolve, reject) => {
    this.find((err, docs) => {
      if (err) {
        return reject(err);
      }
      resolve(docs);
    });
  });
};
/**
 * @param {Array} userList - list of user id
 * return an array of user objects
 */
userSchema.statics.userFriendList = function (userList) {
  return new Promise((resolve, reject) => {
    this.find({ _id: userList }, (err, person) => {
      if (err) {
        return reject(err);
      }
      resolve(person);
    });
  });
};

userSchema.plugin(timestampPlugin);
userSchema.plugin(require("mongoose-deep-populate")(mongoose));

const User = mongoose.model("User", userSchema, "users");

module.exports = User;
