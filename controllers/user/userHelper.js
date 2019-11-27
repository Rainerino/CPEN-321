/**
 * @module helpers/userHelper
 * @desc Contains all routes for user model
 */
const JWT = require('jsonwebtoken');
const { JWT_SECRET } = require('../../config/index');

const ONE_WEEK = 7 * 24 * 60 * 60 * 1000; // One week's time in ms

const Calendar = require('../../db/models/calendar');
const Event = require('../../db/models/event');

/**
 * @description Helper function for postGoogleCalendar.
 * Adds the google events to the database
 * @return void
 */
exports.addEventsToDb = async function(googleCal, calendar, oauth2Client, savedUser) {
    /*
    * This date and time are both ahead by 9 hours, but we only worry
    * about getting the recurring events throughout one week.
    */
    const todays_date = new Date(Date.now());
    const next_week_date = new Date(todays_date.getTime() + ONE_WEEK);

    // Assuming user wants to use their "primary" calendar
    const eventResponse = await googleCal.events.list({
      auth: oauth2Client,
      calendarId: 'primary',
      timeMin: todays_date.toISOString(),
      timeMax: next_week_date.toISOString(),
    });

    const events = eventResponse.data.items;
    events.forEach((event) => {
      if (event.recurrence) {
        const startHour = new Date(event.start.dateTime);
        const endHour = new Date(event.end.dateTime);
        startHour.setMinutes(0);
        endHour.setMinutes(0);

        const newEvent = new Event({
          eventName: event.summary,
          startTime: startHour,
          endTime: endHour,
          repeatType: 'WEEKLY',
          eventType: 'CALENDAR',
          ownerId: savedUser._id
        });
        newEvent.save();
        Calendar.addEventToCalendar(calendar, newEvent);
      }
    });
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