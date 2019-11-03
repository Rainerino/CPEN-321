/**
 * @module models/user
 * @desc data model for users
 * @requires validator
 * @requires mongoose
 */
const mongoose = require('mongoose');
const validator = require('validator');
// const GeoJSON = require('geojson');
const timestampPlugin = require('../plugins/timeStampUpdate');
const Calendar = require('./calendar');

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
    enum: [],
  },
  jobPosition: {
    enum: [],
  },
  timeZoneOffset: Date,
  /**
   * Preference setting for Users
   */
  meetingNotification: Boolean,
  /**
   * User's list of relations to other models.
   */
  calendarList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Calendar',
      validate: (value) => Calendar.findOne({ _id: value }, (err, calendar) => {
        if (err) return false;
        if (calendar.calendarType === 'USER') {
          return true;
        }
        return false;
      })
    }
  ],
  groupList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Group',
    }
  ],
  friendList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    }
  ],
  suggestedFriendList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    }
  ],
  scheduleEventList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Event',

    }
  ]
});
/**
 * Get the user name
 */
userSchema.methods.getName = function () {
  return (`${this.firstName} ${this.lastName}`);
};
/**
 *
 */
userSchema.statics.getUsers = function () {
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
userSchema.plugin(require('mongoose-autopopulate'));

const User = mongoose.model('User', userSchema);

module.exports = User;
