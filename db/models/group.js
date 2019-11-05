const mongoose = require('mongoose');

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
    // autopopulate: true,
    // validate: (value) => Calendar.findOne({ _id: value }, (err, calendar) => {
    //   if (err) return false;
    //   if (calendar.calendarType === 'GROUP') {
    //     return true;
    //   }
    //   return false;
    // })
  },
  userList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      // autopopulate: true
    }
  ],
});

groupSchema.plugin(timestampPlugin);
groupSchema.plugin(require('mongoose-deep-populate')(mongoose));

const Group = mongoose.model('Group', groupSchema, 'groups');
module.exports = Group;
