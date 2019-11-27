const mongoose = require('mongoose');
const Calendar = require('./calendar');
const User = require('./user');
const timestampPlugin = require('../plugins/timeStampUpdate');


const groupSchema = new mongoose.Schema({
  groupName: String,
  groupDescription: String,
  location: {
    type: { type: String },
    coordinate: [{ type: Number }]
  },
  userList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    }
  ],
  /**
   * @param {ObjectId list} calendarList - a list of the user's calendar
   */
  calendarList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Calendar',
    }
  ],
});

groupSchema.statics.addCalendarToGroup = function (group, calendar) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate(group._id,
      { $addToSet: { calendarList: calendar._id } },
      {
        new: true,
        useFindAndModify: false
      },
      async (err, updatedGroup) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedGroup);
      });
  });
};

groupSchema.statics.removeCalendarToGroup = function (group, calendar) {
  return new Promise((resolve, reject) => {
    this.findByIdAndUpdate(group._id,
      { $pull: { calendarList: calendar._id } },
      {
        new: true,
        useFindAndModify: false
      },
      async (err, updatedGroup) => {
        if (err) {
          console.log(err);
          return reject(err);
        }
        resolve(updatedGroup);
      });
  });
};

/**
 * @param {Array} userList - list of user id
 * return an array of user objects
 */
groupSchema.statics.groupUserNameList = function (userList) {
  return new Promise((resolve, reject) => {
    User.find({ _id: userList }, (err, person) => {
      if (err) {
        return reject(err);
      }
      const name = String(`${person.firstName} ${person.lastName}`);
      resolve(name);
    });
  });
};


groupSchema.plugin(timestampPlugin);

const Group = mongoose.model('Group', groupSchema, 'groups');
module.exports = Group;
