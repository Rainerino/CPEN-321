const mongoose = require("mongoose");
const timestampPlugin = require("../plugins/timeStampUpdate");

const messageSchema = new mongoose.Schema({
  messageBody: String,
  groupId: { type: mongoose.Schema.Types.ObjectId, ref: "Group" },
  fromUserId: { type: mongoose.Schema.Types.ObjectId, ref: "User" },
  toUserId: { type: mongoose.Schema.Types.ObjectId, ref: "User" },
  // chatroom
});

messageSchema.plugin(timestampPlugin);
const Message = mongoose.model("Message", messageSchema);
module.exports = Message;
