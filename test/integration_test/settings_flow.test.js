const request = require('supertest');
const app = require('../../app');
const dbHandler = require('../unit_test/db_handler');
const dotenv = require('dotenv');
dotenv.config({ path: '../.env.example' });
var token;

jest.setTimeout(100000);

describe('Settings Flow', () => {
  beforeAll(async () => {
    await dbHandler.connect();
  });

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

    token = res.header.authorization;
  });

  it('Import Calendar', async () => {
    accessToken = {
      access_token: process.env.TEST_GOOGLE_ACCESS_TOKEN
    }
    
    res = await post('/user/google-calendar', accessToken)
    .set('Content-Type', 'application/json')
    .set('Authorization', token)
    .expect('Content-Type', /json/)
    .expect(200);
  });

  /* Need push notification settings */
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
