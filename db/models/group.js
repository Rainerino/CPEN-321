const mongoose = require('mongoose');
const Calendar = require('./calendar');
const timestampPlugin = require('../plugins/timeStampUpdate');


const groupSchema = new mongoose.Schema({
  groupName: String,
  groupDescription: String,
  location: {
    type: { type: String },
    coordinate: [{ type: Number }]
  },
  /**
   * @param {Array} userId
   * @param {Array} calendarId
   * Group calendar. A group can only have one calendar.
   * At the creation of a group, each users that are added to the group
   * will also give a calendarId of each users to be merged into
   * the group calendar.
   */
  calendarId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Calendar',
    // autopopulate: true,
    // validate: (value) => Calendar.findOne({ _id: value }, (err, calendar) => {
    //   if (err) return false;
    //   if (calendar.calendarType === 'GROUP') {
    //     return true;
    //   }
    //   return false;
    // })
  },
  userList: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      // autopopulate: true
    }
  ],
});
groupSchema.statics.addCalendarToGroup =
    function (group, calendar) {
      return new Promise((resolve, reject) => {
        this.findByIdAndUpdate(
            group._id,
            { $set: { calendarId: calendar._id } },
            { new: true, useFindAndModify: false},
            async (err, updatedGroup) => {
              if (err) {
                console.log(err);
                return reject(err);
              }
              console.log(updatedGroup);
              resolve(updatedGroup);
              await Calendar.findByIdAndUpdate(
                  calendar._id,
                  { $set: { ownerId: group._id}},
                  { new: true, useFindAndModify: false},
                  async (err, updatedCal) => {
                    if (err) {
                      console.log(err);
                      return reject(err);
                    }
                    console.log(updatedCal);
                    resolve(updatedCal);
                  });
            });
      });
    };
//
// groupSchema.statics.addUsersToGroup =
//     function (group, userList) {
//       return new Promise((resolve, reject) => {
//         this.findByIdAndUpdate(
//             group._id,
//             { $addToSet: { userList: userList } },
//             { new: true, useFindAndModify: false},
//             async (err, updatedUser) => {
//               if (err) {
//                 console.log(err);
//                 return reject(err);
//               }
//               console.log(updatedUser);
//               resolve(updatedUser);
//               await Group.findByIdAndUpdate(
//                   group._id ,
//                   {$addToSet: {userList: user._id}},
//                   { new: true, useFindAndModify: false},
//                   async (err, updatedGroup) => {
//                     if (err) {
//                       console.log(err);
//                       return reject(err);
//                     }
//                     console.log(updatedGroup);
//                     resolve(updatedGroup);
//                   });
//             });
//       });
//     };


groupSchema.plugin(timestampPlugin);
groupSchema.plugin(require('mongoose-deep-populate')(mongoose));

const Group = mongoose.model('Group', groupSchema, 'groups');
module.exports = Group;
