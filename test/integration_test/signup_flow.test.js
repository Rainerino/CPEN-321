const request = require('supertest');
const app = require('../../app');
const dbHandler = require('../unit_test/db_handler');

jest.setTimeout(100000);

describe('Signup Flow', () => {
  // afterAll(async () => {
  //   await dbHandler.clearDatabase();
  //   await dbHandler.closeDatabase();
  // });

  it('Signup', async () => {
    let res;
    const demoUser = {
      email: 'nimanasirisoccerguy@gmail.com',
      password: 'testpass',
      firstName: 'Nima',
      lastName: 'Nasiri'
    };

    res = await post('/user/signup', demoUser)
      .expect('Content-Type', /json/)
      .expect(201);

    const token = res.header.authorization;
    const userId = res.body._id;

    demoEvent = {
      eventName: '8 am meeting',
      eventDescription: 'this is the first test',
      startTime: '2019-11-11T08:00:00.000-08:00',
      endTime: '2019-11-11T09:00:00.000-08:00',
      repeatType: 'WEEKLY',
      eventType: 'MEETING',
      ownerId: userId
    };

    res = await post('/event/create/event', demoEvent)
      .set('Content-Type', 'application/json')
      .set('Authorization', token)
      .expect('Content-Type', /json/)
      .expect(201);
  });

  it('Fails with duplicate users', async () => {
    const demoUser = {
      email: 'nimanasirisoccerguy@gmail.com',
      password: 'testpass',
      firstName: 'Nima',
      lastName: 'Nasiri'
    };

    await post('/user/signup', demoUser)
      .expect(403)
      .expect('Account with that email address already exists.');
  });
});

// a helper function to make a POST request.
function post(url, body) {
  const httpRequest = request(app).post(url);
  httpRequest.send(body);
  httpRequest.set('Accept', 'application/json');
  httpRequest.set('Origin', 'http://localhost:8080');
  return httpRequest;
}

// a helper function to make a PUT request.
function put(url, body) {
  const httpRequest = request(app).put(url);
  httpRequest.send(body);
  httpRequest.set('Accept', 'application/json');
  httpRequest.set('Origin', 'http://localhost:8080');
  return httpRequest;
}
