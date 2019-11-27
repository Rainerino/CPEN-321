const { Response } = require('jest-express/lib/response');
const { Request } = require('jest-express/lib/request');
const mongoose = require('mongoose');
const userController = require('../../../controllers/user/user');
const dbHandler = require('../db_handler');
const User = require('../../../db/models/user');
const Event = require('../../../db/models/event');
const Calendar = require('../../../db/models/calendar');
const preference = require('../../../core/preference');
const suggestion = require('../../../core/suggestion');

describe('User test', () => {
  let user1Id;
  let user2Id;
  let user3Id;
  let user4Id;
  let meeting1Id;
  let meeting2Id;
  let meeting3Id;
  let meeting4Id;
  let meeting5Id;
  let meeting6Id;
  let calendar1Id;
  let calendar2Id;
  let event1Id;
  let event2Id;
  let event3Id;
  let event4Id;
  let meeting1;
  let meeting2;
  let meeting3;
  let meeting4;
  let meeting5;
  let meeting6;
  let calendar1;
  let calendar2;
  let event1;
  let event2;
  let event3;
  let event4;
  let user1;
  let user2;
  let user3;
  let user4;
  let request;
  let response;
  /**
   * Connect to a new in-memory database before running any tests.
   */
  beforeAll(async () => {
    await dbHandler.connect();
    await dbHandler.clearDatabase();
  });
  //
  // /**
  //  * Clear all test data after every test.
  //  */

  beforeEach(async () => {
    user1 = await User.create({
      email: 'albertyanyy@gmail.com',
      password: '123456789',
      firstName: 'Yiyi',
      lastName: 'Yan',
      location: {
        coordinate: [-123.2493002316112, 49.26158905157983],
        city: 'Vancouver',
        country: 'Canada'
      },
      suggestedRadius: 0.5,
      meetingNotification: false,
      calendarList: [],
      groupList: [],
      friendList: [],
      suggestedFriendList: [],
      firebaseRegistrationToken: null
    });

    user2 = await User.create({
      email: 'Yuyi@gmail.com',
      password: '123456789',
      firstName: 'Yuyi',
      lastName: 'Wang',
      location: {
        coordinate: [-123.2493002316112, 49.26158905157983],
        city: 'Vancouver',
        country: 'Canada'
      },
      suggestedRadius: 0.5,
      meetingNotification: false,
      calendarList: [],
      groupList: [],
      friendList: [],
      suggestedFriendList: [],
      firebaseRegistrationToken: null
    });

    user3 = await User.create({
      email: 'nima@gmail.com',
      password: '123456789',
      firstName: 'Nima',
      lastName: 'SorryIforget',
      location: {
        coordinate: [-123.2493002316112, 49.26158905157983],
        city: 'Vancouver',
        country: 'Canada'
      },
      suggestedRadius: 0.5,
      meetingNotification: false,
      calendarList: [],
      groupList: [],
      friendList: [],
      suggestedFriendList: [],
      firebaseRegistrationToken: null
    });

    user4 = await User.create({
      email: 'joe@gmail.com',
      password: '123456789',
      firstName: 'Joe',
      lastName: 'SorryIforget',
      location: {
        coordinate: [-123.2493002316112, 49.26158905157983],
        city: 'Vancouver',
        country: 'Canada'
      },
      suggestedRadius: 0.5,
      meetingNotification: false,
      calendarList: [],
      groupList: [],
      friendList: [],
      suggestedFriendList: [],
      firebaseRegistrationToken: null
    });

    // never
    meeting1 = await Event.create({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'NEVER',
      eventType: 'MEETING',
      ownerId: null,
      userList: [],
      notified: false
    });

    // never
    meeting2 = await Event.create({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'NEVER',
      eventType: 'MEETING',
      ownerId: null,
      userList: [],
      notified: false
    });

    // daily
    meeting3 = await Event.create({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'DAILY',
      eventType: 'MEETING',
      ownerId: null,
      userList: [],
      notified: false
    });

    // weekly
    meeting4 = await Event.create({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'WEEKLY',
      eventType: 'MEETING',
      ownerId: null,
      userList: [],
      notified: false
    });

    // monthly
    meeting5 = await Event.create({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'MONTHLY',
      eventType: 'MEETING',
      ownerId: null,
      userList: [],
      notified: false
    });

    meeting6 = await Event.create({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'MONTHLY',
      eventType: 'MEETING',
      ownerId: null,
      userList: [],
      notified: false
    });

    event1 = await Event.create({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'MONTHLY',
      eventType: 'CALENDAR',
      ownerId: null,
      userList: [],
      notified: false
    });

    event2 = await Event.create({
      eventName: '9 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T09:00:00.000-08:00',
      endTime: '2019-11-11T10:00:00.000-08:00',
      repeatType: 'MONTHLY',
      eventType: 'CALENDAR',
      ownerId: null,
      userList: [],
      notified: false
    });

    event3 = await Event.create({
      eventName: '10 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T10:00:00.000-08:00',
      endTime: '2019-11-11T11:00:00.000-08:00',
      repeatType: 'MONTHLY',
      eventType: 'CALENDAR',
      ownerId: null,
      userList: [],
      notified: false
    });

    event4 = await Event.create({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'MONTHLY',
      eventType: 'CALENDAR',
      ownerId: null,
      userList: [],
      notified: false
    });

    user1Id = await user1._id;
    user2Id = await user2._id;
    user3Id = await user3._id;
    user4Id = await user4._id;

    event1Id = await event1._id;
    event2Id = await event2._id;
    event3Id = await event3._id;
    event4Id = await event4._id;


    calendar1 = await Calendar.create({
      calendarName: 'course schedule 1',
      calendarDescription: 'this is a calendar for yiyi',
      ownerId: null,
      eventList: []
    });

    calendar2 = await Calendar.create({
      calendarName: 'course schedule 1',
      calendarDescription: 'this is a calendar for yiyi',
      ownerId: null,
      eventList: []
    });

    User.addCalendarToUser(user1, calendar1);

    calendar1Id = calendar1._id;
    calendar2Id = calendar2._id;

    meeting1Id = await meeting1._id;
    meeting2Id = await meeting2._id;
    meeting3Id = await meeting3._id;
    meeting4Id = await meeting4._id;
    meeting5Id = await meeting5._id;
    meeting6Id = await meeting6._id;

    await User.addMeetingToUser(user1, meeting1, true);
    await User.addMeetingToUser(user1, meeting2, true);
    await User.addMeetingToUser(user1, meeting3, true);
    await User.addMeetingToUser(user1, meeting4, true);
    await User.addMeetingToUser(user1, meeting5, true);

    await User.addMeetingToUser(user2, meeting1, false);
    await User.addMeetingToUser(user2, meeting2, false);
    await User.addMeetingToUser(user2, meeting3, false);
    await User.addMeetingToUser(user2, meeting4, false);
    await User.addMeetingToUser(user2, meeting5, false);

    await User.findByIdAndDelete(user4Id);
    await Event.findByIdAndDelete(meeting6Id);
    await Event.findByIdAndDelete(event4Id);

    response = new Response();
    request = new Request();
  });

  afterEach(async () => {
    // delete all users
    await User.deleteMany({});
    request.resetMocked();
    response.resetMocked();
  });

  /**
   * Remove and close the db and server.
   */
  afterAll(async () => {
    await dbHandler.clearDatabase();
    await dbHandler.closeDatabase();
  });
  test('preference: event collide', async () => {
    const result = await Event.checkEventsCollide(event1, event1);
    expect(result).toBe(true);
  });
  test('preference: event not collide', async () => {
    const result = await Event.checkEventsCollide(event1, event2);
    expect(result).toBe(false);
  });
  test('preference: event overlap', async () => {
    const result = await Event.checkEventsCollide(event1, event2);
    expect(result).toBe(false);
  });
  test('preference: event not collide', async () => {
    const result = await Event.checkEventsCollide(event1, event2);
    expect(result).toBe(false);
  });
});
