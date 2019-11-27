/**
 * @module helpers/userHelper
 * @desc Contains all routes for user model
 */
const { google } = require('googleapis');
const googleAuth = require('google-auth-library');
const JWT = require('jsonwebtoken');
const { JWT_SECRET, oauth } = require('../../config/index');

const ONE_WEEK = 7 * 24 * 60 * 60 * 1000; // One week's time in ms

const User = require('../../db/models/user');
const Calendar = require('../../db/models/calendar');
const Event = require('../../db/models/event');

/**
 * @description Helper function for postGoogleCalendar.
 * Adds the given google calendar to the database
 * @return {Calendar} Corresponds to the calendar in the db
 */
exports.addCalToDb = async function (calendar, oauth2Client, savedUser) {
  const googleCalendar = await calendar.calendarList.list({
    auth: oauth2Client,
    calendarId: 'primary'
  });

  const newCal = new Calendar({
    calendarName: googleCalendar.data.items[0].id,
    calendarDescription: googleCalendar.data.items[0].description,
    ownerId: savedUser._id
  });
  savedCal = await newCal.save();
  await savedUser.update({ $push: { calendarList: savedCal._id } });
  return savedCal;
};

/**
 * @description Helper function for postGoogleCalendar.
 * Adds the google events to the database
 * @return void
 */
exports.addEventsToDb = async function (googleCal, calendar, oauth2Client, savedUser) {
  /*
    * This date and time are both ahead by 9 hours, but we only worry
    * about getting the recurring events throughout one week.
    */
  const todayDate = new Date(Date.now());
  const nextWeekDate = new Date(todayDate.getTime() + ONE_WEEK);

  // Assuming user wants to use their "primary" calendar
  const eventResponse = await googleCal.events.list({
    auth: oauth2Client,
    calendarId: 'primary',
    timeMin: todayDate.toISOString(),
    timeMax: nextWeekDate.toISOString(),
  });

  const events = eventResponse.data.items;
  events.forEach((event) => {
    if (event.recurrence) {
      const newEvent = new Event({
        eventName: event.summary,
        startTime: event.start.dateTime,
        endTime: event.end.dateTime,
        repeatType: 'WEEKLY',
        eventType: 'CALENDAR',
        ownerId: savedUser._id,
        notified: false
      });
      newEvent.save();
      Calendar.addEventToCalendar(calendar, newEvent);
    }
  });
};

/**
 * @description Helper function for postGoogleCalendar.
 * Adds the google user to the database
 * @return {User} Corresponds to the user in the db
 */
exports.addNewUser = async function (newUser) {
  const user = new User({
    firstName: newUser.body.firstName,
    lastName: newUser.body.lastName,
    email: newUser.body.email,
    password: 'google',
    groupList: [],
    friendList: [],
    suggestedFriendList: [],
    calendarList: []
  });

  // validation needed
  savedUser = await user.save();
  return savedUser;
};

/**
 * @description Signs a JWT corresponding to the user
 * @return {JWT_Token}
 */
exports.signToken = (user) => JWT.sign({
  iss: 'Nimanasiribrah',
  sub: user.id,
  iat: new Date().getTime(), // current time
  exp: new Date().setDate(new Date().getDate() + 365), // current time + 365 days
}, JWT_SECRET);
