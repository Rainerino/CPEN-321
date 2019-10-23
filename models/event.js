const mongoose = require('mongoose');

const eventSchema = new mongoose.Schema({
  eventName: String,
  startTime: mongoose.Schema.Types.Date,
  duration: Number,
  calendarId: { type: mongoose.Schema.Types.ObjectId, ref: 'Calendar' },
}, { timestamps: true });

const Event = mongoose.model('Event', eventSchema);
module.exports = Event;
