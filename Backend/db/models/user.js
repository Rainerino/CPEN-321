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
const Event = require('./event');
const Group = require('./group');

const userSchema = new mongoose.Schema({
  email: {
    type: String,
    unique: true,
    minlength: 1,
    maxlength: 30,
    validate: (value) => validator.isEmail(value)
  },
  password: {
    type: String,
    required: true,
    minlength: 6,
    maxlength: 30,
    validate: (value) => validator.isAlphanumeric(value)
  },
  firebaseRegistrationToken: {
    type: String,
  },
  firstName: {
    type: String,
    required: true,
    minlength: 1,
    maxlength: 30,
    validate: (value) => validator.isAlpha(value)
  },
  lastName: {
    type: String,
    required: true,
    minlength: 1,
    maxlength: 30,
    validate: (value) => validator.isAlpha(value)
  },
  /**
   * Coordinate are in lon and lat format!
   */
  location: {
    type: { type: String },
    // longitude then latitude
    coordinate: [{
      type: Number,
    }],
    city: String,
    country: String,
  },
  /**
   * Preference setting for Users
   */
  suggestionRadius: {
    enum: [0.1, 0.2, 0.5, 1, 2, 3, 5, 10, 20],
    type: Number,
    default: 0.5
  },
  meetingNotification: Boolean,
  /**
   * User's list of relations to other models.
   * FIXME: Note that we are currently support only one calendar per user!! only calendarList[0] will be valid.
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
userSchema.statics.id2ObjectList = function (userList) {
  return new Promise((resolve, reject) => {
    this.find({ _id: userList }, (err, person) => {
      if (err) {
        return reject(err);
      }
      resolve(person);
    });
  });
};

userSchema.statics.addMeetingToUser = function (user, event, isOwner) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate({ _id: user._id, },
      { $addToSet: { scheduleEventList: event._id } },
      { new: true, useFindAndModify: false },
      async (err, updatedUser) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedUser);
        if (isOwner) {
          await Event.findByIdAndUpdate(event._id,
            {
              $set: { ownerId: user._id, eventType: 'MEETING' },
            },
            { new: true, useFindAndModify: false },
            async (err, updatedEvent) => {
              if (err) {
                console.log(err);
                return reject(err);
              }
              resolve(updatedEvent);
            });
        } else {
          await Event.findByIdAndUpdate(event._id,
            {
              $set: { eventType: 'MEETING' },
              $addToSet: { userList: user._id }
            },
            { new: true, useFindAndModify: false },
            async (err, updatedEvent) => {
              if (err) {
                console.log(err);
                return reject(err);
              }
              resolve(updatedEvent);
            });
        }
      });
  });
};
userSchema.statics.removeMeetingFromUser = function (user, event, isOwner) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate({ _id: user._id, },
      { $pull: { scheduleEventList: event._id } },
      { new: true, useFindAndModify: false },
      async (err, updatedUser) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedUser);
        if (isOwner) {
          await Event.findByIdAndDelete(event._id,
            async (err, updatedEvent) => {
              if (err) {
                console.log(err);
                return reject(err);
              }
              resolve(updatedEvent);
            });
        } else {
          await Event.findByIdAndUpdate(event._id,
            {
              $pull: { userList: user._id }
            },
            { new: true, useFindAndModify: false },
            async (err, updatedEvent) => {
              if (err) {
                console.log(err);
                return reject(err);
              }
              resolve(updatedEvent);
            });
        }
      });
  });
};

userSchema.statics.addCalendarToUser = function (user, calendar) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate(user._id,
      { $addToSet: { calendarList: calendar._id } },
      { new: true, useFindAndModify: false },
      async (err, updatedUser) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedUser);
        await Calendar.findByIdAndUpdate(calendar._id,
          { $set: { ownerId: user._id } },
          { new: true, useFindAndModify: false },
          async (err, updatedCal) => {
            if (err) {
              console.log(err);
              return reject(err);
            }
            resolve(updatedCal);
          });
      });
  });
};

userSchema.statics.addGroupToUser = function (user, group) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate(user._id,
      { $addToSet: { groupList: group._id } },
      { new: true, useFindAndModify: false },
      async (err, updatedUser) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedUser);
        await Group.findByIdAndUpdate(group._id,
          { $addToSet: { userList: user._id } },
          { new: true, useFindAndModify: false },
          async (err, updatedGroup) => {
            if (err) {
              console.log(err);
              return reject(err);
            }
            resolve(updatedGroup);
          });
      });
  });
};

userSchema.statics.deleteGroupFromUser = function (user, group) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate(user._id,
      { $pull: { groupList: group._id } },
      { new: true, useFindAndModify: false },
      async (err, updatedUser) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedUser);
        await Group.findByIdAndUpdate(group._id,
          { $pull: { userList: user._id } },
          { new: true, useFindAndModify: false },
          async (err, updatedGroup) => {
            if (err) {
              console.log(err);
              return reject(err);
            }
            resolve(updatedGroup);
          });
      });
  });
};

userSchema.statics.addFriendToUser = function (friend, user) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate({ _id: user._id, },
      { $addToSet: { friendList: friend._id } },
      { new: true, useFindAndModify: false },
      async (err, updatedUser) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedUser);
        await this.findByIdAndUpdate(friend._id,
          { $addToSet: { friendList: user._id } },
          { new: true, useFindAndModify: false },
          async (err, updatedFriend) => {
            if (err) {
              console.log(err);
              return reject(err);
            }
            resolve(updatedFriend);
          });
      });
  });
};

userSchema.statics.deleteFriendFromUser = function (friend, user) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate({ _id: user._id, },
      { $pull: { friendList: friend._id } },
      { new: true, useFindAndModify: false },
      async (err, updatedUser) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedUser);
        await this.findByIdAndUpdate(friend._id,
          { $pull: { friendList: user._id } },
          { new: true, useFindAndModify: false },
          async (err, updatedFriend) => {
            if (err) {
              console.log(err);
              return reject(err);
            }
            resolve(updatedFriend);
          });
      });
  });
};

userSchema.plugin(timestampPlugin);

const User = mongoose.model('User', userSchema, 'users');

module.exports = User;
