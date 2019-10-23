const mongoose = require('mongoose');

const chatRoomSchema = new mongoose.Schema({
  chatRoomName: String,
  messageList: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Message' }],
  // chatroom
}, { timestamps: true });

const ChatRoom = mongoose.model('ChatRoom', chatRoomSchema);
module.exports = ChatRoom;
