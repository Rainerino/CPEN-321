const request =  require('supertest');
const app = require('../app');

jest.setTimeout(100000);

describe('Settings Flow', () => { 
    
  it('Login', async () => {
    const demoUser = {
      email: 'albertyanyy@gmail.com',
      password: '123456789'
    };

    await post(`/user/login`, demoUser)
    .expect(200);
  }); 

  it('View Friends List', async () => {
    const demoUser = {}; 

    await get(`/user/5dd38bd80ea1ba24d0e5e63a/friendlist`, demoUser)
    .expect(200);
  }); 

  /* Add new friend */
  it('Edit Friends List', async () => {
    const demoCal = {
        userId: '5dd38bd80ea1ba24d0e5e63c'
    }; 

    await put(`/user/5dd38bd80ea1ba24d0e5e63b/friendlist`, demoCal)
    .expect(201);
  }); 

  /* Need push notification settings */

});

  // a helper function to make a POST request.
function post(url, body){
  const httpRequest = request(app).post(url);
  httpRequest.send(body);
  httpRequest.set('Accept', 'application/json');
  httpRequest.set('Origin', 'http://localhost:8080');
  return httpRequest;
}

// a helper function to make a PUT request.
function put(url, body){
  const httpRequest = request(app).put(url);
  httpRequest.send(body);
  httpRequest.set('Accept', 'application/json');
  httpRequest.set('Origin', 'http://localhost:8080');
  return httpRequest;
}

// a helper function to make a GET request.
function get(url, body){
    const httpRequest = request(app).get(url);
    httpRequest.send(body);
    httpRequest.set('Accept', 'application/json');
    httpRequest.set('Origin', 'http://localhost:8080');
    return httpRequest;
  }