const mongoose = require("mongoose");
const Schema = mongoose.Schema;

exports.generateRandomObjectId = (model, callback) => {
  model.estimatedDocumentCount((_err, totalCount) => {
    model.findOne().skip(Math.floor(Math.random() * totalCount)).exec((_err, result) => callback(result._id));
  });
};

exports.generateRandomObjectIdList = (model, length) => {

  const objectIdList = new Schema.Types.ObjectId();
  let i;
  for (i = 0; i < length; i++) {
    const random = Math.floor(Math.random() * model.countDocuments());
    model.findOne().skip(random).exec((_err, result) => {
      objectIdList.push(result._id);
    });
  }
  return objectIdList;
};
