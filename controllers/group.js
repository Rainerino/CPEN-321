const Group = require('../models/group');

/**
 * put /group/:groupName
 * create a group, return the group json
 */
// TODO
exports.createGroup = (req, res) => {
  console.log(req.body);
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
 * PUT /group/userlist
 * add user to group
 */
exports.putGroup = (req, res) => {

};

/**
 * PUT /group/:groupId/userlist
 * add users to group's user;ist
 */
exports.addUserList = (req, res) => {

};
