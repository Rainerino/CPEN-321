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
  console.log(req.body);
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
 * GET /:userId/account
 * get the group list of a user
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
 * PUT /:userId/friendList
 * add friendList to a user
 */
exports.putFriendList = (req, res) => {
  User.findById(req.body.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (!existingUser) {
      res.status(400).send('Account with that userID doesn\'t exist.');
    }
  });
  // TODO: need to check and remove repeated user
  User.findByIdAndUpdate(req.params.userId, { $addToSet: { friendList: req.body.userId } },
    { new: true }, (err, updatedUser) => {
      if (err) { return res.status(400).send("Account with that from userID doesn't exist."); }
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
  User.findByIdAndUpdate(req.params.userId, { $addToSet: { groupList: req.body.groupId } },
    { new: true }, (err, updatedUser) => {
      if (err) { return res.status(400).send("Account with that fromuserID doesn't exist."); }
      res.status(201).json(updatedUser);
    });
};
/**
 * PUT 
 */
exports.putCalendar = (req, res) => {

};