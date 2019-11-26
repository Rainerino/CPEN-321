/**
 * @module controller/event-notification
 * @desc Contains all routes for event notification
 */

const admin = require('firebase-admin');
const Event = require('../../db/models/event');
const User = require('../../db/models/user');
const helper = require('../helper');

const logger = helper.getMyLogger('User Controller');
/**
 * @example POST /event/notify/meeting/invite
 * @param {ObjectId} userId - the user to notify
 * @param {ObjectId} eventId - the event to notify with
 * @type {Request}
 * @desc create a new suggest new friend notification
 */
exports.notifyMeetingUsers = async (req, res) => {
  if (!helper.checkNullArgument(2, req.body.userId, req.body.eventId)) {
    return res.status(400).send('Null input');
  }

  let owner;
  let event;
  try {
    // check if the owner exist or not
    owner = await User.findById(req.body.userId).orFail();
    // check if the event exist or not
    event = await Event.findById(req.body.eventId).orFail();
  } catch (e) {
    logger.info(e.toString());
    return res.status(404).send(e);
  }

  try {
    // check if event has user. if not, the event is not set up properly.
    if (event.userList.length === 0) {
      logger.warn('No user in the event');
      return res.status(400).send('No users are in the event');
    }

    // call the firebase notification
    const userList = await User.id2ObjectList(event.userList);
    const registrationTokens = await userList
      .filter((user) => {
        if (!user.firebaseRegistrationToken) {
          return false;
        }
        return true;
      }).map((user) => user.firebaseRegistrationToken);

    await logger.info(registrationTokens);
    const payload = {
      notification: {
        title: `${await owner.firstName} invite you to join ${await event.eventName}`,
        body: `${await event.eventDescription}`
      },
      data: {
        type: 1,
        eventId: `${await event._id}`,
        ownerId: `${await event.ownerId}`,
        startTime: `${await event.startTime.toJSON()}`,
        endTime: `${await event.endTime.toJSON()}`
      },
      tokens: await registrationTokens,
    };
    // ownerId is to get the token to reply
    await logger.info(payload);
    await admin.messaging().sendMulticast(payload)
      .then((response) => {
        console.log(`${response.successCount} messages were sent successfully`);
      });
    // set the notified flag to true
    event = await Event.findByIdAndUpdate({ $set: { notified: true } }).orFail();
    return res.status(200).json(event);
  } catch (e) {
    logger.error(e.toString());
    return res.status(500).send(e);
  }
};

/**
 * @example /event/notify/meeting/accept
 * @param eventId - event that was notifying
 * @param userId - user to notify
 */
exports.notifyAccept = async (req, res) => {
  if (!helper.checkNullArgument(2, req.body.userId, req.body.eventId)) {
    return res.status(400).send('Null input');
  }
  let user;
  let event;
  let owner;

  try {
    user = await User.findById(req.body.userId).orFail();
    event = await Event.findById(req.body.eventId).orFail();
    owner = await User.findById(event.ownerId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }

  // send a notification to the owner
  const registrationTokens = await owner.firebaseRegistrationToken;

  await logger.info(`Owner's token is ${registrationTokens}`);

  const payload = {
    notification: {
      title: `${await user.firstName} accepted your invitation to join ${await event.eventName}`,
      body: `${await event.eventDescription}`
    },
    data: {
      eventId: `${await event._id}`,
      startTime: `${await event.startTime.toJSON()}`,
      endTime: `${await event.endTime.toJSON()}`
    },
  };
  try {
    await logger.info(payload);
    await admin.messaging().sendToDevice(registrationTokens, payload)
      .then((response) => {
        logger.info(`${response.successCount} messages were sent successfully`);
      });
    // set the notified flag to true
    return res.status(200).json(event);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(500).send(e.toString());
  }
};

/**
 * @example /event/notify/meeting/reject
 * @param eventId - event that was notifying
 * @param userId - user to notify
 */
exports.notifyReject = async (req, res) => {
  if (!helper.checkNullArgument(2, req.body.userId, req.body.eventId)) {
    return res.status(400).send('Null input');
  }
  let user;
  let event;
  let owner;

  try {
    user = await User.findById(req.body.userId).orFail();
    event = await Event.findById(req.body.eventId).orFail();
    owner = await User.findById(event.ownerId).orFail();
  } catch (e) {
    logger.warn(e.toString());
    return res.status(404).send(e.toString());
  }

  // send a notification to the owner
  const registrationTokens = await owner.firebaseRegistrationToken;

  await logger.info(`Owner's token is ${registrationTokens}`);

  const payload = {
    notification: {
      title: `${await user.firstName} rejected your invitation to join ${await event.eventName}`,
      body: `${await event.eventDescription}`
    },
    data: {
      eventId: `${await event._id}`,
      startTime: `${await event.startTime.toJSON()}`,
      endTime: `${await event.endTime.toJSON()}`
    },
  };
  try {
    await logger.info(payload);
    await admin.messaging().sendToDevice(registrationTokens, payload)
      .then((response) => {
        logger.info(`${response.successCount} messages were sent successfully`);
      });
    // set the notified flag to true
    return res.status(200).json(event);
  } catch (e) {
    logger.warn(e.toString());
    return res.status(500).send(e.toString());
  }
};
