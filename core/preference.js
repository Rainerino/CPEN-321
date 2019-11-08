const geolib = require('geolib');
const Event = require('../db/models/event');
const Calendar = require('../db/models/calendar');

/**
 * @module preference
 * @description Upon having
 *
 */
// const tf = require('@tensorflow/tfjs');
const kmeans = require('node-kmeans');
const User = require('../db/models/user');

/**
 * @description find the nearest friends in terms of location
 */
exports.collectNearestFriends = async (userId, meetingLocation) => {
  // get the friendlist
  const user = await User.findById(userId);
  const friendList = await User.userFriendList(user.friendList);
  // first filter out all firends that are not within the radius.
  const filteredFriendList = await friendList.filter((friend) => {
    const distance = geolib.getDistance({
      latitude: user.location.coordinate[1],
      longitude: user.location.coordinate[0]
    },{
      latitude: friend.location.coordinate[1],
      longitude: friend.location.coordinate[0]
    });
    console.log(distance);
    return user.suggestionRadius > distance;
  });
  const vectors = [];
  await friendList.map((user) => {
    vectors.push([user.location.coordinate[0], user.location.coordinate[1]]);
  });
  // console.log(vectors);
  // await kmeans.clusterize(vectors, { k: 1 }, (err, res) => {
  //   if (err) console.error(err);
  //   // else console.log('%o', res);
  // });
  return friendList;
};
/**
 * @description find the nearest friends in terms of event times.
 */
exports.collectFreeFriends = async (userId, startTime, endTime) => {
  const user = await User.findById(userId);
  const friendList = await User.userFriendList(user.friendList);
  const freeSlot = new Event({
    startTime,
    endTime
  });

  await console.log(friendList);
  const calendarCollideList = await friendList.filter(async (friend) => {
    // get the calendar, then the event list and then compare the
    const friendEventList = await Calendar.findById(friend.calendarId);
    const eventObjectList = await Calendar.eventList(friendEventList);
    const collideEventList = eventObjectList.filter((event) => {
      return Event.checkEventsCollide(freeSlot, event);
    });
    return collideEventList.length === 0;
  });
  return calendarCollideList;
};

/**
 * @description create kmean cluster to speed up the search.
 */
