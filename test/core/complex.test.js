const dbHandler = require('../db_handler');
const User = require('../../db/models/user');
const Event = require('../../db/models/event');
const Group = require('../../db/models/group');
const Calendar = require('../../db/models/calendar');
const complex = require('../../core/preference');
const mongoose = require('mongoose');
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

describe('complex logic test location', async () => {
  it('default radius 0.5km', async () => {
    const yiyi_user = await User.findOne({ email: 'albertyanyy@gmail.com' });
    const result = await complex.collectNearestFriends(yiyi_user._id);
    expect(result.length).toBe(3);
  });
});

describe('complex logic test location', () => {
  it('using 0.15 km', async () => {
    const yiyi_user = await User.findOneAndUpdate({ email: 'albertyanyy@gmail.com' },
      { $set: { suggestionRadius: 0.1} },
      {useFindAndModify: false, new: true });
    console.log(yiyi_user);
    const result = await complex.collectNearestFriends(yiyi_user._id);
    expect(result.length).toBe(0);
    const resultUser = await User.findById(result);
  });
});

describe('complex logic test location', () => {
  it('using 1 km', async () => {
    const yiyi_user = await User.findOneAndUpdate({ email: 'albertyanyy@gmail.com' },
      { $set: { suggestionRadius: 1} },
      {useFindAndModify: false, new: true });
    console.log(yiyi_user);
    const result = await complex.collectNearestFriends(yiyi_user._id);
    expect(result.length).toBe(4);
    const resultUser = await User.findById(result);
  });
});

describe('complex logic test location', () => {
  it('no user found with bad objectId', () => {
    complex.collectNearestFriends(mongoose.Types.ObjectId()).then(() => null,
      (error) => {expect(error.message).toBe('No user found');});
  });
});

describe('complex logic test location', () => {
  it('no user found with bad input', () => {
    complex.collectNearestFriends(12345).then(() => null, (error) => {
      expect(error.name).toBe('CastError');
    });
  });
});

describe('complex logic test location', () => {
  it('bad user with no friendlist', () => {
    User.findOne({ email: 'user8@gmail.com' }, (err, user) => {
      console.log(user);
      complex.collectNearestFriends(user._id).then(() => null, (error) => {
        expect(error.message).toBe('Cannot read property \'friendList\' of null');
      });
    });
  });
});

describe('complex logic test time', () => {
  it('default time slot 7-8', () => {
    User.findOne({ email: 'albertyanyy@gmail.com' }, (err, user) => {
      complex.collectFreeFriends(
        user._id,
        Date('2019-11-11T07:00:00.000+00:00'),
        Date('2019-11-11T08:00:00.000+00:00')).then((result) => {
        expect(result.length).toBe(2);
      });
    });
  });
});

describe('complex logic test time', () => {
  it('default timeslot 11-12', () => {
    User.findOne({ email: 'albertyanyy@gmail.com' }).then((user) => {
      complex.collectFreeFriends(
        user._id,
        Date('2019-11-11T23:00:00.000+00:00'),
        Date('2019-11-12T00:00:00.000+00:00')).then((result) => {
        expect(result.length).toBe(1);
      });
    });
  });
});

describe('complex logic test time', () => {
  it('error no user', () => {
    User.findOne({ email: '1234' }).then((user) => {
      complex.collectFreeFriends(
        user._id,
        Date('2019-11-11T23:00:00.000+00:00'),
        Date('2019-11-12T00:00:00.000+00:00')).then(() => null, error => {
          expect(error.message).toBe('No user found');
      });
    });
  });
});

describe('complex logic test time', () => {
  it('bad time slot: no on the same day', () => {
    User.findOne({ email: 'albertyanyy@gmail.com' }).then((user) => {
      complex.collectFreeFriends(
        user._id,
        Date('2019-11-11T23:00:00.000+00:00'),
        Date('2019-11-12T00:00:00.000+00:00')).then(() => null, (error) => {
        expect(error.message).toBe('Input time invalid');
      });
    });
  });
});

describe('complex logic test time', () => {
  it('bad time slot: start time after end time', () => {
    User.findOne({ email: 'albertyanyy@gmail.com' }).then((user) => {
      complex.collectFreeFriends(
        user._id,
        Date('2019-11-11T09:00:00.000+00:00'),
        Date('2019-11-11T08:00:00.000+00:00')).then(() => null, (error) => {
        expect(error.message).toBe('Input time invalid');
      });
    });
  });
});

describe('complex logic test time', () => {
  it('bad time slot: false date type', () => {
    User.findOne({ email: 'albertyanyy@gmail.com' }).then((user) => {
      complex.collectFreeFriends(
        user._id,
        123,
        345).then(() => null, (error) => {
        expect(error.message).toBe('Input time invalid');
      });
    });
  });
});

