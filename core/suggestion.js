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
exports.suggestNearbyUser = async (userId) => {
    // get the friendlist
    const main = await User.findById(userId);
    const userList = await User.getUsers();
    return await userList.filter((user) => {
        // we dont want to suggest ourself
        if (user._id.equals(userId)) {
            return false;
        }
        const distance = geolib.getDistance({
            latitude: main.location.coordinate[1],
            longitude: main.location.coordinate[0]
        }, {
            latitude: user.location.coordinate[1],
            longitude: user.location.coordinate[0]
        });
        console.log(distance / 1000.0);
        return main.suggestionRadius > distance / 1000.0;
    }).map((user) => {
        console.log(user.getName());
        return user._id;
    });
};
