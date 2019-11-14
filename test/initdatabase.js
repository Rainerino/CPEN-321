/**
 * @module {initdatabase}
 * yiyi: in mcld
 * Albert: Kaiser (0.11 km)
 * Ninma: ESB (0.26 km)
 * Yuyi: scarfe (0.42 km)
 * Joe: Buch A (0.86 km)
 *
 * Yiyi, Albert and Yuyi in group A, Nima, Joe in group B
 *
 */
const fs = require('fs');
const path = require('path');
const mongoose = require('mongoose');
const chalk = require('chalk');
const dotenv = require('dotenv');
const User = require('../db/models/user');
const Event = require('../db/models/event');
const Group = require('../db/models/group');
const Calendar = require('../db/models/calendar');


dotenv.config({ path: '.env.example' });
mongoose.connect(process.env.MONGODB_TEST_URI, { useNewUrlParser: true });

// mongoose.connect(process.env.MONGODB_TEST_URI, { useNewUrlParser: true }, () => {
//   mongoose.connection.db.dropDatabase();
//   console.log('Database dropped');
// });

mongoose.connection.on('error', (err) => {
  console.error(err);
  console.log('%s MongoDB connection error. Please make sure MongoDB is running.', chalk.red.bold('Failed:'));
  process.exit();
});
console.log('%s MongoDB is connected at %s.', chalk.blue.bold('Connected:'), process.env.MONGODB_TEST_URI);

const users = JSON.parse(fs.readFileSync(path.join(__dirname, './default_user.json'), 'utf-8'));

const group = JSON.parse(fs.readFileSync(path.join(__dirname, './default_group.json'), 'utf-8'));

const eventCal = JSON.parse(fs.readFileSync(path.join(__dirname, './default_cal_event.json'), 'utf-8'));

const eventMeeting = JSON.parse(fs.readFileSync(path.join(__dirname, './default_meeting_event.json'), 'utf-8'));

const calendar = JSON.parse(fs.readFileSync(path.join(__dirname, './default_calendar.json'), 'utf-8'));

console.log(users);

async function loadUsers() {
  try {
    await User.insertMany(users);
    await Calendar.insertMany(calendar);
    await Group.insertMany(group);
    await Event.insertMany(eventCal);
    await Event.insertMany(eventMeeting);

    User.findOneAndUpdate({firstName: 'Yiyi'}, );

    console.log('Done!');
    process.exit();
  } catch (e) {
    console.log(e);
    process.exit();
  }
}

loadUsers();
