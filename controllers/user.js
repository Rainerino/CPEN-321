/**
 * @module controller/user
 * @desc Contains all routes for user model
 */

const User = require('../models/user');
const Group = require('../models/group');
const Calendar = require('../models/calendar');

/**
 * @example POST /login
 * @param {String} email and password of the user
 * @type {Request}
 * @desc Sign in using email and password.
 */
exports.postLogin = (req, res, next) => {
  User.findOne({ email: req.body.email }, (err, existingUser) => {
    if (err) { return next(err); }
    if (existingUser) {
      if (req.body.password) {
        if (existingUser.password === req.body.password) {
          return res.status(200).json(existingUser);
        }
        return res.status(403).send('Wrong password');
      }
      return res.status(400).send('Need password');
    }
    res.status(404).send("Account with that email address doesn't exist.");
  });
};
/**
 * @example POST /signup
 * @param {String} the whole user object
 * @type {Request}
 * @desc Create a new local account.
 */
exports.postSignup = (req, res, next) => {
  const user = new User({
    firstName: req.body.firstName,
    lastName: req.body.lastName,
    email: req.body.email,
    password: req.body.password,
    groupList: [],
    friendList: [],
    suggestedFriendList: [],
    calendarList: []
  });
  User.findOne({ email: req.body.email }, (err, existingUser) => {
    if (err) { return next(err); }
    if (existingUser) {
      return res.status(403).send('Account with that email address already exists.');
    }
    // validation needed
    user.save((err, createdUser) => {
      if (err) { return next(err); }
      res.status(201).json(createdUser);
    });
  });
};
/**
 * @example GET /:userId/account
 * @param {String} key
 * @type {Request}
 * @desc get the group list of a user
 */
exports.getUser = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (existingUser) {
      return res.status(200).json(existingUser);
    }
    res.status(404).send("Account with that userID doesn't exist.");
  });
};
/**
 * @example GET /:userId/group
 * @param {String} key
 * @type {Request}
 * @desc get the group list of a user
 */
exports.getGroup = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (existingUser) {
      return res.status(200).json(existingUser.groupList);
    }
    res.status(404).send("Account with that userID doesn't exist.");
  });
};
/**
 * @example GET /:userId/friendList
 * @param key
 * @type {Request}
 * @desc get the friendlist of a user
 */
exports.getFriendList = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { res.status(400).send('Bad user id.'); }
    if (existingUser) {
      User.find({ _id: existingUser.friendList }, (err, user) => {
        if (err) { res.status(400).send('get friend list errors'); }
        res.status(200).json(user);
      });
    } else {
      res.status(404).send("Account with that userID doesn't exist.");
    }
  });
};
/**
 * @example PUT /:userId/friendList
 * @param {String} user list to be added to friendlist
 * @type {Request}
 * @desc add user to another user's friend list
 */
exports.putFriendList = (req, res) => {
  User.findById(req.body.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (!existingUser) {
      res.status(400).send('Account with that userID doesn\'t exist.');
    }
  });
  User.findByIdAndUpdate(req.params.userId, { $addToSet: { friendList: req.body.userId } },
    { new: true }, (err, updatedUser) => {
      if (err) { return res.status(400).send("Account with that from userID doesn't exist."); }
      User.findByIdAndUpdate(req.body.userId, { $addToSet: { friendList: req.params.userId } },
        { new: true }, (err, updatedUser) => {
          if (err) { return res.status(400).send("Account of added userID doesn't exist."); }
          res.status(201).json(updatedUser);
        });
    });
};
/**
 * @example PUT /:userId/group
 * @param {Json}  add group list to user
 * @type {Request}
 * @desc add group to user
 */
exports.putGroup = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (existingUser) {
      User.findByIdAndUpdate(req.params.userId, { $addToSet: { groupList: req.body.groupId } },
        { new: true }, (err, updatedUser) => {
          if (err) { return res.status(400).send('Bad request due to duplication'); }
          Group.findByIdAndUpdate(req.body.groupId, { $addToSet: { userList: req.params.userId } },
            { new: true }, (err, updatedGroup) => {
              if (err) { return res.status(400).send('Bad request due to duplication'); }
              res.status(201).json(updatedUser);
            });
        });
    } else {
      res.status(400).send("Account with that userID doesn't exist.");
    }
  });
};
/**
 * @example POST /user/:userId/calendar/:calendarName
 * @type {Request}
 * @desc create a new calendar for the users
 */
exports.createCalendar = (req, res) => {
  const calendar = new Calendar({
    calendarName: req.params.calendarName,
    userId: req.params.userId
  });
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { res.status(201).send(); }
    if (existingUser) {
      calendar.save((err, createdCalendar) => {
        if (err) { res.status(500).send('Save Calendar failed'); }
        console.log(createdCalendar._id);
        User.findByIdAndUpdate(req.params.userId, { $addToSet: { calendarList: createdCalendar._id } },
          { new: true }, (err, updatedUser) => {
            if (err) { return res.status(400).send(''); }
            console.log(updatedUser);
            res.status(201).json(createdCalendar);
          });
      });
    } else {
      res.status(400).send('User doesn\'t exists.');
    }
  });
};
/**
 * @example GET /user/:userId/calendar
 * @type {Request}
 * @desc get the calendar from the user
 */
exports.getCalendar = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400).send(''); }
    res.status(200).json(existingUser.calendarList);
  });
};
/**
 * @example GET /user/:userId/suggested-friends
 * @type {Request}
 * @desc get suggested friends list from a user
 */
exports.getSuggestedFriends = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400).send(''); }
    res.status(200).json(existingUser.suggestedFriendList);
  });
};
/**
 * @example PUT /user/:userId/suggested-friends
 * @param {Json} suggested user list 
 * @type {Request}
 * @desc add suggested friends to a user
 */
exports.putSuggestedFriends = (req, res) => {
  User.findById(req.body.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (existingUser) {
      User.findByIdAndUpdate(req.params.userId, { $addToSet: { suggestedFriendList: req.body.userId } },
        { new: true }, (err, updatedFromUser) => {
          if (err) { res.status(400).send("Account with that from userID doesn't exist."); }
          User.findByIdAndUpdate(req.body.userId, { $addToSet: { suggestedFriendList: req.params.userId } },
            { new: true }, (err, updatedAddedUser) => {
              if (err) { res.status(400).send("Account of added userID doesn't exist."); }
              res.status(201).json(updatedFromUser);
            });
        });
    } else {
      res.status(400).send('Account with that userID doesn\'t exist.');
    }
  });
};
/**
 * @example POST /user/:userId/suggested-friends/:toUserId
 * @param 
 * @type {Request}
 * @desc create a new suggest new friend notification
 */
exports.notifySuggestedUser = (req, res) => {
  return res.status(500).send('function not implemented');
};
/**
 * @example DELETE /user/:userId/suggested-friends
 * @param {String} user list to be deleted
 * @type {Request}
 * @desc delete suggested friends from a user
 */
exports.deleteSuggestedFriends = (req, res) => {
  User.findByIdAndUpdate(req.params.userId,
    { $pullAll: { suggestedFriendList: req.body.userId } }, (err, updatedUser) => {
      if (err) { return res.status(400).send('delete failed'); }
      res.status(200).send('deleted');
    });
};
