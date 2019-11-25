const request = require('supertest');
const app = require('../../app');

jest.setTimeout(100000);

describe('Signup Flow', () => {
  it('Signup', async () => {
    const demoUser = {
      email: 'nimanasirisoccerguy@gmail.com',
      password: 'testpass',
      firstName: 'Nima',
      lastName: 'Nasiri'
    };

    const res = await post('/user/signup', demoUser)
      .expect('Content-Type', /json/)
      .expect(201);

    expect(res.body.email).toBe(demoUser.email);
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

  it('Create Calendar', async () => {
    const demoCal = {
      calendarName: 'Nima Calendar',
      calendarDescription: 'School'
    };

    const res = await post('/calendar/create', demoCal)
      .expect('Content-Type', /json/)
      .expect(201);

    const demoEvent = {
      calendarId: res.body._id,
      eventId: '5dd38bd90ea1ba24d0e5e650'
    };

    await put('/calendar/event/add-events', demoEvent)
      .expect('Content-Type', /json/)
      .expect(200);
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
