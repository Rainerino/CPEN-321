/**
 * @module controller/group
 * @desc Contains all routes for group model
 */
const Group = require('../db/models/group');
const User = require('../db/models/user');
const Calendar = require('../db/models/calendar');
const helper = require('./helper');

const logger = helper.getMyLogger('Calendar Controller');

/**
 * @example POST /group/create
 * @desc  create a group,
 * @param {String}  groupName
 * @param {String}  groupDescription
 * @return group
 */
exports.createGroup = (req, res) => {
  const group = new Group({
    groupName: req.body.groupName,
    groupDescription: req.body.groupDescription,
  });
  // TODO: send notification to everyone on the group.
  group.save((err, createdGroup) => {
    if (err) { return res.status(500).send('Save group failed'); }
    res.status(201).json(createdGroup);
  });
};
/**
 * @example Get /group/:groupId
 * @desc get group object
 * @return group
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
