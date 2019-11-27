const geolib = require('geolib');
const kmeans = require('node-kmeans');
const Event = require('../db/models/event');
const Calendar = require('../db/models/calendar');
const Group = require('../db/models/group');
const helper = require('../controllers/helper');

const logger = helper.getMyLogger('Complex preference');
/**
 * @module preference
 * @description Upon having
 *
 */
const User = require('../db/models/user');

/**
 * @description find the nearest friends in terms of location
 */
exports.collectNearestFriends = async (userId) => {
  // get the friendlist
  try {
    const user = await User.findById(userId, (err, user) => {
      if (err) throw err;
      if (!user) throw Error('No user found');
    });
    const friendList = await User.id2ObjectList(user.friendList, (err, list) => {
      if (err) throw err;
    });
    return friendList.filter((friend) => {
      const distance = geolib.getDistance({
        latitude: user.location.coordinate[1],
        longitude: user.location.coordinate[0]
      }, {
        latitude: friend.location.coordinate[1],
        longitude: friend.location.coordinate[0]
      });
      console.log(distance / 1000.0);
      return user.suggestionRadius > distance / 1000.0;
    }).map((user) => user._id);
  } catch (e) {
    console.log(e.message);
    throw e;
  }
};

/**
 * check if a user's calendar has event collide between the timeslot
 * @param user
 * @param startTime
 * @param endTime
 * @returns {Promise<boolean>}
 */
exports.notCollided = async (user, startTime, endTime) => {
  // get the calendar, then the event list and then compare the
  const freeSlot = await new Event({ startTime, endTime });

  const friendCalendar = await Calendar.findById(user.calendarList[0]);
  const calendarEventList = await Event.id2ObjectList(friendCalendar.eventList);
  const friendMeetingList = await Event.id2ObjectList(user.scheduleEventList);
  const eventObjectList = calendarEventList.concat(friendMeetingList);
  logger.debug(eventObjectList);
  for (let i = 0; i < eventObjectList.length; i++) {
    if (Event.checkEventsCollide(freeSlot, eventObjectList[i])) {
      return false;
    }
  }
  return true;
};

exports.dateValidation = async (startTime, endTime) => {
  if (!(startTime instanceof Date && !isNaN(startTime))) {
    console.log('failed 1');
    return false;
  } if (!(endTime instanceof Date && !isNaN(endTime))) {
    console.log('failed 2');
    return false;
  }
  // check if they are on the same day
  if (startTime.getDay() !== endTime.getDay()) {
    console.log('failed 3', startTime.getDay(), endTime.getDay());
    return false;
  }
  if (startTime.getFullYear() !== endTime.getFullYear()) {
    console.log('failed 4');
    return false;
  }
  if (startTime.getMonth() !== endTime.getMonth()) {
    return false;
  }
  // check if the stattime is before endtime or equal
  if (startTime.getHours() >= endTime.getHours()) {
    console.log('failed 5');
    return false;
  }
  return true;
};

/**
 * @description find the nearest friends in terms of event times.
 */
exports.collectFreeFriends = async (userId, startTime, endTime) => {
  await console.log(userId, startTime.toString(), endTime.toString());
  if (!(this.dateValidation(startTime, endTime))) {
    throw new Error('Input time invalid');
  }
  const user = await User.findById(userId, (err, user) => {
    if (err) throw err;
    if (!user) throw Error('No user found');
  });
  const friendList = await User.id2ObjectList(user.friendList);
  const freeFriendList = [];
  for (let i = 0; i < friendList.length; i++) {
    const result = await this.notCollided(friendList[i], startTime, endTime);
    if (result) {
      await freeFriendList.push(friendList[i]);
    }
  }
  return freeFriendList.map((user) => user._id);
};
