/**
 * @module controller/event
 * @desc Contains all routes for event model
 */
const Event = require('../db/models/event');
const User = require('../db/models/user');
const admin = require('firebase-admin');
/**
 * @example PUT /calendar/:calendarId/event
 * @param {String} eventId - the id of event
 * @type {Request}
 * @desc add events to calendar
 */
/**
 * @example GET /event/:eventId
 * @desc get events objects from the id
 */
exports.getEvent = (req, res) => {
  Event.findById(req.params.eventId, (err, existingEvent) => {
    if (err) { return res.status(400); }
    if (existingEvent) { return res.json(existingEvent); }
    return res.status(404).send("Event with the given event Id doesn't exist.");
  });
};
/**
 * This is a function.
 *
 * @param {string} n - A string param
 * @return {string} A good string
 *
 * @example
 *
 *     foo('hello')
 */
exports.deleteEvent = (req, res) => {
  res.status(501).send('Not implemented');
};

/**
 * @example POST /event/create/event
 * @description create a calendar event. The type field is not set until it's added.
 */
exports.createEvent = (req, res) => {
  const event = new Event({
    eventName: req.body.eventName,
    eventDescription: req.body.eventDescription,
    startTime: req.body.startTime,
    endTime: req.body.endTime,
    repeatType: req.body.repeatType,
    ownerId: req.body.ownerId,
  });
  event.save((err, createdEvent) => {
    if (err) { return res.status(500).send('Save user/grou[ event failed'); }
    res.status(201).json(createdEvent);
  });
};

/**
 * @example POST /event/create/meeting
 * @param ALOT
 * @description create a meeting event
 */
exports.createMeeting = (req, res) => {
  const event = new Event({
    eventName: req.body.eventName,
    eventDescription: req.body.eventDescription,
    startTime: req.body.startTime,
    endTime: req.body.endTime,
    repeatType: req.body.repeatType,
    ownerId: req.body.ownerId,
    eventType: 'MEETING',
    userList: req.body.userList,
    groupList: req.body.groupList,
    notified: false
  });
  console.log(event);
  event.save((err, createdEvent) => {
    if (err) {
      console.log(err);
      return res.status(500).send('Save meeting event failed');
    }
    User.findByIdAndUpdate(req.body.ownerId,
      { $addToSet: { scheduleEventList: createdEvent._id } },
      { new: true },
      (err, updatedUser) => {
        if (err) res.status(400).send('Bad owner Id');
        console.log(updatedUser);
      });
    return res.status(201).json(createdEvent);
  });
};


/**
 * @example POST /event/notify/meeting
 * @param {ObjectId} userId - the user to notify
 * @param {ObjectId} eventId - the event to notify with
 * @type {Request}
 * @desc create a new suggest new friend notification
 */
exports.notifyMeetingUsers = async (req, res) => {
  try {
    const owner = await User.findById(req.body.userId, (err, user) => {
      if (err) return res.status(500).send(err);
      if (!user) return res.status(404).send('No owner found');
    });
    const event = await Event.findById(req.body.eventId, (err, event) => {
      if (err) return res.status(500).send(err);
      if (!event) return res.status(404).send('No event found');
    });
    if (event.userList.length === 0) {
      return res.status(400).send('No users are in the event');
    }
    const userList = await User.userFriendList(event.userList);
    const registrationTokens = await userList
      .filter((user) => {
        if (!user.firebaseRegistrationToken) {
          return false;
        }
        return true;
      }).map((user) => user.firebaseRegistrationToken);
    await console.log(registrationTokens);
    const payload = {
      notification: {
        title: `${await owner.firstName} invite you to join ${await event.eventName}`,
        body: `${await event.eventDescription}`
      },
      data: {
        eventId: `${await event._id}`,
        startTime: `${await event.startTime.toJSON()}`,
        endTime: `${await event.endTime.toJSON()}`
      },
      tokens: await registrationTokens,
    };
    await console.log(payload);
    await admin.messaging().sendMulticast(payload)
      .then((response) => {
        console.log(`${response.successCount} messages were sent successfully`)});
    // set the notified flag to true
    return res.status(200).json(event);
  } catch (e) {
    console.log(e.toString());
    return res.status(500).send(e);
  }

};
/**
 * @example POST /user/:userId/suggested-friends/
 * @param
 * @type {Request}
 * @desc create a new suggest new friend notification
 */
exports.removeUserFromMeeting = (req, res) => {

};
