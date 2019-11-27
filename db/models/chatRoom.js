const mongoose = require("mongoose");
const timestampPlugin = require("../plugins/timeStampUpdate");

const chatRoomSchema = new mongoose.Schema({
  chatRoomName: String,
  messageList: [{ type: mongoose.Schema.Types.ObjectId, ref: "Message" }],
  // chatroom
});

chatRoomSchema.plugin(timestampPlugin);
const ChatRoom = mongoose.model("ChatRoom", chatRoomSchema);
module.exports = ChatRoom;
