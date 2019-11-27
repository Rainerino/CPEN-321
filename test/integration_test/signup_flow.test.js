const request = require('supertest');
const app = require('../../app');
const dbHandler = require('../unit_test/db_handler');
const dotenv = require('dotenv');
const initDb= require('../../db/seeders/init_database');
dotenv.config({ path: '../.env.example' });

jest.setTimeout(100000);

describe('Signup Flow', () => {

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

    await post('/user/signup', demoUser)
      .expect('Content-Type', /json/)
      .expect(201);
  });
});

// a helper function to make a POST request.
function post(url, body) {
  const httpRequest = request(app).post(url);
  httpRequest.send(body);
  httpRequest.set('Accept', 'application/json');
  httpRequest.set('Origin', 'http://localhost:8080');
  return httpRequest;
};
