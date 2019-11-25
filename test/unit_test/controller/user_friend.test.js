const { Response } = require('jest-express/lib/response');
const { Request } = require('jest-express/lib/request');
const mongoose = require('mongoose');
const userController = require('../../../controllers/user/user');
const dbHandler = require('../db_handler');
const User = require('../../../db/models/user');

describe('User friend test', () => {
  let user1Id;
  let user2Id;
  let user3Id;
  let user1;
  let user2;
  let user3;
  let request;
  let response;
  let token;
  /**
   * Connect to a new in-memory database before running any tests.
   */
  beforeAll(async () => {
    await dbHandler.connect();
    await dbHandler.clearDatabase();
    token = 'eGwQImy4o_g:APA91bEZg29nzLVNWHGIy9Uive9po3qa7CBbNZt7D1qg5c8nWTchd-wvkuer5pMQ_'
      + 'sEwjKhmAljFbWmrX3mpKuXLwN1HSjw-snQqmIzemVjRQqaf25j75rA7l5JEDa1kEK8eMXzIpSmZ';
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
    user1Id = await user1._id;
    user2Id = await user2._id;
    user3Id = await user3._id;

    // user1 and user 2 are friend
    
    await User.findByIdAndDelete(user3Id);

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

  test('login: success', async () => {
    await request.setBody({
      email: user1.email,
      password: user1.password
    });
    await userController.postLogin(request, response);

    expect(response.status)
      .toBeCalledWith(200);
    // expect(response.json).toBeCalledWith(JSON.parse(JSON.stringify(user1)));
  });
});
