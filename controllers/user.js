/**
 * @module controller/user
 * @desc Contains all routes for user model
 */
const { google } = require('googleapis');
const googleAuth = require('google-auth-library');
const { JWT_SECRET, oauth } = require('../config/google_calendar_api');

const ONE_WEEK = 7 * 24 * 60 * 60 * 1000; // One week's time in ms


const User = require('../db/models/user');
const Group = require('../db/models/group');
const Calendar = require('../db/models/calendar');
const Event = require('../db/models/event');

const complexLogic = require('../core/preference');
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
      console.log(existingUser.location.coordinate);
      return res.status(200).json(existingUser);
    }
    res.status(404).send("Account with that userID doesn't exist.");
  });
};
/**
 * @example POST /:userId/location
 * @param {Number}: longitude - location
 * @param {Number}: latitude - location
 * @type {Request}
 * @desc update the location of a user.
 */
exports.postLocation = (req, res) => {
  User.findByIdAndUpdate(req.params.userId,
    { $set: { 'location.coordinate': [req.body.longitude, req.body.latitude] }, },
    (err, existingUser) => {
      if (err) { res.status(400); }
      console.log(existingUser);
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
 * @example GET /:userId/friendlist
 * @param key
 * @type {Request}
 * @desc get the friendlist of a user
 */
exports.getFriendList = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { res.status(400).send('Bad user id.'); }
    if (existingUser) {
      User.userFriendList(existingUser.friendList)
        .then((user) => {
          res.status(200).json(user);
        })
        .catch((err) => {
          res.status(400).send('get friend list errors');
          console.error(err);
        });
    } else {
      res.status(404).send("Account with that userID doesn't exist.");
    }
  });
};
/**
 * @example PUT /:userId/friendList
 * @param {Json} userIdList - list to be added to friendlist
 * @type {Request}
 * @desc add user to another user's friend list
 */
exports.putFriendList = (req, res) => {
  User.find({ _id: req.body.userId }, (err, existingUser) => {
    if (err) { return res.status(400); }
    console.log(existingUser);
    if (!existingUser) {
      return res.status(400).send('Account with that userID doesn\'t exist. 1');
    }
  });
  User.findByIdAndUpdate(req.params.userId, { $addToSet: { friendList: req.body.userId } },
    { new: true }, (err, updatedUser) => {
      if (err) { return res.status(500).send("Account with that from userID doesn't exist. 2"); }
      User.findByIdAndUpdate(req.body.userId, { $addToSet: { friendList: req.params.userId } },
        { new: true }, (err, updatedUser) => {
          if (err) { return res.status(500).send("Account of added userID doesn't exist. 3"); }
        });
      res.status(201).json(updatedUser);
    });
};
/**
 * @example PUT /:userId/group
 * @param {Json}  grouplist - add group list to user
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
 * @example POST /user/:userId/calendar/:calendarId
 * @type {Request}
 * @desc link a calendar to user.
 */
exports.addCalendar = async (req, res) => {
  await Calendar.findOneAndUpdate({ _id: req.params.calendarId }, { $set: { calendarType: 'USER' } }, (err, existingCalendar) => {
    if (err) { res.status(400).send('Calendar not found'); }
    console.log(existingCalendar);
    User.findOneAndUpdate({ _id: req.params.userId }, { $addToSet: { calendarList: req.params.calendarId } },
      (err, updatedUser) => {
        if (err) { res.status(400).send('User not found'); }
        res.status(201).json(updatedUser);
      });
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
 * @example POST /user/:userId/event/eventId
 * @type {Request}
 * @desc add a meeting event to the user, at the same time add the user to the event's list and change the event type.
 */
exports.addEvent = (req, res) => {
  Event.findOneAndUpdate({ _id: req.params.eventId },
    { $addToSet: { userList: req.params.userId } }, (err, existingEvent) => {
      if (err) { res.status(400).send('Event not found'); }
      existingEvent.save();
      console.log(existingEvent);
      User.findOneAndUpdate({ _id: req.params.userId },
        { $addToSet: { scheduleEventList: req.params.eventId } },
        (err, updatedUser) => {
          if (err) { res.status(400).send('User not found'); }
          res.status(201).json(updatedUser);
        });
    });
};
/**
 * @example POST /user/:userId/event/owner/eventId
 * @type {Request}
 * @desc set the owner of the event and also add the user to the userlist of the event
 */
exports.addEventOwner = async (req, res) => {
  await Event.findOneAndUpdate({ _id: req.params.eventId },
    { $set: { ownerId: req.params.userId } }, (err, existingEvent) => {
      if (err) { res.status(400).send('Event not found'); }
      console.log(existingEvent);
      existingEvent.save();
      User.findOneAndUpdate({ _id: req.params.userId },
        { $addToSet: { scheduleEventList: req.params.eventId } },
        (err, updatedUser) => {
          if (err) { res.status(400).send('User not found'); }
          res.status(201).json(updatedUser);
        });
    });
};
/**
 * @example GET /user/:userId/event/
 * @type {Request}
 * @desc return the event of a user
 */
exports.getEvent = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400).send(''); }
    res.status(200).json(existingUser.scheduleEventList);
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
exports.notifySuggestedUser = (req, res) => res.status(500).send('function not implemented');
/**
 * @example DELETE /user/:userId/suggested-friends
 * @param {String} user list to be deleted
 * @type {Request}
 * @desc delete suggested friends from a user
 */
exports.deleteSuggestedFriends = async (req, res) => {
  User.findByIdAndUpdate(req.params.userId,
    { $pullAll: { suggestedFriendList: req.body.userId } }, (err, updatedUser) => {
      if (err) { return res.status(400).send('delete failed'); }
      res.status(200).send('deleted');
    });
};

/**
 * @example GET /user/:userId/event/suggested-friend
 * @description get a list of suggested friends based on data given
 * @param {Date} startTime - the starting time of the event
 * @param {Json} location - the current location of the meeting
 * @return {Array} suggestedFriends - top x people suggested
 */
exports.getMeetingSuggestedFriends = (req, res) => {
  const suggestedBasedOnLocation = complexLogic.collectNearestFriends(req.params.userId);
  const suggestedBasedOnTime = complexLogic.collectFreeFriends();
  res.status(500).send('function not implemented');
};
/**
 * @example GET /user/:userId/preference
 * @description get user's preferences
 */
exports.getPreferences = (req, res) => {
  res.status(500).send('function not implemented');
};
/**
 * @example PUT /user/:userId/preference
 * @description update user's prefernces
 */
exports.putPreferences = (req, res) => {
  res.status(500).send('function not implemented');
};

/**
 * @param {String} credentials
 * save the google calendar of the user.
 */
exports.postGoogleCalendar = async (req, res, next) => {
  const oauth2Client = new googleAuth.OAuth2Client(oauth.google.clientID,
    oauth.google.clientSecret,
    process.env.GOOGLE_REDIRECT_URL);

  // Check the OAuth 2.0 Playground to see request body example,
  // must have Calendar.readonly and google OAuth2 API V2 for user.email
  // and user.info
  oauth2Client.setCredentials(req.body);

  const calendar = google.calendar('v3');

  // This date and time are both ahead by 9 hours, but we only worry
  // about getting the recurring events throughout one week.
  const todays_date = new Date(Date.now());
  const next_week_date = new Date(todays_date.getTime() + ONE_WEEK);

  calendar.calendarList.list({ auth: oauth2Client }, (err, resp) => {
    resp.data.items.forEach((cal) => {
      console.log(`${cal.summary} - ${cal.id}`);
    });
  });

  // Assuming user wants to use their "primary" calendar
  calendar.events.list({
    auth: oauth2Client,
    calendarId: 'primary',
    timeMin: todays_date.toISOString(),
    timeMax: next_week_date.toISOString(),
  }, (err, response) => {
    if (err) {
      console.log(`The API returned an error: ${err}`);
      return;
    }
    const events = response.data.items;
    events.forEach((event) => {
      const start = event.start.dateTime || event.start.date;
      // If it's a recurring event, print it.
      if (event.recurrence) {
        console.log('%s - %s', start, event.summary);
      }
    });
  });
  res.status(200).json({ list: 'events' }); // we need to return the list of events...
};
