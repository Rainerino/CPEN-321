/**
 * @module models/user
 * @desc data model for users
 * @requires validator
 * @requires mongoose
 */
const mongoose = require('mongoose');
const validator = require('validator');
const ct = require('countries-and-timezones');
const bcrypt = require('bcryptjs');
// const GeoJSON = require('geojson');
const timestampPlugin = require('../plugins/timeStampUpdate');
const Calendar = require('./calendar');
const Event = require('./event');

const userSchema = new mongoose.Schema({
  email: {
    type: String,
    unique: true,
    validate: (value) => validator.isEmail(value)
  },
  password: {
    type: String,
    required: true,
  },
  firstName: String,
  lastName: String,
  interests: {
    enum: ['STUDY', 'STUDY', 'PLAY'],
    type: String
  },
  jobPosition: {
    enum: ['JOKER'],
    type: String
  },
  timeZoneOffset: Number,
  /**
   * Coordinate are in lon and lat format!
   */
  location: {
    type: { type: String },
    coordinate: [{ type: Number }],
    city: String,
    country: String
  },
  /**
   * Preference setting for Users
   */
  suggestionRadius: {
    enum: [0.1, 0.5, 1, 2, 3, 5, 10, 20],
    type: Number,
    default: 0.5
  },
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
      // autopoluate: true
    }
  ],
  /**
   * User's current friendlist.
   */
  friendList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    }
  ],
  /**
   * Suggest friend list are list of users that are suggested to the user
   * based on the complex logic.
   */
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
      validate: (value) => Event.findOne({ _id: value }, (err, event) => {
        if (err) return false;
        if (event.eventType === 'MEETING') {
          return true;
        }
        return false;
      })
    }
  ]
});

/** 
 * @description Store the encrypted password
 */
userSchema.pre('save', async function(next) {
  try {
      // Generate a salt
      const salt = await bcrypt.genSalt(10);
      const passwordHash = await bcrypt.hash(this.password, salt);
      // Re-assign hashed version over original, plain text password
      this.password = passwordHash;
      next();
  } catch(error) {
      next(error);
  }
});

/** 
 * @description Check the user's password
 */
userSchema.methods.isValidPassword = async function(newPassword) {
  try{
     return await bcrypt.compare(newPassword, this.password);
  } catch{
      throw new Error(error);
  }
}

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
userSchema.plugin(require('mongoose-deep-populate')(mongoose));

const User = mongoose.model('User', userSchema, 'users');

module.exports = User;
