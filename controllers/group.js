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
  // TODO: send notification to everyone on the group.
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
exports.getUserList = async (req, res) => {
  if (!helper.checkNullArgument(1, req.params.groupId)) {
    return res.status(400).send('Null input');
  }
  // check if group is valid.
  let group;
  try {
    group = await Group.findById(req.params.groupId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(400).send(e.toString());
  }

  // change user id to user object list
  try {
    const result = await User.id2ObjectList(group.userList);
    logger.debug(`get user object list success: ${result.length} users returned`);
    return res.status(200).json(result);
  } catch (e) {
    logger.err(e.toString());
    return res.status(500).send(e.toString());
  }
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
 * @example PUT /add/user
 * @param userId - ObjectId
 * @param groupId - ObjectId
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
 * @example DELETE /group/user
 * @param {ObjectId} groupId
 * @param {ObjectId} userId -
 * @desc remove the user and its calendar from the group
 */
exports.deleteUser = (req, res) => {
  // TODO: complete this
  res.status(500).send('not implemneted');
};