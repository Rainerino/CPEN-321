const express = require('express');
const logger = require('morgan');
const bodyParser = require('body-parser');
const path = require('path');
// This will be our application entry. We'll setup our server here.
const http = require('http');
// Set up the express app
const app = express();
const createError = require('http-errors');
const routes = require('./app/routes/index')
// Log requests to the console.
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, 'public')));

// Parse incoming requests data (https://github.com/expressjs/body-parser)
// app.use(bodyParser.json());
// app.use(bodyParser.urlencoded({ extended: false }));
app.use('/', routes)

var models = require("./models")

models.sequelize.sync().then(function(){
  console.log("Database connected");

}).catch(function(err) {
  console.log(err, "Database not connected");
});

// Setup a default catch-all route that sends back a welcome message in JSON format.
const port =  8000;
// app.set('port', port);

// const server = http.createServer(app);

app.listen(port);

console.log("Listening on port " + port)

module.exports = app;
