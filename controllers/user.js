/**
 * @module controller/user
 * @desc Contains all routes for user model
 */
const { google } = require('googleapis');

const googleAuth = require('google-auth-library');
const JWT = require('jsonwebtoken');
const { JWT_SECRET, oauth } = require('../config/index');
const helper = require('./helper');
const calendarHelper = require('./calendar/calendar_helper');
const logger = helper.getMyLogger('User Controller');
const ONE_WEEK = 7 * 24 * 60 * 60 * 1000; // One week's time in ms

const User = require('../db/models/user');
const Group = require('../db/models/group');
const Calendar = require('../db/models/calendar');
const Event = require('../db/models/event');

const complexLogicFriend = require('../core/preference');
const complexLogicUser = require('../core/suggestion');

/* Used to sign JSON Web Tokens for new users */
const signToken = (user) => JWT.sign({
  iss: 'Nimanasiribrah',
  sub: user.id,
  iat: new Date().getTime(), // current time
  exp: new Date().setDate(new Date().getDate() + 365), // current time + 365 days
}, JWT_SECRET);

/**
 * @example POST /login
 * @param {String} email and password of the user
 * @type {Request}
 * @desc Sign in using email and password.
 */
exports.postLogin = (req, res) => {
  User.findOne({ email: req.body.email },
    async (err, existingUser) => {
      if (err) { return res.status(500).send(err); }
      if (existingUser) {
        if (req.body.password) {
          const isMatch = await existingUser.isValidPassword(req.body.password);
          // Shouldn't need the OR statement, but it's there for the already made accounts

          if (existingUser.password === req.body.password) {
            // disabled passport for testing
            const token = signToken(existingUser);
            return res.status(200).json(existingUser);
          }
          return res.status(403).send('Wrong password');
        }
        return res.status(400).send('Need password');
      }
      return res.status(404).send("Account with that email address doesn't exist.");
    });
};

/* test for JWT */
exports.secret = (req, res, next) => {
  res.json({ secret: 'resource' });
};

/**
 * @example POST /signup
 * @param {String} the whole user object
 * @type {Request}
 * @desc Create a new local account.
 */
exports.postSignup = (req, res) => {
  const user = new User({
    firstName: req.body.firstName,
    lastName: req.body.lastName,
    email: req.body.email,
    password: req.body.password,
    groupList: [],
    friendList: [],
    suggestedFriendList: [],
    calendarList: [],
    scheduleEventList: [],
    meetingNotification: true,
  });
  User.findOne({ email: req.body.email }, (err, existingUser) => {
    if (err) { return res.status(500).send(err); }
    if (existingUser) {
      return res.status(403).send('Account with that email address already exists.');
    }
    // validation needed
    user.save((err, createdUser) => {
      if (err) { return res.status(500).send(err); }

      const token = signToken(createdUser);
      // res.status(201).json({ token, createdUser });
      return res.status(201).json(createdUser);
    });
  });
};
/**
 * @example PUT /notification-token
 * @param {String} token - firebase notification token
 * @type {Request}
 * @desc set the notification token
 */
exports.notificationToken = async (req, res) => {
  console.log(req.body);
  if (!req.body.token) {
    return res.status(400).send('Token not given');
  }
  User.findByIdAndUpdate(req.body.userId,
    { $set: { firebaseRegistrationToken: req.body.token } },
    { new: true, useFindAndModify: false },
    (err, user) => {
      if (err) return res.status(500).send(err);
      if (!user) return res.status(404).send('No user found.');
      return res.status(200).json(user);
    });
};
/**
 * @example GET /all
 * @type {Request}
 * @desc get the group list of a user
 */
exports.getAllUser = async (req, res) => {
  const allUserList = await User.getUsers();
  res.status(200).json(allUserList);
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
 * @example PUT /user/location
 * @param {Number}: longitude - location
 * @param {Number}: latitude - location
 * @type {Request}
 * @desc update the location of a user.
 */
exports.putLocation = (req, res) => {
  if (!req.body.userId) {
    return res.status(400).send('No userId given');
  }
  User.findByIdAndUpdate(req.body.userId,
    { $set: { 'location.coordinate': [req.body.longitude, req.body.latitude] }, },
    { new: true, useFindAndModify: false },
    (err, existingUser) => {
      if (err) {
        console.log(err);
        return res.status(500).send(err);
      }
      if (!existingUser) {
        return res.status(404).send('No user found');
      }
      return res.status(200).json(existingUser);
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
 * @example PUT /add/friend
 * @param {Objectid} userId - objectId of the user
 * @param {Objectid} friendId - objectId of the friend
 * @type {Request}
 * @desc add user to another user's friend list
 */
exports.putFriendList = async (req, res) => {
  try {
    // get the two users. the order doesn't matter.
    const fromUser = await User.findById(req.body.userId).orFail();
    const toUser = await User.findById(req.body.friendId).orFail();
    await User.addFriendToUser(fromUser, toUser);
    return res.status(200).send('Successfully added');
  } catch (e) {
    console.log(e);
    return res.status(404).send(e.Messages());
  }
};

/**
 * @example PUT /add/group
 * @param {Objectid} userId - objectId of the user
 * @param {Objectid} groupId - objectId of the group
 * @type {Request}
 * @desc add group to user
 */
exports.putGroup = async (req, res) => {
  try {
    const user = await User.findById(req.body.userId).orFail();
    const group = await Group.findById(req.body.groupId).orFail();
    await User.addGroupToUser(user, group);
    return res.status(200).send('Successfully add user to the group');
  } catch (e) {
    console.log(e);
    return res.status(404).send(e.Messages());
  }
};
/**
 * @example POST /user/calendar/add
 * @type {Request}
 * @param {ObjectId} userId - objectId of the user
 * @param {ObjectId} calendarId - objectId of the calendar
 * @desc link a calendar to user.
 */
exports.addCalendar = async (req, res) => {
  try {
    const user = await User.findById(req.body.userId).orFail();
    const calendar = await Calendar.findById(req.body.calendar).orFail();
    await User.addCalendarToUser(user, calendar);
    return res.status(200).send('Successfully add calendar to the user');
  } catch (e) {
    console.log(e);
    return res.status(404).send(e.Messages());
  }
};

/**
 * @example POST /user/add/event
 * @type {Request}
 * @param {ObjectId} userId - user to add the meeting to
 * @param {ObjectId} eventId - add meeting events to users.
 * @param {Boolean} isOwner - true if it's the owner, false if not
 * @desc add a meeting event to the user, at the same time add the user to the event's list and change the event type.
 */
exports.addEvent = async (req, res) => {
  if (!helper.checkNullArgument(3, req.body.userId, req.body.eventId, req.body.isOwner)) {
    logger.warn('Null input');
    return res.status(400).send('Null input');
  }
  let user;
  let event;
  try {
    user = await User.findById(req.body.userId).orFail();
    event = await Event.findById(req.body.eventId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }
  if (event.eventType !== 'MEETING') {
    logger.warn(`${event.eventName} is not a meeting`);
    return res.status(400).send('Bad event type');
  }
  try {
    await User.addMeetingToUser(user, event, req.body.isOwner);
    logger.info(`add ${event.eventName} to ${user.firstName}`);
    return res.status(200).send('Successfully add meeting event to the user');
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
};
/**
 * @example GET /user/:userId/event/:date
 * @type {Request}
 * @desc return the meeting events of a user
 */
exports.getEventsOfDay = async (req, res) => {
  if (!helper.checkNullArgument(2, req.params.userId, req.params.date)) {
    return res.status(400).send('Null input');
  }
  // check if date is valid or not
  const date = new Date(req.params.date);

  if (!(date instanceof Date && !isNaN(date))) {
    logger.warn(`Bad input with ${req.params.date}`);
    return res.status(400).send('Bad date input');
  }

  logger.debug(`Date to return is ${date.toDateString()}`);

  let user;
  let calendar;

  // check if users are valid or not
  try {
    user = await User.findById(req.params.userId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(400).send(e.toString());
  }

  // check if the calendar is valid or not
  try {
    calendar = await Calendar.findById(user.calendarList[0]).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(400).send(e.toString());
  }

  // get the event list of the date, and update all the events.
  try {
    const eventList = await calendarHelper.getEventsOfDay(calendar.eventList, date);
    const meetingList = await calendarHelper.getEventsOfDay(user.scheduleEventList, date);
    logger.info(`get eventList length of ${eventList.length + meetingList.length}`);
    return res.status(200).json(eventList.concat(meetingList));
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
};
/**
 * @example GET /user/:userId/suggested-friends
 * @type {Request}
 * @desc get suggested friends list from a user
 */
exports.getSuggestedFriends = async (req, res) => {
  const user = await User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(500).send(err); }
    if (!existingUser) return res.status(400).send('Bad User Id');
  });
  const userList = await complexLogicUser.suggestNearbyUser(req.params.userId);
  // update the user's suggested friend list
  await user.update({ $set: { suggestedFriendList: userList } });
  await user.save();
  const userObjectList = await User.userFriendList(user.suggestedFriendList);
  return res.status(200).json(userObjectList);
};

/**
 * @example GET /user/:userId/event/suggested-meeting-users/:startTime/:endTime
 * @description get a list of suggested friends based on data given
 * @return {Array} suggestedFriends - top x people suggested
 */
exports.getMeetingSuggestedFriends = async (req, res) => {
  const suggestedBasedOnLocation
    = await complexLogicFriend.collectNearestFriends(req.params.userId);
  const suggestedBasedOnTime
    = await complexLogicFriend.collectFreeFriends(
      req.params.userId,
      req.params.startTime,
      req.params.endTime
    );

  await console.log(suggestedBasedOnLocation);
  await console.log(suggestedBasedOnTime);
  // const result = arrayUnique(suggestedBasedOnLocation.concat(suggestedBasedOnTime));
  const result = await helper.findCommonElement(suggestedBasedOnLocation, suggestedBasedOnTime);
  const userList = await User.userFriendList(result);
  await logger.info(`meeting suggesting ${userList.length} users from ${req.params.startTime} to ${req.params.endTime}`);
  return res.status(200).json(userList);
};
/**
 * @example POST /google-calendar
 * @param {String} credentials
 * save the google calendar of the user.
 */
exports.postGoogleCalendar = async (req, res, next) => {
  const oauth2Client = new googleAuth.OAuth2Client(oauth.google.clientID,
    oauth.google.clientSecret,
    process.env.GOOGLE_REDIRECT_URL);

  /*
   * Check the OAuth 2.0 Playground to see request body example,
   * must have Calendar.readonly and google OAuth2 API V2 for user.email
   * and user.info
   */
  oauth2Client.setCredentials(req.body);

  const calendar = google.calendar('v3');

  /*
   * This date and time are both ahead by 9 hours, but we only worry
   * about getting the recurring events throughout one week.
   */
  const todays_date = new Date(Date.now());
  const next_week_date = new Date(todays_date.getTime() + ONE_WEEK);

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
