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

exports.connect = () => {
  dotenv.config({ path: '.env.example' });
  mongoose.connect(process.env.MONGODB_URI, { useNewUrlParser: true });

  mongoose.connection.on('error', (err) => {
    console.error(err);
    console.log('%s MongoDB connection error. Please make sure MongoDB is running.', chalk.red.bold('Failed:'));
    process.exit();
  });

  console.log('%s MongoDB is connected at %s.', chalk.blue.bold('Connected:'), process.env.MONGODB_URI);

};

exports.loadData = async () => {
  try {
    const users = await JSON.parse(fs.readFileSync(path.join(__dirname, './default_user.json'), 'utf-8'));

    const group = await JSON.parse(fs.readFileSync(path.join(__dirname, './default_group.json'), 'utf-8'));

    const eventCal = await JSON.parse(fs.readFileSync(path.join(__dirname, './default_cal_event.json'), 'utf-8'));

    const eventMeeting = await JSON.parse(fs.readFileSync(path.join(__dirname, './default_meeting_event.json'), 'utf-8'));

    const calendar = await JSON.parse(fs.readFileSync(path.join(__dirname, './default_calendar.json'), 'utf-8'));

    await User.insertMany(users);
    console.log('%s', chalk.green.bold('User added'));
    await Calendar.insertMany(calendar);
    console.log('%s', chalk.green.bold('calendar added'));
    await Group.insertMany(group);
    console.log('%s', chalk.green.bold('group added'));
    await Event.insertMany(eventCal);
    console.log('%s', chalk.green.bold('calendar event added'));
    await Event.insertMany(eventMeeting);
    console.log('%s', chalk.green.bold('meeting event added'));

    /**
     * Initialize all the variables
     */

    const yiyi_user = await User.findOne({ email: 'albertyanyy@gmail.com' });
    const albert_user = await User.findOne({ email: 'yiyi@gmail.com' });
    const nima_user = await User.findOne({ email: 'nima@gmail.com' });
    const yuyi_user = await User.findOne({ email: 'Yuyi@gmail.com' });
    const joe_user = await User.findOne({ email: 'joe@gmail.com' });

    const eight_am_meeting = await Event.findOne({ eventName: '8 am meeting' });
    const one_pm_meeting = await Event.findOne({ eventName: 'Repeated 1 pm meeting' });

    const cal_one = await Calendar.findOne({ calendarName: 'course schedule 1' });
    const cal_two = await Calendar.findOne({ calendarName: 'course schedule 2' });
    const cal_three = await Calendar.findOne({ calendarName: 'course schedule 3' });
    const cal_four = await Calendar.findOne({ calendarName: 'course schedule 4' });
    const cal_five = await Calendar.findOne({ calendarName: 'course schedule 5' });
    const cal_group_A = await Calendar.findOne({ calendarName: 'group A calendar' });
    const cal_group_B = await Calendar.findOne({ calendarName: 'group B calendar' });

    const event_7 = await Event.findOne({ eventName: '7 am event' });
    const event_8 = await Event.findOne({ eventName: '8 am event' });
    const event_9 = await Event.findOne({ eventName: '9 am event' });
    const event_10 = await Event.findOne({ eventName: '10 am event' });
    const event_11 = await Event.findOne({ eventName: '11 am event' });
    const event_12 = await Event.findOne({ eventName: '12 am event' });
    const event_13 = await Event.findOne({ eventName: '1 pm event' });
    const event_14 = await Event.findOne({ eventName: '2 pm event' });
    const event_15 = await Event.findOne({ eventName: '3 pm event' });
    const event_16 = await Event.findOne({ eventName: '4 pm event' });
    const event_17 = await Event.findOne({ eventName: '5 pm event' });
    const event_18 = await Event.findOne({ eventName: '6 pm event' });
    const event_19 = await Event.findOne({ eventName: '7 pm event' });
    const event_20 = await Event.findOne({ eventName: '8 pm event' });
    const event_21 = await Event.findOne({ eventName: '9 pm event' });
    const event_22 = await Event.findOne({ eventName: '10 pm event' });
    const event_23 = await Event.findOne({ eventName: '11 pm event' });

    const group_A = await Group.findOne({ groupName: 'group A' });
    const group_B = await Group.findOne({ groupName: 'group B' });
    /**
     * schedule meetings for users
     */
    await User.addMeetingToUser(yiyi_user, eight_am_meeting, true);
    await User.addMeetingToUser(albert_user, eight_am_meeting, false);
    await User.addMeetingToUser(yuyi_user, eight_am_meeting, false);
    await User.addMeetingToUser(nima_user, one_pm_meeting, true);
    await User.addMeetingToUser(joe_user, one_pm_meeting, false);

    /**
     * Add calendars to users
     */
    await User.addCalendarToUser(yiyi_user, cal_one);
    await User.addCalendarToUser(albert_user, cal_two);
    await User.addCalendarToUser(yuyi_user, cal_three);
    await User.addCalendarToUser(nima_user, cal_four);
    await User.addCalendarToUser(joe_user, cal_five);

    /**
     * Add users to group
     */
    await User.addGroupToUser(yiyi_user, group_A);
    await User.addGroupToUser(albert_user, group_A);
    await User.addGroupToUser(yuyi_user, group_B);
    await User.addGroupToUser(nima_user, group_B);

    /**
     * Add events to calendars
     */
    /**
     * Calendar 1
     */
    await Calendar.addEventToCalendar(cal_one, event_14);
    await Calendar.addEventToCalendar(cal_one, event_16);

    /**
     * Calendar 2
     */
    await Calendar.addEventToCalendar(cal_two, event_7);
    await Calendar.addEventToCalendar(cal_two, event_14);
    await Calendar.addEventToCalendar(cal_two, event_16);

    /**
     * Calendar 3
     */
    await Calendar.addEventToCalendar(cal_three, event_8);
    await Calendar.addEventToCalendar(cal_three, event_10);
    await Calendar.addEventToCalendar(cal_three, event_15);
    await Calendar.addEventToCalendar(cal_three, event_18);
    await Calendar.addEventToCalendar(cal_three, event_21);

    /**
     * Calendar 4
     */
    await Calendar.addEventToCalendar(cal_four, event_7);
    await Calendar.addEventToCalendar(cal_four, event_8);
    await Calendar.addEventToCalendar(cal_four, event_10);
    await Calendar.addEventToCalendar(cal_four, event_13);
    await Calendar.addEventToCalendar(cal_four, event_20);
    await Calendar.addEventToCalendar(cal_four, event_19);
    await Calendar.addEventToCalendar(cal_four, event_23);

    /**
     * Calendar 5
     */
    await Calendar.addEventToCalendar(cal_five, event_9);
    await Calendar.addEventToCalendar(cal_five, event_11);
    await Calendar.addEventToCalendar(cal_five, event_12);
    await Calendar.addEventToCalendar(cal_five, event_17);
    await Calendar.addEventToCalendar(cal_five, event_22);


    /**
     * create group calendar
     */
    await Calendar.combineCalendarIntoCalendar(cal_one, cal_group_A);
    await Calendar.combineCalendarIntoCalendar(cal_two, cal_group_A);

    await Calendar.combineCalendarIntoCalendar(cal_three, cal_group_B);
    await Calendar.combineCalendarIntoCalendar(cal_four, cal_group_B);

    /**
     * add calendar to group
     */
    await Group.addCalendarToGroup(group_A, cal_group_A);
    await Group.addCalendarToGroup(group_B, cal_group_B);


    /**
     * add friends
     */
    await User.addFriendToUser(albert_user, yiyi_user);
    await User.addFriendToUser(yuyi_user, yiyi_user);
    await User.addFriendToUser(nima_user, yiyi_user);
    await User.addFriendToUser(joe_user, yiyi_user);


    await console.log('Done!');
    // await process.exit();
  } catch (e) {
    console.log(e);
    // process.exit();
  }
};
//
this.connect();
this.loadData();


