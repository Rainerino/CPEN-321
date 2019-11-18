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
  const user = await User.findById(userId);
  const friendList = await User.userFriendList(user.friendList);
  // first filter out all firends that are not within the radius.
  // const vectors = [];
  // await friendList.map((user) => {
  //   vectors.push([user.location.coordinate[0], user.location.coordinate[1]]);
  // });
  // console.log(vectors);
  // await kmeans.clusterize(vectors, { k: 1 }, (err, res) => {
  //   if (err) console.error(err);
  //   // else console.log('%o', res);
  // });
  return await friendList.filter((friend) => {
    const distance = geolib.getDistance({
      latitude: user.location.coordinate[1],
      longitude: user.location.coordinate[0]
    }, {
      latitude: friend.location.coordinate[1],
      longitude: friend.location.coordinate[0]
    });
    // console.log(distance / 1000.0);
    return user.suggestionRadius > distance / 1000.0;
  }).map((user) => user._id);
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

/**
 * @description find the nearest friends in terms of event times.
 */
exports.collectFreeFriends = async (userId, startTime, endTime) => {
  const user = await User.findById(userId);
  const friendList = await User.userFriendList(user.friendList);
  const freeFriendList = [];
  for (let i = 0; i < friendList.length; i++) {
    const result = await notCollided(friendList[i], startTime, endTime);
    if (result) {
      await freeFriendList.push(friendList[i]);
    }
  }
  return freeFriendList.map((user) => user._id);
};
