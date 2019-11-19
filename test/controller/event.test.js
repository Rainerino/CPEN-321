const mongoose = require('mongoose');
const userController = require('../../controllers/user');
const dbHandler = require('../db_handler');
const { mockRequest, mockResponse } = require('mock-req-res')
const User = require('../../db/models/user');
const Event = require('../../db/models/event');
const Group = require('../../db/models/group');
const Calendar = require('../../db/models/calendar');

/**
 * Connect to a new in-memory database before running any tests.
 */
beforeAll(async () => {
  await dbHandler.connect();
});
//
// /**
//  * Clear all test data after every test.
//  */
// afterEach(async () => await dbHandler.clearDatabase());

/**
 * Remove and close the db and server.
 */
afterAll(async () => await dbHandler.closeDatabase());

describe('Sample Test', () => {
  it('should test that true === true', () => {
    expect(true).toBe(true);
  })
})

describe('user account test', () => {
  it('should return user', async () => {
    const myEmail = 'albertyanyy@gmail.com';
    const myPassword = '123456789';
    const yiyi_user = await User.findOne({ email: myEmail}).lean().exec((err, user) => {
      return JSON.stringify(user);
    });
    const req = mockRequest({body: myEmail, myPassword});
    const res = mockResponse();
    await userController.postSignup(req, res);
    await expect(res.data).toBe(yiyi_user);
  })
})
