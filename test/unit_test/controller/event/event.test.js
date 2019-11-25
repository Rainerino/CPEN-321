const { Response } = require('jest-express/lib/response');
const { Request } = require('jest-express/lib/request');
const mongoose = require('mongoose');
const eventController = require('../../../../controllers/event/event');
const dbHandler = require('../../db_handler');
const User = require('../../../../db/models/user');
const Event = require('../../../../db/models/event');

describe('Event test', () => {
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
  let meeting1;
  let meeting2;
  let meeting3;
  let meeting4;
  let meeting5;
  let meeting6;
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

    meeting1 = await Event.create({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: null,
      eventType: 'MEETING',
      ownerId: null,
      userList: [],
      notified: false
    });

    user1Id = await user1._id;
    user2Id = await user2._id;
    user3Id = await user3._id;
    user4Id = await user4._id;

    meeting1Id = await meeting1._id;

    await User.findByIdAndDelete(user4Id);

    response = new Response();
    request = new Request();
  });

  afterEach(async () => {
    // delete all users
    await User.deleteMany({});
    await Event.deleteMany({});
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

  test('createMeeting: success with no user', async () => {
    await request.setBody({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'MONTHLY',
      eventType: 'MEETING',
      ownerId: user1Id,
      userList: [],
      notified: false
    });
    await eventController.createMeeting(request, response);

    expect(response.status).toBeCalledWith(200);

    const user = await User.findById(user1Id);
    const event = await Event.findById(user.scheduleEventList[0]);

    expect(user.scheduleEventList.length).toEqual(1);

    expect(event.userList.length).toEqual(0);

    expect(event.ownerId).toEqual(user1Id);

    expect(user.scheduleEventList).toContainEqual(event._id);
  });

  test('createMeeting: success with some user', async () => {
    await request.setBody({
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'MONTHLY',
      eventType: 'MEETING',
      ownerId: user1Id,
      userList: [
        user2Id,
        user3Id
      ],
      notified: false
    });
    await eventController.createMeeting(request, response);

    expect(response.status).toBeCalledWith(200);

    const tempUser1 = await User.findById(user1Id);
    const tempUser2 = await User.findById(user2Id);
    const tempUser3 = await User.findById(user3Id);

    const event = await Event.findById(tempUser1.scheduleEventList[0]);

    // check if eveyrone are on the event list.
    expect(tempUser1.scheduleEventList.length).toEqual(1);
    expect(tempUser2.scheduleEventList.length).toEqual(1);
    expect(tempUser3.scheduleEventList.length).toEqual(1);

    expect(event.userList.length).toEqual(2);

    expect(event.ownerId).toEqual(tempUser1._id);

    expect(tempUser1.scheduleEventList).toContainEqual(event._id);
    expect(tempUser2.scheduleEventList).toContainEqual(event._id);
    expect(tempUser3.scheduleEventList).toContainEqual(event._id);
  });

  test('deleteEvent: success', async () => {
    await User.addMeetingToUser(user1, meeting1, true);
    await User.addMeetingToUser(user2, meeting1, false);
  });
});
