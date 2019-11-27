const request = require('supertest');
const app = require('../../app');

let token;

jest.setTimeout(100000);

describe('Login Flow', () => {
  it('Login', async () => {
    const demoUser = {
      email: 'albertyanyy@gmail.com',
      password: '123456789'
    };

    const res = await post('/user/login', demoUser)
      .expect(200);


    token = res.header.authorization;
  });

  it('View Calendar', async () => {
    const demoCal = {
      calendarId: '5dde581a89d07c45a81ddfc0'
    };

    await get('/calendar/5dde581a89d07c45a81ddfc0', demoCal)
      .set('Content-Type', 'application/json')
      .set('Authorization', token)
      .expect('Content-Type', /json/)
      .expect(200);
  });

  it('View Friends List', async () => {
    const demoUser = {};

    await get('/user/5dde581989d07c45a81ddfb3/friendlist', demoUser)
      .set('Content-Type', 'application/json')
      .set('Authorization', token)
      .expect('Content-Type', /json/)
      .expect(200);
  });

  /* Add new friend */
  it('Edit Friends List (Add)', async () => {
    const demoCal = {
      userId: '5dde581989d07c45a81ddfb3',
      friendId: '5dde581989d07c45a81ddfb4'
    };

    await put('/user/add/friend', demoCal)
      .set('Content-Type', 'application/json')
      .set('Authorization', token)
      .expect('Content-Type', /json/)
      .expect(200);
  });

  /* Delete new friend */
  it('Edit Friends List (Remove)', async () => {
    const demoCal = {
      userId: '5dde581989d07c45a81ddfb3',
      friendId: '5dde581989d07c45a81ddfb4'
    };

    await del('/user/delete/friend', demoCal)
      .set('Content-Type', 'application/json')
      .set('Authorization', token)
      .expect('Content-Type', /json/)
      .expect(200);
  });


  it('Get suggested friends', async () => {
    const demoUser = {
      userId: '5dde581989d07c45a81ddfb3'
    };

    await get('/user/5dde581989d07c45a81ddfb3/suggested-friends', demoUser)
      .set('Content-Type', 'application/json')
      .set('Authorization', token)
      .expect('Content-Type', /json/)
      .expect(200);
  });

  /* Chatroom, get group calendar */
  it('Get group calendar', async () => {
    const demoUser = {};

    await get('/group/5dde581a89d07c45a81ddfc7', demoUser)
      .set('Content-Type', 'application/json')
      .set('Authorization', token)
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
