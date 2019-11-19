/**
 * @module controller/group
 * @desc Contains all routes for group model
 */
const Group = require('../db/models/group');
const Calendar = require('../db/models/calendar');
/**
 * @example PUT /create
 * @desc * create a group,
 * @param groupName - String
 * @param groupDescription - String
 */
exports.createGroup = (req, res) => {
  const group = new Group({
    groupName: req.body.groupName,
    groupDescription: req.body.groupDescription,
  });
  group.save((err, createdGroup) => {
    if (err) { return res.status(500).send('Save group failed'); }
    res.status(201).json(createdGroup);
  });
};
/**
 * @example Get /group/:groupId
 * @desc get group object
 */
exports.getGroup = (req, res) => {
  Group.findById(req.params.groupId, (err, existingGroup) => {
    if (err) { return res.status(400); }
    if (existingGroup) { 
      return res.status(200).json(existingGroup); 
    }
    return res.status(404).send("Group with the given group Id doesn't exist.");
  });
};
/**
 * @example GET /group/:groupId/user-list
 * @desc get the user's list
 */
exports.getUserList = (req, res) => {
  Group.findById(req.params.groupId, (err, existingGroup) => {
    if (err) { res.status(400).send('Bad group id.'); }
    if (existingGroup) {
      Group.groupUserList(existingGroup.userList)
        .then((user) => {
          res.status(200).json(user);
        })
        .catch((err) => {
          res.status(400).send('get user list errors');
          console.error(err);
        });
    } else {
      res.status(404).send("Account with that groupId doesn't exist.");
    }
  });
};

/**
 * @example GET /group/:groupId/user-name-list
 * @desc get the user's list
 */
exports.getUserNameList = (req, res) => {
  Group.findById(req.params.groupId, (err, existingGroup) => {
    if (err) { res.status(400).send('Bad group id.'); }
    if (existingGroup) {
      Group.groupUserNameList(existingGroup.userList)
        .then((userName) => {
          res.status(200).json(userName);
        })
        .catch((err) => {
          res.status(400).send('get user list errors');
          console.error(err);
        });
    } else {
      res.status(404).send("Account with that groupId doesn't exist.");
    }
  });
};

/**
 * @example PUT /group/:groupId/add-user
 * @param userId - ObjectId
 * @desc add users to group's user;ist
 */
exports.addUser = (req, res) => {
  User.findById(req.body.userId, (err, user) => {
    if (err) {
      console.log(err);
      return res.status(400).send('User not found');
    }
    Group.findById(req.body.groupId, (err, group) => {
      if (err) {
        console.log(err);
        return res.status(400).send('Group not found');
      }
      User.addGroupToUser(user, group).then((result) => {
        console.log(result);
        return res.status(200).send('added user to group');
      }, (err) => {
        console.log(err);
        return res.status(500).send('Adding user to group failed!');
      });
    });
  });
};
/**
 * @example POST /group/:groupId/calendar
 * @desc get group's calendarId
 */
exports.getCalendarId = (req, res) => {
  Group.findById(req.params.groupId, (err, existingGroup) => {
    if (err) { return res.status(400); }
    if (existingGroup) { return res.json(existingGroup.calendarId); }
    return res.status(404).send("Group with the given group Id doesn't exist.");
  });
};
/**
 * @example PUT /group/set-calendar
 * @param groupId - ObjectId
 * @param calendarId - ObjectId
 * @desc set group's calendar and return updated group in the end.
 */
exports.setCalendar = (req, res) => {
  Calendar.findById(req.param.calendarId, (err, calendar) => {
    if (err) return res.status(400).send('Bad calednar id');
    if (!calendar) return res.status(400).send('Calendar not found');
    console.log(calendar);
  });
  Group.findByIdAndUpdate(req.param.groupId,
    { $set: { getCalendarId: req.param.calendarId } },
    { new: true, useFindAndModify: false },
    (err, group) => {
      if (err) return res.status(400).send('Bad group id');
      return res.status(200).json(group);
    });
};
