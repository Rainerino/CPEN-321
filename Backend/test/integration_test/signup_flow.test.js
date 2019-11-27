const request = require('supertest');
const app = require('../../app');
const dbHandler = require('../unit_test/db_handler');
const dotenv = require('dotenv');
const initDb= require('../../db/seeders/init_database');
dotenv.config({ path: '../.env.example' });

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

    await post('/user/signup', demoUser)
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
