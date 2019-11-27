const { Response } = require('jest-express/lib/response');
const { Request } = require('jest-express/lib/request');
const mongoose = require('mongoose');
const userController = require('../../../../controllers/user/user');
const dbHandler = require('../../db_handler');
const User = require('../../../../db/models/user');

describe('User test', () => {
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

    expect(response.status).toBeCalledWith(200);
    // expect(response.json).toBeCalledWith(JSON.parse(JSON.stringify(user1)));
  });
  test('login: no email', async () => {
    await request.setBody({
      password: user1.password
    });
    await userController.postLogin(request, response);

    expect(response.status).toBeCalledWith(400);
  });
  test('login: no password', async () => {
    await request.setBody({
      email: user1.email,
    });
    await userController.postLogin(request, response);

    expect(response.status).toBeCalledWith(400);
  });
  test('login: bad email', async () => {
    await request.setBody({
      email: 'maxim',
      password: user1.password
    });

    await userController.postLogin(request, response);

    expect(response.status).toBeCalledWith(404);
  });
  test('login: right email but wrong password', async () => {
    await request.setBody({
      email: user1.email,
      password: 1233
    });
    await userController.postLogin(request, response);

    expect(response.status).toBeCalledWith(403);
  });
  test('login: no user', async () => {
    await request.setBody({
      email: user3.email,
      password: user3.password
    });
    await userController.postLogin(request, response);

    expect(response.status).toBeCalledWith(404);
  });

  test('signup: success', async () => {
    await request.setBody({
      email: user3.email,
      password: user3.password,
      firstName: user3.firstName,
      lastName: user3.lastName
    });

    await userController.postSignup(request, response);
    expect(response.status).toBeCalledWith(201);
    // expect(response.json).toBeCalledWith(JSON.parse(JSON.stringify(user1)));

    const user = await User.findOne({ email: user3.email });
    expect(user.firstName).toEqual(user3.firstName);
  });

  test('signup: bad request: missing fields', async () => {
    await request.setBody({
      password: user3.password,
      firstName: user3.firstName,
      lastName: user3.lastName
    });

    await userController.postSignup(request, response);

    expect(response.status).toBeCalledWith(400);
  });

  test('signup: existed user', async () => {
    await request.setBody({
      email: user1.email,
      password: user1.password,
      firstName: user1.firstName,
      lastName: user1.lastName
    });

    await userController.postSignup(request, response);

    expect(response.status).toBeCalledWith(403);

    const user = await User.findOne({ email: user1.email });
    expect(user.firstName).toEqual(user1.firstName);
  });

  test('signup: no email', async () => {
    await request.setBody({
      password: user3.password,
      firstName: user3.firstName,
      lastName: user3.lastName
    });

    await userController.postSignup(request, response);

    expect(response.status).toBeCalledWith(400);

    const user = await User.findOne({ firstName: user3.firstName });
    expect(user).toEqual(null);
  });

  test('signup: bad email', async () => {
    await request.setBody({
      email: 'd1uej01i2meo12',
      password: user3.password,
      firstName: user3.firstName,
      lastName: user3.lastName
    });

    await userController.postSignup(request, response);

    expect(response.status).toBeCalledWith(400);

    const user = await User.findOne({ firstName: user3.firstName });
    expect(user).toEqual(null);
  });

  test('signup: no password', async () => {
    await request.setBody({
      email: user3.email,
      firstName: user3.firstName,
      lastName: user3.lastName
    });

    await userController.postSignup(request, response);

    expect(response.status).toBeCalledWith(400);

    const user = await User.findOne({ email: user3.email });
    expect(user).toEqual(null);
  });

  test('signup: bad first name', async () => {
    await request.setBody({
      email: user3.email,
      password: user3.password,
      firstName: 1234,
      lastName: user3.lastName
    });

    await userController.postSignup(request, response);

    expect(response.status).toBeCalledWith(400);

    const user = await User.findOne({ email: user3.email });
    expect(user).toEqual(null);
  });

  test('signup: bad last name', async () => {
    await request.setBody({
      email: user3.email,
      password: user3.password,
      firstName: user3.firstName,
      lastName: mongoose.Types.ObjectId()
    });

    await userController.postSignup(request, response);

    expect(response.status).toBeCalledWith(400);

    const user = await User.findOne({ email: user3.email });
    expect(user).toEqual(null);
  });

  test('signup: repeated email', async () => {
    await request.setBody({
      email: user1.email,
      password: user3.password,
      firstName: user3.firstName,
      lastName: user3.lastName
    });

    await userController.postSignup(request, response);

    expect(response.status).toBeCalledWith(403);

    const user = await User.findOne({ firstName: user3.firstName });
    expect(user).toEqual(null);
  });


  test('account: success', async () => {
    await request.setParams({
      userId: user1Id
    });

    await userController.getUser(request, response);

    expect(response.status).toBeCalledWith(200);
  });
  test('account: failed with no userId', async () => {
    await userController.getUser(request, response);

    expect(response.status).toBeCalledWith(400);
  });
  test('account: no user found', async () => {
    await request.setParams({
      userId: user3Id
    });
    await userController.getUser(request, response);

    expect(response.status).toBeCalledWith(404);
  });
  test('notificationToken: success', async () => {
    await request.setBody({
      userId: user1Id,
      token
    });

    await userController.notificationToken(request, response);

    expect(response.status).toBeCalledWith(200);

    const user = await User.findById(user1Id);
    expect(user.firebaseRegistrationToken).toEqual(token);
    // expect(response.json).toBeCalledWith(JSON.parse(JSON.stringify(user1)));
  });
  test('notificationToken: missing user id', async () => {
    await request.setBody({
      token
    });

    await userController.notificationToken(request, response);

    expect(response.status).toBeCalledWith(404);

    const user = await User.findById(user1Id);
    expect(user.firebaseRegistrationToken).toEqual(null);
  });

  test('notificationToken: missing token', async () => {
    await request.setBody({
      userId: user1Id,
    });

    await userController.notificationToken(request, response);

    expect(response.status).toBeCalledWith(400);

    const user = await User.findById(user1Id);
    expect(user.firebaseRegistrationToken).toEqual(null);
  });

  test('notificationToken: user doesn\'t exist', async () => {
    await request.setBody({
      userId: user3Id,
      token
    });

    await userController.notificationToken(request, response);

    expect(response.status).toBeCalledWith(404);
  });

  test('getAllUser: success', async () => {
    await userController.getAllUser(request, response);

    expect(response.status).toBeCalledWith(200);
  });

  test('location: success', async () => {
    const longitude = 123;
    const latitude = 49;
    request.setBody({
      userId: user1Id,
      longitude,
      latitude
    });

    await userController.putLocation(request, response);

    expect(response.status).toBeCalledWith(200);
    const user = await User.findById(user1Id);
    expect(user.location.coordinate[0]).toEqual(longitude);
    expect(user.location.coordinate[1]).toEqual(latitude);
  });

  test('location: no userid', async () => {
    const longitude = 123;
    const latitude = 49;
    request.setBody({
      longitude,
      latitude
    });

    await userController.putLocation(request, response);

    expect(response.status).toBeCalledWith(400);
    const user = await User.findById(user1Id);
    expect(user.location.coordinate[0]).toEqual(user1.location.coordinate[0]);
    expect(user.location.coordinate[1]).toEqual(user1.location.coordinate[1]);
  });

  test('location: no longitude', async () => {
    const longitude = 123;
    const latitude = 49;
    request.setBody({
      userId: user1Id,
      latitude
    });

    await userController.putLocation(request, response);

    expect(response.status).toBeCalledWith(400);
    const user = await User.findById(user1Id);
    expect(user.location.coordinate[0]).toEqual(user1.location.coordinate[0]);
    expect(user.location.coordinate[1]).toEqual(user1.location.coordinate[1]);
  });

  test('location: no latitude', async () => {
    const longitude = 123;
    const latitude = 49;
    request.setBody({
      userId: user1Id,
      longitude,
    });

    await userController.putLocation(request, response);

    expect(response.status).toBeCalledWith(400);
    const user = await User.findById(user1Id);
    expect(user.location.coordinate[0]).toEqual(user1.location.coordinate[0]);
    expect(user.location.coordinate[1]).toEqual(user1.location.coordinate[1]);
  });

  test('location: no user found', async () => {
    const longitude = 123;
    const latitude = 49;
    request.setBody({
      userId: user3Id,
      longitude,
      latitude
    });

    await userController.putLocation(request, response);

    expect(response.status).toBeCalledWith(404);
  });

  test('location: bad longitude', async () => {
    const longitude = 190;
    const latitude = 49;
    request.setBody({
      userId: user1Id,
      longitude,
      latitude
    });

    await userController.putLocation(request, response);

    expect(response.status).toBeCalledWith(400);
    const user = await User.findById(user1Id);
    expect(user.location.coordinate[0]).toEqual(user1.location.coordinate[0]);
    expect(user.location.coordinate[1]).toEqual(user1.location.coordinate[1]);
  });

  test('location: bad latitude', async () => {
    const longitude = 123;
    const latitude = -100;
    request.setBody({
      userId: user1Id,
      longitude,
      latitude
    });

    await userController.putLocation(request, response);

    expect(response.status).toBeCalledWith(400);
    const user = await User.findById(user1Id);
    expect(user.location.coordinate[0]).toEqual(user1.location.coordinate[0]);
    expect(user.location.coordinate[1]).toEqual(user1.location.coordinate[1]);
  });
});
