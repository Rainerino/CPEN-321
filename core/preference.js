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
  const friendLocationList = [];
  // eslint-disable-next-line no-restricted-syntax
  for (userId of user.friendList) {
    const coordinate = await User.findById(userId);
    friendLocationList.push(coordinate.location.coordinate);
  }

  const vectors = [];
  for (let i = 0; i < friendLocationList.length; i++) {
    vectors[i] = [friendLocationList[i][0], friendLocationList[i][1]];
  }
  kmeans.clusterize(vectors, { k: 3 }, (err, res) => {
    if (err) console.error(err);
    else console.log('%o', res);
  });
};
/**
 * @description find the nearest friends in terms of event times.
 */
exports.collectFreeFriends = async (userId) => {
  const user = await User.findById(userId);
  const friendLocationList = [];
  // eslint-disable-next-line no-restricted-syntax
  for (userId of user.friendList) {
    const coordinate = await User.findById(userId);
    friendLocationList.push(coordinate.location.coordinate);
  }
};
/**
 * @description find the nearest users in terms of location
 */
