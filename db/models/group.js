const mongoose = require('mongoose');

const timestampPlugin = require('../plugins/timeStampUpdate');

const Calendar = require('./calendar');

const groupSchema = new mongoose.Schema({
  groupName: String,
  /**
   * Group calendar
   */
  calendarId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Calendar',
    validate: (value) => Calendar.findOne({ _id: value }, (err, calendar) => {
      if (err) return false;
      if (calendar.calendarType === 'GROUP') {
        return true;
      }
      return false;
    })
  },
  userList: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
  // chatroom
});

groupSchema.plugin(timestampPlugin);
const Group = mongoose.model('Group', groupSchema);
module.exports = Group;
