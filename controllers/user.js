const User = require('../models/user');

/**
 * GET /login
 * login with default userid
 *
 */
exports.getLogin = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (existingUser) { return res.json(existingUser); }
    // TODO: add more password and account validation
    return res.status(404).send("Account with that email address doesn't exist.");
  });
};

/**
 * POST /login
 * Sign in using email and password.
 */
exports.postLogin = (req, res, next) => {
  console.log(req.body.email);
  User.find({ email: req.body.email }, (err, existingUser) => {
    if (err) { return next(err); }
    if (existingUser.length > 0) { return res.status(200).json(existingUser); }
    res.status(404).send("Account with that email address doesn't exist.");
  });
};
/**
 * POST /signup
 * Create a new local account.
 */
exports.postSignup = (req, res, next) => {
  const user = new User({
    firstName: req.body.firstName,
    lastName: req.body.lastName,
    email: req.body.email,
    password: req.body.password
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
 * GET /:userId/group
 * get the group list of a user
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
 * GET /:userId/friendList
 * get the friendlist of a user
 */
exports.getFriendList = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (existingUser) {
      return res.status(200).json(existingUser.friendList);
    }
    res.status(404).send("Account with that userID doesn't exist.");
  });
};
/**
 * PUT /:userId/friendList
 * add friendList to a user
 */
exports.putFriendList = (req, res) => {
  console.log(req.body);
  console.log(req.params.userId);
  User.findById(req.body.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (!existingUser) {
      res.status(400).send("Account with that userID doesn't exist.");
    }
  });
  User.findByIdAndUpdate(req.params.userId, { $push: { friendList: req.body.userId } },
    { new: true }, (err, updatedUser) => {
      if (err) { return res.status(400).send("Account with that fromuserID doesn't exist."); }
      res.status(201).json(updatedUser);
    });
};
/**
 * PUT /:userId/group
 * add group to user
 */
exports.putGroup = (req, res) => {
  User.findById(req.body.groupId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (!existingUser) {
      res.status(400).send("Account with that userID doesn't exist.");
    }
  });
  User.findByIdAndUpdate(req.params.userId, { $push: { groupList: req.body.groupId } },
    { new: true }, (err, updatedUser) => {
      if (err) { return res.status(400).send("Account with that fromuserID doesn't exist."); }
      res.status(201).json(updatedUser);
    });
};