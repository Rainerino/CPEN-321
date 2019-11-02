/**
 * @module controller/group
 * @desc Contains all routes for group model
 */
const Group = require('../db/models/group');

/**
 * put /group/:groupName
 * create a group, return the group json
 */
exports.createGroup = (req, res) => {
  const group = new Group({
    groupName: req.params.groupName,
  });
  Group.findOne({ groupName: req.params.groupName }, (err, existingGroup) => {
    if (err) { return res.status(201).send(); }
    if (existingGroup) {
      return res.status(403).send('Account with that email address already exists.');
    }
    group.save((err, createdGroup) => {
      if (err) { return res.status(500).send('Save group failed'); }
      res.status(201).json(createdGroup);
    });
  });
};
/**
 * Get /group/:groupId
 * get group
 */
exports.getGroup = (req, res) => {
  Group.findById(req.params.groupId, (err, existingGroup) => {
    if (err) { return res.status(400); }
    if (existingGroup) { return res.json(existingGroup); }
    return res.status(404).send("Group with the given group Id doesn't exist.");
  });
};

/**
 * PUT /group/:groupId/userlist
 * add users to group's user;ist
 */
exports.addUserList = (req, res) => {
  res.status(501).send('Not implemented');
};
/**
 * POST /group/calendar
 * create a new calendar
 */
exports.createCalendar = (req, res) => {
  res.status(501).send('Not implemented');
};
/**
 * PUT /group/calendar/:calendarId
 */
exports.putCalendar = (req, res) => {
  res.status(501).send('Not implemented');
};
