const mongoose = require('mongoose');

exports.generateRandomObjectId = (model, callback) => {
  model.estimatedDocumentCount((_err, totalCount) => {
    model.findOne().skip(Math.floor(Math.random() * totalCount)).exec((_err, result) => callback(result._id));
  });
};

exports.generateRandomObjectIdList = (model, length) => {
  const objectIdList = [mongoose.Types.ObjectId()];
  let i;
  console.log(length);
  for (i = 0; i < length; i++) {
    const random = Math.floor(Math.random() * model.countDocuments());
    console.log('r %d', random);
    model.findOne().skip(random).exec((_err, result) => {
      objectIdList.push(result._id);
      console.log(result._id);
    });
  }
  return objectIdList;
};
