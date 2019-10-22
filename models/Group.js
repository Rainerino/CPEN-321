const mongoose = require('mongoose');

const groupSchema = new mongoose.Schema({
  groupName: String,
  userList: [],
}, { timestamps: true });

const Group = mongoose.model('Group', groupSchema);
module.exports = Group;
