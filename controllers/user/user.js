/**
 * @module controller/user
 * @desc Contains all routes for user model
 */
const { google } = require('googleapis');
const validator = require('validator');
const googleAuth = require('google-auth-library');
const JWT = require('jsonwebtoken');
const { JWT_SECRET, oauth } = require('../../config');
const helper = require('../helper');
const calendarHelper = require('../calendar/calendar_helper');

const User = require('../../db/models/user');
const Group = require('../../db/models/group');
const Calendar = require('../../db/models/calendar');
const Event = require('../../db/models/event');
const userHelper = require('./user_helper');

const complexLogicFriend = require('../../core/preference');
const complexLogicUser = require('../../core/suggestion');

const logger = helper.getMyLogger('User Controller');

/**
 * @example POST /user/login
 * @param {String} email
 * @param {String} password - of the user
 * @type {Request}
 * @desc Sign in using email and password.
 * @return user
 */
exports.postLogin = async (req, res) => {
  if (!helper.checkNullArgument(2, req.body.email, req.body.password)) {
    return res.status(400).send('Null input');
  }
  let user;
  try {
    user = await User.findOne({ email: req.body.email }).orFail();
  } catch (e) {
    logger.warn('User donesn\'t exist');
    return res.status(404).send("Account with that email address doesn't exist.");
  }
  if (user.password === req.body.password) {
    const token = userHelper.signToken(user);
    return res.status(200).set({
      'content-type': 'application/json',
      Authorization: token
    }).json(user);
  }
  logger.warn('Wrong password');
  return res.status(403).send('Wrong password');
};
/**
 * @example POST /user/signup
 * @param {String} the whole user object
 * @type {Request}
 * @desc Create a new local account.
 * @return user
 */
exports.postSignup = async (req, res) => {
  if (!helper.checkNullArgument(4, req.body.email, req.body.password,
    req.body.firstName, req.body.lastName)) {
    return res.status(400).send('Null input');
  }
  logger.debug(req.body);

  // TODO: save the location with defaulted to current server location.
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

  const err = await user.validateSync();

  // check if the user object is valid or not.
  if (err) {
    logger.warn('Save user unsuccessful!');
    return res.status(400).send('Bad request');
  }
  // check if the user already exist
  let createdUser;
  try {
    createdUser = await User.findOne({ email: req.body.email });
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }

  if (createdUser) {
    logger.warn('User already exist');
    return res.status(403).send('Account with that email address already exists.');
  }

  // save the user
  try {
    createdUser = await User.create(user);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(500).send(e.toString());
  }

  // check if the user is null
  if (!createdUser) {
    logger.error('Save user unsuccessful!');
    return res.status(500).send('Bad request');
  }
  // create a calendar for User, save it to the user ith a default name
  try {
    const calendar = await Calendar.create({
      calendarName: `${await user.firstName}'s first calendar`,
      ownerId: await user._id
    });

    await createdUser.update({ $addToSet: { calendarList: calendar._id } });
    await createdUser.save();
    logger.info('User created');
    logger.debug(createdUser);
    const token = userHelper.signToken(createdUser);
    return res.status(201).set({
      'content-type': 'application/json',
      Authorization: token
    }).json(createdUser);
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
};
/**
 * @example PUT /user/notification-token
 * @param {String} token - firebase notification token
 * @type {Request}
 * @desc set the notification token
 * @return user
 */
exports.notificationToken = async (req, res) => {
  if (!helper.checkNullArgument(1, req.body.token)) {
    return res.status(400).send('Null input');
  }
  let user;
  // update user
  try {
    user = await User.findByIdAndUpdate(req.body.userId,
      { $set: { firebaseRegistrationToken: req.body.token } },
      { new: true, useFindAndModify: false }).orFail();
    return res.status(200).json(user);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }
};
/**
 * @example GET /user/all
 * @type {Request}
 * @desc get the group list of a user
 * @return {Array} user
 */
exports.getAllUser = async (req, res) => {
  try {
    const allUserList = await User.getUsers();
    return res.status(200).json(allUserList);
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
};
/**
 * @example GET /user/:userId/account
 * @type {Request}
 * @desc get the group list of a user
 * @return user
 */
exports.getUser = async (req, res) => {
  if (!helper.checkNullArgument(1, req.params.userId)) {
    return res.status(400).send('Null input');
  }
  let user;
  try {
    user = await User.findById(req.params.userId).orFail();
    logger.debug(user);
    return res.status(200).json(user);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }
};
/**
 * @example PUT /user/location
 * @param {ObjectId} userId
 * @param {Number}: longitude - location
 * @param {Number}: latitude - location
 * @type {Request}
 * @desc update the location of a user.
 * @return user
 */
exports.putLocation = async (req, res) => {
  if (!helper.checkNullArgument(3, req.body.userId, req.body.longitude, req.body.latitude)) {
    return res.status(400).send('Null input');
  }

  if (!(req.body.longitude > -180 && req.body.longitude < 180)) {
    return res.status(400).send('Bad longitude');
  }
  if (!(req.body.latitude > -90 && req.body.latitude < 90)) {
    return res.status(400).send('Bad latitude');
  }
  // update user
  let user;
  try {
    user = await User.findByIdAndUpdate(req.body.userId,
      { $set: { 'location.coordinate': [req.body.longitude, req.body.latitude] }, },
      { new: true, useFindAndModify: false }).orFail();
    return res.status(200).json(user);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }
};
/**
 * @example GET /user/:userId/friendlist
 * @type {Request}
 * @desc get the friendlist of a user
 * @return {Array} - user
 */
exports.getFriendList = async (req, res) => {
  if (!helper.checkNullArgument(1, req.params.userId)) {
    return res.status(400).send('Null input');
  }
  let user;
  try {
    user = await User.findById(req.params.userId).orFail();
  } catch (e) {
    logger.warn('User not found');
    return res.status(404).send('User not found');
  }
  const userObjectList = await User.id2ObjectList(user.friendList);
  logger.info(`${user.firstName} returned ${userObjectList.length} friends`);
  return res.status(200).json(userObjectList);
};
/**
 * @example PUT /user/add/friend
 * @param {Objectid} userId - objectId of the user
 * @param {Objectid} friendId - objectId of the friend
 * @type {Request}
 * @desc add user to another user's friend list
 * @return user
 */
exports.putFriendList = async (req, res) => {
  if (!helper.checkNullArgument(2, req.body.userId, req.body.friendId)) {
    return res.status(400).send('Null input');
  }
  try {
    // get the two users. the order doesn't matter.
    const fromUser = await User.findById(req.body.userId).orFail();
    const toUser = await User.findById(req.body.friendId).orFail();
    await User.addFriendToUser(fromUser, toUser);
    return res.status(200).json(fromUser);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }
};
/**
 * @example DELETE /user/delete/friend
 * @param {Objectid} userId - objectId of the user
 * @param {Objectid} friendId - objectId of the friend
 * @type {Request}
 * @desc delete a user from another's friendlist. it's a mutual delete
 * @return user
 */
exports.deleteFriend = async (req, res) => {
  if (!helper.checkNullArgument(2, req.body.userId, req.body.friendId)) {
    return res.status(400).send('Null input');
  }
  let fromUser;
  let toUser;
  try {
    // get the two users. the order doesn't matter.
    fromUser = await User.findById(req.body.userId).orFail();
    toUser = await User.findById(req.body.friendId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }
  try {
    await User.deleteFriendFromUser(fromUser, toUser);
    logger.info(`Deleted ${toUser.firstName} from ${fromUser.firstName}`);
    return res.status(200).json(fromUser);
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
};
/**
 * @example PUT /user/add/group
 * @param {Objectid} userId - objectId of the user
 * @param {Objectid} groupId - objectId of the group
 * @type {Request}
 * @desc add group to user
 * @return group
 */
exports.putGroup = async (req, res) => {
  if (!helper.checkNullArgument(2, req.body.userId, req.body.groupId)) {
    return res.status(400).send('Null input');
  }
  try {
    const user = await User.findById(req.body.userId).orFail();
    const group = await Group.findById(req.body.groupId).orFail();
    await User.addGroupToUser(user, group);
    return res.status(200).json(group);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }
};
/**
 * @example DELETE /user/delete/group
 * @param {Objectid} userId - objectId of the user
 * @param {Objectid} groupId - objectId of the group
 * @type {Request}
 * @desc delete a user from a group
 * @return group
 */
exports.deleteGroup = async (req, res) => {
  if (!helper.checkNullArgument(2, req.body.userId, req.body.groupId)) {
    return res.status(400).send('Null input');
  }
  try {
    const user = await User.findById(req.body.userId).orFail();
    const group = await Group.findById(req.body.groupId).orFail();
    await User.deleteGroupFromUser(user, group);
    return res.status(200).json(group);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }
};
/**
 * @example POST /user/add/event
 * @type {Request}
 * @param {ObjectId} userId - user to add the meeting to
 * @param {ObjectId} eventId - add meeting events to users.
 * @param {Boolean} isOwner - true if it's the owner, false if not
 * @desc add a meeting event to the user, at the same time add the user to the event's list
 * and change the event type.
 * @return event
 */
exports.addEvent = async (req, res) => {
  logger.debug(req.body);
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
    return res.status(200).send(event);
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
};
/**
 * @example DELETE /user/delete/event/user
 * @param {ObjectId} userId - the user id of the user to be removed from
 * @param {ObjectID} eventId - the meeting to remove the user from
 * @desc delete a user from a meeting event.
 * @return user
 */
exports.deleteUserFromEvent = async (req, res) => {
  if (!helper.checkNullArgument(2, req.body.userId, req.body.eventId)) {
    return res.status(400).send('Null input');
  }
  let user;
  let event;

  // get the user and event from database.
  try {
    user = await User.findById(req.body.userId).orFail();
    event = await Event.findById(req.body.eventId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }

  try {
    // remove user from meeting
    await User.removeMeetingFromUser(user, event, event.ownerId === user._id);
    const msg = `${event.ownerId === user._id ? 'owner' : 'member'} removed from ${event.eventName} meeting`;
    logger.info(msg);
    return res.status(200).json(user);
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e.toString());
  }
};
/**
 * @example GET /user/:userId/event/:date
 * @type {Request}
 * @desc return the meeting events of a user on given date.
 * @return {Array} eventId
 */
exports.getEventsOfDay = async (req, res) => {
  if (!helper.checkNullArgument(2, req.params.userId, req.params.date)) {
    return res.status(400).send('Null input');
  }
  // check if date is valid or not
  const date = new Date(req.params.date);

  // eslint-disable-next-line no-restricted-globals
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
  if (calendar.eventList.length === 0 && user.scheduleEventList.length === 0) {
    logger.warn('The event lists are empty');
    return res.status(200).json([]);
  }
  // get the event list of the date, and update all the events.
  let eventList = [];
  let meetingList = [];

  try {
    if (calendar.eventList.length !== 0) {
      eventList = await calendarHelper.getEventsOfDay(calendar.eventList, date);
    }
  } catch (e) {
    logger.error('Calendar failed');
    return res.status(500).send(e.toString());
  }
  try {
    if (user.scheduleEventList.length !== 0) {
      meetingList = await calendarHelper.getEventsOfDay(user.scheduleEventList, date);
    }
  } catch (e) {
    logger.error('Meeting failed');
    return res.status(500).send(e.toString());
  }

  logger.info(`get eventList length of ${eventList.length + meetingList.length}`);
  return res.status(200).json(eventList.concat(meetingList));
};
/**
 * @example GET /user/:userId/suggested-friends
 * @type {Request}
 * @desc get suggested user list for a user. It's based on distance
 * @return {Array} user
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
  const userObjectList = await User.id2ObjectList(user.suggestedFriendList);
  return res.status(200).json(userObjectList);
};
/**
 * @example GET /user/:userId/event/suggested-meeting-users/:startTime/:endTime
 * @description get a list of suggested friends based on time slice given
 * @return {Array} user - top x people suggested
 */
exports.getMeetingSuggestedFriends = async (req, res) => {
  const suggestedBasedOnLocation = await complexLogicFriend.collectNearestFriends(req.params.userId);
  const suggestedBasedOnTime = await complexLogicFriend.collectFreeFriends(req.params.userId,
    req.params.startTime,
    req.params.endTime);

  await console.log(suggestedBasedOnLocation);
  await console.log(suggestedBasedOnTime);
  // const result = arrayUnique(suggestedBasedOnLocation.concat(suggestedBasedOnTime));
  const result = await helper.findCommonElement(suggestedBasedOnLocation, suggestedBasedOnTime);
  const userList = await User.id2ObjectList(result);
  await logger.info(`meeting suggesting ${userList.length} users from ${req.params.startTime} to ${req.params.endTime}`);
  return res.status(200).json(userList);
};
/**
 * @example POST /google-calendar
 * @param {String} credentials
 * save the google calendar of the user.
 */
exports.postGoogleCalendar = async (req, res) => {
  const { email } = req.user;
  const user = await User.findOne({ email }, (err, existingUser) => {
    if (err) { return res.status(500).send(err); }
    if (!existingUser) {
      return res.status(403).send('Account with that email address doesn\'t exist.');
    }
  });
  logger.info(req.body);
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
  const createdCal = await userHelper.addCalToDb(calendar, oauth2Client, user);

  await userHelper.addEventsToDb(calendar, createdCal, oauth2Client, user);

  res.status(200).json(user); // we need to return the list of events...
};
