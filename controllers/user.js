const User = require('../models/user');
const Group = require('../models/group');
const Calendar = require('../models/calendar');

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
 * add user to another user's friend list
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
 * PUT /:userId/group
 * add group to user
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
 * POST /user/:userId/calendar/:calendarName
 * create a new calendar for the users
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
 * GET /user/:userId/calendar
 * 
 */
exports.getCalendar = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400).send(''); }
    res.status(200).json(existingUser.calendarList);
  });
};
/**
 * GET /user/:userId/suggested-friends
 * 
 */
exports.getSuggestedFriends = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400).send(''); }
    res.status(200).json(existingUser.suggestedFriendList);
  });
};
/**
 * PUT /user/:userId/suggested-friends
 * 
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
 * DELETE /user/:userId/suggested-friends
 */
exports.deleteSuggestedFriends = (req, res) => {
  User.findByIdAndUpdate(req.params.userId,
    { $pullAll: { suggestedFriendList: req.body.userId } }, (err, updatedUser) => {
      if (err) { return res.status(400).send('delete failed'); }
      res.status(200).send('deleted');
    });
};
