/**
 * Module dependencies.
 */
const express = require('express');
const session = require('express-session');
const bodyParser = require('body-parser');
const logger = require('morgan');
const chalk = require('chalk');
const errorHandler = require('errorhandler');
const dotenv = require('dotenv');
const MongoStore = require('connect-mongo')(session);
const flash = require('express-flash');
const path = require('path');
const mongoose = require('mongoose');
const expressStatusMonitor = require('express-status-monitor');

/**
 * Load environment variables from .env file, where API keys and passwords are configured.
 */
dotenv.config({ path: '.env.example' });

/**
 * Controllers (route handlers).
 */
const userController = require('./controllers/user');
const groupController = require('./controllers/group');
const calendarController = require('./controllers/calendar');

/**
 * API keys and Passport configuration.
 */

/**
 * Create Express server.
 */
const app = express();

/**
 * Connect to MongoDB.
 */
mongoose.set('useFindAndModify', false);
mongoose.set('useCreateIndex', true);
mongoose.set('useNewUrlParser', true);
mongoose.set('useUnifiedTopology', true);

mongoose.connect(process.env.MONGODB_URI);

mongoose.connection.on('error', (err) => {
  console.error(err);
  console.log('%s MongoDB connection error. Please make sure MongoDB is running.', chalk.red('Failed:'));
  process.exit();
});
/**
 * Express configuration.
 */
app.set('host', process.env.OPENSHIFT_NODEJS_IP || '0.0.0.0');
app.set('port', process.env.PORT || process.env.OPENSHIFT_NODEJS_PORT || 8080);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');
app.use(expressStatusMonitor());
app.use(logger('dev'));

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// app.use(session({
//   resave: true,
//   saveUninitialized: true,
//   secret: process.env.SESSION_SECRET,
//   cookie: { maxAge: 1209600000 }, // two weeks in milliseconds
//   store: new MongoStore({
//     url: process.env.MONGODB_URI,
//     autoReconnect: true,
//   })
// }));

// app.use((req, res, next) => {
//   res.locals.user = req.user;
//   next();
// });
app.use(flash());

app.use('/', express.static(path.join(__dirname, 'public'), { maxAge: 31557600000 }));
/**
 * Primary app routes.
 */

/**
 * Login route: TODO: fix login
 */
app.get('/login/:userId', userController.getLogin);
app.post('/login', userController.postLogin);
app.post('/signup', userController.postSignup);

/**
 * User routine.
 * TODO: delete and validations.
 */
app.get('/user/:userId/account', userController.getUser);
app.get('/user/:userId/group', userController.getGroup); // get user's group list
app.get('/user/:userId/friendlist', userController.getFriendList); // get user's firendlist
app.put('/user/:userId/friendlist', userController.putFriendList); // add user to user's friendlist
app.put('/user/:userId/group', userController.putGroup); // add group to user
app.post('/user/:userId/calendar/:calendarName', userController.createCalendar); // add calendar
app.get('/user/:userId/calendar/', userController.getCalendar); // get user's calendar list
app.get('/user/:userId/suggested-friends', userController.getSuggestedFriends);
app.put('/user/:userId/suggested-friends', userController.putSuggestedFriends);
app.delete('/user/:userId/suggested-friends', userController.deleteSuggestedFriends);
// app.delete('/user/:userId', userController.deleteUser);
/**
 * Group routine.
 */
app.post('/group/:groupName', groupController.createGroup); // create new group
app.get('/group/:groupId', groupController.getGroup); // get group
app.put('/group/:groupId/userlist', groupController.addUserList); // add user to group
app.post('/group/calendar', groupController.createCalendar);
app.put('/group/calendar/:calendarId', groupController.putCalendar);
// app.delete('/group/:groupId', groupController.deleteGroup);
// app.delete('/group/userlist/:userId', groupController.deleteGroupUser);

/**
 * Chatroom routine
 *
 */

/**
 * Calendar routine
 */
app.get('/calendar/:calendarId', calendarController.getCalendar); // get calendar based on id
app.put('/calendar/:calendarId/event', calendarController.putEvent); // add events, if event doesn't exist just create one
app.delete('/calendar/:calendarId/:eventId', calendarController.deleteEvent);

/**
  * event
  */

/**
 * Error Handler.
 */
if (process.env.NODE_ENV === 'development') {
  // only use in development
  app.use(errorHandler());
} else {
  app.use((err, req, res, next) => {
    console.error(err);
    res.status(500).send('Server Error');
  });
}

/**
 * Start Express server.
 */
app.listen(app.get('port'), () => {
  console.log('%s App is running at http://localhost:%d in %s mode', chalk.green('Running:'), app.get('port'), app.get('env'));
  console.log('  Press CTRL-C to stop\n');
});

module.exports = app;
