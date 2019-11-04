const mongoose = require('mongoose');

const messageSchema = new mongoose.Schema({
  messageBody: String,
  groupId: { type: mongoose.Schema.Types.ObjectId, ref: 'Group' },
  fromUserId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  toUserId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  // chatroom
}, { timestamps: true });

const Message = mongoose.model('Message', messageSchema);
module.exports = Message;
