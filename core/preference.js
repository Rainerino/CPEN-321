const geolib = require('geolib');
const Event = require('../db/models/event');
const Calendar = require('../db/models/calendar');
const mongoose = require('mongoose');
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
  }).map(user => {
    return user._id;
  });
};
/**
 * @description find the nearest friends in terms of event times.
 */
exports.collectFreeFriends = async (userId, startTime, endTime) => {
  const user = await User.findById(userId);
  const friendList = await User.userFriendList(user.friendList);
  // console.log(friendList);
  const freeSlot = await new Event({
    startTime: startTime,
    endTime: endTime
  });
  const list = await friendList.filter( async (friend) => {
    // get the calendar, then the event list and then compare the
    console.log(friend.firstName);
    const friendCalendar = await Calendar.findById(friend.calendarList[0]);
    const eventObjectList = await Calendar.eventList(friendCalendar.eventList);
    const collideEventList = await eventObjectList.filter( (event) => {
      // if collide, add the event to the list.
      return Event.checkEventsCollide(freeSlot, event)
    });
    await console.log(collideEventList.length);

    const result = await (collideEventList.length === 0);
    // await console.log("=========", result)
    return result;
  }).map(user => {
    return user._id;
  });
  return list;
};

/**
 * @description create kmean cluster to speed up the search.
 */
