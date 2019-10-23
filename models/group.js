const mongoose = require('mongoose');

const groupSchema = new mongoose.Schema({
  groupName: String,
  userList: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
  // chatroom
}, { timestamps: true });

const Group = mongoose.model('Group', groupSchema);
module.exports = Group;
