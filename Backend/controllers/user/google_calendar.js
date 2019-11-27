/**
 * @module helpers/userHelper
 * @desc Contains all routes for user model
 */
const JWT = require('jsonwebtoken');
const { JWT_SECRET } = require('../../config/index');

const ONE_YEAR = 365 * 24 * 60 * 60 * 1000; // One week's time in ms

const Calendar = require('../../db/models/calendar');
const Event = require('../../db/models/event');
const helper = require('../helper');

const logger = helper.getMyLogger('Google Calendar');
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
  const todaysDate = new Date(Date.now());
  const nextWeekDate = new Date(todaysDate.getTime() + ONE_YEAR);

  logger.debug(`All google cal events: ${googleCal.events.toString()}`);
  // Assuming user wants to use their "primary" calendar
  const eventResponse = await googleCal.events.list({
    auth: oauth2Client,
    calendarId: 'primary',
    timeMin: todaysDate.toISOString(),
    timeMax: nextWeekDate.toISOString(),
  });

  const events = eventResponse.data.items;

  logger.debug(events);

  const eventList = await Promise.all(events.map(async (event) => {
    const startHour = new Date(event.start.dateTime);
    const endHour = new Date(event.end.dateTime);
    startHour.setMinutes(0);
    endHour.setMinutes(0);
    let repeatType = 'NEVER';

    if (event.recurrence) {
      const recurrence = event.recurrence.toString();
      const weekly = recurrence.search('WEEKLY');
      const monthly = recurrence.search('MONTHLY');
      const daily = recurrence.search('DAILY');
      if (weekly > 0) {
        repeatType = 'WEEKLY';
      } else if (monthly > 0) {
        repeatType = 'MONTHLY';
      } else if (daily > 0) {
        repeatType = 'DAILY';
      }
    }
    const newEvent = await Event.create({
      eventName: event.summary,
      eventDescription: event.description,
      startTime: startHour,
      endTime: endHour,
      repeatType,
      eventType: 'CALENDAR',
      ownerId: savedUser._id
    });
    await Calendar.addEventToCalendar(calendar, newEvent);
    logger.info(`Google Calendar added ${newEvent.eventName} into ${savedUser.firstName}'s calendar`);
    return newEvent;
  }));
  logger.debug(eventList);
  return eventList;
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
