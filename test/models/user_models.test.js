const { expect } = require('chai');
const sinon = require('sinon');
const mongoose = require('mongoose');
require('sinon-mongoose');

const User = require('../../db/models/user');

describe('User Model test 1', () => {
  it('should create a new user', (done) => {
    const userMock = new User({
      email: 'albertyanyy@gmail.com',
      password: '123456789',
      firstName: 'Yiyi',
      lastName: 'Yan',
      location: {
        coordinate: [-123.2386041, 49.27273],
        city: 'Vancouver',
        country: 'Canada'
      },
      suggestedRadius: 20,
      meetingNotification: false,
      calendarList: [],
      groupList: [],
      friendList: [],
      suggestedFriendList: []
    });
    const UserMock = sinon.mock(userMock);
    const user = UserMock.object;
    UserMock
      .expects('save')
      .yields(null);
    user.save((err) => {
      UserMock.verify();
      UserMock.restore();
      expect(err).to.be.null;
      done();
    });
  });

  it('should return error if user is not created', (done) => {
    const UserMock = sinon.mock(new User({
      email: 'test@gmail.com',
      password: 'root'
    }));
    const user = UserMock.object;
    const expectedError = {
      name: 'ValidationError'
    };

    UserMock
      .expects('save')
      .yields(expectedError);

    user.save((err, result) => {
      UserMock.verify();
      UserMock.restore();
      console.log(err);
      expect(err.name).to.equal('ValidationError');
      expect(result).to.be.undefined;
      done();
    });
  });

  it('should not create a user with the unique email', (done) => {
    const UserMock = sinon.mock(new User({
      email: 'test@gmail.com',
      password: 'root'
    }));
    const user = UserMock.object;
    const expectedError = {
      name: 'MongoError',
      code: 11000
    };

    UserMock
      .expects('save')
      .yields(expectedError);

    user.save((err, result) => {
      UserMock.verify();
      UserMock.restore();
      console.log(err);
      expect(err.name).to.equal('MongoError');
      expect(err.code).to.equal(11000);
      expect(result).to.be.undefined;
      done();
    });
  });
  it('should use Validate email', (done) => {
    const UserMock = sinon.mock(new User({
      email: 'albertyanyy@gmail.com',
      password: '123456789',
      firstName: 'Yiyi',
      lastName: 'Yan',
      location: {
        coordinate: [-123.2386041, 49.27273],
        city: 'Vancouver',
        country: 'Canada'
      },
      suggestedRadius: 20,
      meetingNotification: false,
      calendarList: [],
      groupList: [],
      friendList: [],
      suggestedFriendList: []
    }));
    const user = UserMock.object;
    const expectedError = {
      name: 'ValidationError'
    };

    UserMock
      .expects('save')
      .yields(expectedError);

    user.save((err, result) => {
      UserMock.verify();
      UserMock.restore();
      console.log(err);
      expect(err.name).to.equal('ValidationError');
      expect(result).to.be.undefined;
      done();
    });
  });

  it('should find user by email', (done) => {
    const userMock = sinon.mock(User);
    const expectedUser = {
      _id: '5700a128bd97c1341d8fb365',
      email: 'test@gmail.com'
    };

    userMock
      .expects('findOne')
      .withArgs({ email: 'test@gmail.com' })
      .yields(null, expectedUser);

    User.findOne({ email: 'test@gmail.com' }, (err, result) => {
      userMock.verify();
      userMock.restore();
      expect(result.email).to.equal('test@gmail.com');
      done();
    });
  });

  it('should remove user by email', (done) => {
    const userMock = sinon.mock(User);
    const expectedResult = {
      nRemoved: 1
    };

    userMock
      .expects('remove')
      .withArgs({ email: 'test@gmail.com' })
      .yields(null, expectedResult);

    User.remove({ email: 'test@gmail.com' }, (err, result) => {
      userMock.verify();
      userMock.restore();
      expect(err).to.be.null;
      expect(result.nRemoved).to.equal(1);
      done();
    });
  });

  // it('should check password', (done) => {
  //   const UserMock = sinon.mock(new User({
  //     email: 'test@gmail.com',
  //     password: '$2b$10$LhjJj5s1pLY/I4eCRaHaB.Fli8NBT8z1L8YF4/pmVU.5pERg4Z1AC'
  //   }));
  //
  //   const user = UserMock.object;
  //
  //   user.comparePassword('root', (err, isMatched) => {
  //     expect(err).to.equal(undefined);
  //     expect(isMatched).to.equal(true);
  //     done();
  //   });
  // });
  //
  // it('should generate gravatar with email', () => {
  //   const UserMock = sinon.mock(new User({ email: 'test@gmail.com' }));
  //   const user = UserMock.object;
  //   const md5 = '1aedb8d9dc4751e229a335e371db8058';
  //
  //   const gravatar = user.gravatar();
  //   expect(gravatar.includes(md5)).to.equal(true);
  // });
});
