/** Study Buddy main backend program
 * @module app
 * @requires express
 * @requires bodyParser
 * @requires morgan
 * @requires chalk
 * @requires express-session
 * @requires errorHandler
 * @requires dotenv
 * @requires connect-mongo
 * @requires express-flash
 * @requires path
 * @requires mongoose
 * @requires express-status-monitor
 * @requires socket.io
 */
const express = require('express');
const bodyParser = require('body-parser');
const chalk = require('chalk');
const errorHandler = require('errorhandler');
const dotenv = require('dotenv');
const morgan = require('morgan');
const path = require('path');
const mongoose = require('mongoose');
const admin = require('firebase-admin');
const expressStatusMonitor = require('express-status-monitor');
const http = require('http');

const app = express();

const server = http.createServer(app);
const io = require('socket.io').listen(server);
const helper = require('./controllers/helper');
const serviceAccount = require('./config/firebase-admin');
const User = require('./db/models/user');

const logger = helper.getMyLogger('Server');

/**
 * Load environment variables from .env file, where API keys and passwords are configured.
 */
dotenv.config({ path: '.env.example' });
/**
 * API keys and Passport configuration.
 */
/**
 * Create Express server.
 */
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(morgan('dev'));
/**
 * Connect to MongoDB.
 */
mongoose.set('useFindAndModify', true);
mongoose.set('useCreateIndex', true);
mongoose.set('useNewUrlParser', true);
mongoose.set('useUnifiedTopology', true);

//mongoose.connect('mongodb://localhost/APIAuthentication');
mongoose.connect(process.env.MONGODB_URI);

mongoose.connection.on('error', (err) => {
  logger.error(err);
  logger.info('%s MongoDB connection error. Please make sure MongoDB is running.', chalk.red.bold('Failed:'));
  process.exit();
});
logger.info('%s MongoDB is connected at %s.', chalk.blue.bold('Connected:'), process.env.MONGODB_URI);
/**
 * Express configuration.
 */
app.set('host', process.env.OPENSHIFT_NODEJS_IP || '0.0.0.0');
app.set('port', process.env.PORT || process.env.OPENSHIFT_NODEJS_PORT || 8080);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');
app.use(expressStatusMonitor());

// Set up firebase notification
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: process.env.MONGODB_URI
});

app.use('/', express.static(path.join(__dirname, 'public')));

// display all the user locations
app.get('/', async (req, res) => {
  const lat = [];
  const lon = [];
  const users = await User.getUsers();
  await users.forEach((user) => {
    lat.push(user.location.coordinate[1]);
    lon.push(user.location.coordinate[0]);
  });
  logger.debug(lat, lon);
  res.render('index', { lat, lon });
});

/**
 * Primary app routes.
 */
/**
 * Routes for users
 */
app.use('/user', require('./routes/user'));
/**
 * Routes for groups
 */
app.use('/group', require('./routes/group'));
/**
 * Routes for events
 */
app.use('/event', require('./routes/event'));
/**
 * Routes for events
 */
app.use('/calendar', require('./routes/calendar'));
/**
 * Seeding routes
 */
/**
 * Error Handler.
 */
if (process.env.NODE_ENV === 'development') {
  // only use in development
  app.use(errorHandler());
} else {
  app.use((err, req, res, next) => {
    res.status(500).send('Server Error');
  });
}
/**
 * Socket io connector
 */
io.on('connection', (socket) => {
  logger.info('user connected');

  socket.on('join', (userNickname) => {
    logger.info(`${userNickname} : has joined the chat `);

    socket.broadcast.emit('userjoinedthechat', `${userNickname} : has joined the chat `);
  });

  socket.on('messagedetection', (senderUserId, receiverUserId, messageContent) => {
    logger.info(`From ${senderUserId} to ${receiverUserId} : ${messageContent}`);
    const message = {
      message: messageContent,
      senderId: senderUserId,
      receiverId: receiverUserId
    };
    io.emit('message', message);
  });

  socket.on('chatroomDestroy', (userId, userName) => {
    logger.info(`${userId} has left!`);
    // use username instead
    socket.broadcast.emit('userdisconnect', `${userId} user has left`);
  });
  socket.on('disconnect', () => {
  });
});

server.listen(3000, () => { logger.info('%s Socket.io app is running on port 3000', chalk.bold.cyan('Running:')); });

/**
 * Start Express server.
 */
app.listen(app.get('port'), () => {
  logger.info('%s App is running at http://localhost:%d in %s mode', chalk.bold.magenta('Running:'), app.get('port'), app.get('env'));
  logger.info('  Press CTRL-C to stop\n');
});


module.exports = app;
