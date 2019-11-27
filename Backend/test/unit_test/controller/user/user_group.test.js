const { Response } = require('jest-express/lib/response');
const { Request } = require('jest-express/lib/request');
const mongoose = require('mongoose');
const userController = require('../../../../controllers/user/user');
const dbHandler = require('../../db_handler');
const User = require('../../../../db/models/user');
const Group = require('../../../../db/models/group');

describe('User group test', () => {
  let user1Id;
  let user2Id;
  let user3Id;
  let user4Id;
  let group1Id;
  let group2Id;
  let user1;
  let user2;
  let user3;
  let user4;
  let group1;
  let group2;
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
      lastName: 'Iam',
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
    user4Id = await user4._id;

    group1 = await Group.create({
      groupName: 'group A',
      groupDescription: '',
      location: {
        type: 'Point',
        coordinate: [1, 1]
      },
      calendarList: [],
      userList: []
    });
    group2 = await Group.create({
      groupName: 'group B',
      groupDescription: '',
      location: {
        type: 'Point',
        coordinate: [1, 1]
      },
      calendarList: [],
      userList: []
    });

    group1Id = group1._id;
    group2Id = group2._id;
    // user1 and user 2 are friend
    await User.addGroupToUser(user1, group1);
    await User.addGroupToUser(user2, group1);

    await Group.findByIdAndDelete(group2Id);
    await User.findByIdAndDelete(user4Id);
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

  // test('putGroup: success', async () => {
  //   await request.setBody({
  //     userId: user3Id,
  //     groupId: group1
  //   });
  //   await userController.putGroup(request, response);
  //
  //   expect(response.status).toBeCalledWith(200);
  //
  //   const group = await Group.findById(group1Id);
  //   const user = await User.findById(user3Id);
  //   expect(group.userList.length).toEqual(3);
  //   expect(user.groupList.length).toEqual(1);
  // });

  test('putGroup: no user id', async () => {
    await request.setBody({
      groupId: group1
    });
    await userController.putGroup(request, response);

    expect(response.status).toBeCalledWith(400);

    const group = await Group.findById(group1Id);
    const user = await User.findById(user3Id);
    expect(group.userList.length).toEqual(2);
    expect(user.groupList.length).toEqual(0);
  });

  test('putGroup: no group id', async () => {
    await request.setBody({
      userId: user3Id,
    });
    await userController.putGroup(request, response);

    expect(response.status).toBeCalledWith(400);

    const group = await Group.findById(group1Id);
    const user = await User.findById(user3Id);
    expect(group.userList.length).toEqual(2);
    expect(user.groupList.length).toEqual(0);
  });

  test('putGroup: user not found', async () => {
    await request.setBody({
      userId: user4Id,
      groupId: group1
    });
    await userController.putGroup(request, response);

    expect(response.status).toBeCalledWith(404);

    const group = await Group.findById(group1Id);
    const user = await User.findById(user3Id);
    expect(group.userList.length).toEqual(2);
    expect(user.groupList.length).toEqual(0);
  });

  test('putGroup: no group found', async () => {
    await request.setBody({
      userId: user3Id,
      groupId: group2
    });
    await userController.putGroup(request, response);

    expect(response.status).toBeCalledWith(404);

    const group = await Group.findById(group1Id);
    const user = await User.findById(user3Id);
    expect(group.userList.length).toEqual(2);
    expect(user.groupList.length).toEqual(0);
  });
  //
  // test('deleteGroup: success', async () => {
  //   await request.setBody({
  //     userId: user1Id,
  //     groupId: group1
  //   });
  //   await userController.deleteGroup(request, response);
  //
  //   expect(response.status).toBeCalledWith(200);
  //
  //   const group = await Group.findById(group1Id);
  //   const user = await User.findById(user1Id);
  //   expect(group.userList.length).toEqual(1);
  //   expect(user.groupList.length).toEqual(0);
  // });

  test('deleteGroup: no user id', async () => {
    await request.setBody({
      groupId: group1
    });
    await userController.deleteGroup(request, response);

    expect(response.status).toBeCalledWith(400);

    const group = await Group.findById(group1Id);
    const user = await User.findById(user1Id);
    expect(group.userList.length).toEqual(2);
    expect(user.groupList.length).toEqual(1);
  });

  test('deleteGroup: no group id', async () => {
    await request.setBody({
      userId: user1Id,
    });
    await userController.deleteGroup(request, response);

    expect(response.status).toBeCalledWith(400);

    const group = await Group.findById(group1Id);
    const user = await User.findById(user1Id);
    expect(group.userList.length).toEqual(2);
    expect(user.groupList.length).toEqual(1);
  });

  test('deleteGroup: user not found', async () => {
    await request.setBody({
      userId: user4Id,
      groupId: group1
    });
    await userController.deleteGroup(request, response);

    expect(response.status).toBeCalledWith(404);

    const group = await Group.findById(group1Id);
    const user = await User.findById(user1Id);
    expect(group.userList.length).toEqual(2);
    expect(user.groupList.length).toEqual(1);
  });

  test('deleteGroup: group not found', async () => {
    await request.setBody({
      userId: user1Id,
      groupId: group2
    });
    await userController.deleteGroup(request, response);

    expect(response.status).toBeCalledWith(404);

    const group = await Group.findById(group1Id);
    const user = await User.findById(user1Id);
    expect(group.userList.length).toEqual(2);
    expect(user.groupList.length).toEqual(1);
  });
});
