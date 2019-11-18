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
  /**
   * @param {Array} userId
   * @param {Array} calendarId
   * Group calendar. A group can only have one calendar.
   * At the creation of a group, each users that are added to the group
   * will also give a calendarId of each users to be merged into
   * the group calendar.
   */
  calendarId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Calendar',
  },
  userList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    }
  ],
});

groupSchema.statics.addCalendarToGroup =
    function (group, calendar) {
      return new Promise((resolve, reject) => {
        this.findByIdAndUpdate(
            group._id,
            { $set: { calendarId: calendar._id } },
            { new: true, useFindAndModify: false},
            async (err, updatedGroup) => {
              if (err) {
                console.log(err);
                return reject(err);
              }
              resolve(updatedGroup);
              await Calendar.findByIdAndUpdate(
                  calendar._id,
                  { $set: { ownerId: group._id}},
                  { new: true, useFindAndModify: false},
                  async (err, updatedCal) => {
                    if (err) {
                      console.log(err);
                      return reject(err);
                    }
                    resolve(updatedCal);
                  });
            });
      });
    }

/**
 * @param {Array} userList - list of user id
 * return an array of user objects
 */
groupSchema.statics.groupUserList = function (userList) {
    return new Promise((resolve, reject) => {
        User.find({ _id: userList }, (err, person) => {
            if (err) {
                return reject(err);
            }
            resolve(person);
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
            const name = String(person.firstName + ' ' + person.lastName);
            resolve(name);
        });
    });
};


groupSchema.plugin(timestampPlugin);
groupSchema.plugin(require('mongoose-deep-populate')(mongoose));

const Group = mongoose.model('Group', groupSchema, 'groups');
module.exports = Group;
