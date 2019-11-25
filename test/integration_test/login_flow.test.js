const request = require('supertest');
const app = require('../../app');

jest.setTimeout(100000);

describe('Login Flow', () => {
  it('Login', async () => {
    const demoUser = {
      email: 'albertyanyy@gmail.com',
      password: '123456789'
    };

    await post('/user/login', demoUser)
      .expect(200);
  });

  it('View Calendar', async () => {
    const demoCal = {
      calendarId: '5dd38bd90ea1ba24d0e5e647'
    };

    await get('/calendar/5dd38bd90ea1ba24d0e5e647', demoCal)
      .expect(200);
  });

  /* Deleting events not implemented yet */
  it('Edit Calendar (delete an event)', async () => {
    const demoUser = {
      userId: '5dd38bd80ea1ba24d0e5e63a'
    };

    await del('/calendar/5dd38bd90ea1ba24d0e5e647/event/delete/5dd38bd90ea1ba24d0e5e657', demoUser)
      .expect(501);
  });


  it('View Friends List', async () => {
    const demoUser = {};

    await get('/user/5dd38bd80ea1ba24d0e5e63a/friendlist', demoUser)
      .expect(200);
  });

  /* Add new friend */
  it('Edit Friends List', async () => {
    const demoCal = {
      userId: '5dd38bd80ea1ba24d0e5e63c'
    };

    await put('/user/5dd38bd80ea1ba24d0e5e63b/friendlist', demoCal)
      .expect(201);
  });


  it('Get suggested friends', async () => {
    const demoUser = {
      userId: '5dd38bd80ea1ba24d0e5e63a'
    };

    await get('/user/5dd38bd80ea1ba24d0e5e63a/suggested-friends', demoUser)
      .expect(200);
  });

  /* Chatroom, get group calendar */
  it('Get group calendar', async () => {
    const demoUser = {};

    await get('/group/5dd38bd90ea1ba24d0e5e64f', demoUser)
      .expect(200);
  });

  /* Need chat room */

  /* Need location sharing */
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

// a helper function to make a GET request.
function get(url, body) {
  const httpRequest = request(app).get(url);
  httpRequest.send(body);
  httpRequest.set('Accept', 'application/json');
  httpRequest.set('Origin', 'http://localhost:8080');
  return httpRequest;
}

// a helper function to make a DELETE request.
function del(url, body) {
  const httpRequest = request(app).delete(url);
  httpRequest.send(body);
  httpRequest.set('Accept', 'application/json');
  httpRequest.set('Origin', 'http://localhost:8080');
  return httpRequest;
}
