const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  email: { type: String, unique: true },
  password: String,
  firstName: String,
  lastName: String,
  interests: {
    enum: [],

  },
  job_position: String,
  calendarList: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Calendar' }],
  groupList: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Group' }],
  friendList: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
  suggestedFriendList: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }]
}, { timestamps: true });

// validation method
// kittySchema.methods.speak = function () {
//   var greeting = this.name
//     ? "Meow name is " + this.name
//     : "I don't have a name";
//   console.log(greeting);
// }

const User = mongoose.model('User', userSchema);

module.exports = User;
