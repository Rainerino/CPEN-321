const mongoose = require('mongoose');

const calendarSchema = new mongoose.Schema({
  calendarName: String,
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  eventList: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Event' }],
}, { timestamps: true });

const Calendar = mongoose.model('Calendar', calendarSchema);
module.exports = Calendar;
