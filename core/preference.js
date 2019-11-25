const geolib = require('geolib');
const kmeans = require('node-kmeans');
const Event = require('../db/models/event');
const Calendar = require('../db/models/calendar');
const Group = require('../db/models/group');
/**
 * @module preference
 * @description Upon having
 *
 */
// const tf = require('@tensorflow/tfjs');
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

async function notCollided(friend, startTime, endTime) {
  // get the calendar, then the event list and then compare the
  const freeSlot = await new Event({
    startTime,
    endTime
  });
  const friendCalendar = await Calendar.findById(friend.calendarList[0]);
  const eventObjectList = await Calendar.eventList(friendCalendar.eventList);
  for (let i = 0; i < eventObjectList.length; i++) {
    if (await Event.checkEventsCollide(freeSlot, eventObjectList[i])) {
      return false;
    }
  }
  return true;
}

function dateValidation(startTime, endTime) {
  if (!(startTime instanceof Date && !isNaN(startTime))) {
    console.log('failed 1');
    return false;
  }
  if (!(endTime instanceof Date && !isNaN(endTime))) {
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
}

/**
 * @description find the nearest friends in terms of event times.
 */
exports.collectFreeFriends = async (userId, startTime, endTime) => {
  try {
    await console.log(userId, startTime.toString(), endTime.toString());
    if (!(dateValidation(startTime, endTime))) {
      throw new Error('Input time invalid');
    }
    const user = await User.findById(userId, (err, user) => {
      if (err) throw err;
      if (!user) throw Error('No user found');
    });
    const friendList = await User.id2ObjectList(user.friendList);
    const freeFriendList = [];
    for (let i = 0; i < friendList.length; i++) {
      const result = await notCollided(friendList[i], startTime, endTime);
      if (result) {
        await freeFriendList.push(friendList[i]);
      }
    }
    return freeFriendList.map((user) => user._id);
  } catch (e) {
    console.log(e.message);
    throw e;
  }
};
