const router = require('express-promise-router')();
const chalk = require('chalk');
const faker = require('faker');
const Calendar = require('../models/calendar');
const Event = require('../models/event');

router.use((req, res, next) => {
  console.log('%s', chalk.red.bold('Creating Calendar!'));
  next();
});

router.post('/:number_of_calendar', (req, res) => {
  let i;
  console.log(req.params.number_of_calendar);
  for (i = 0; i < req.params.number_of_calendar; i++) {
    const calendar = new Calendar({
      calendarName: faker.lorem.word(),
      calendarDescription: faker.lorem.sentence(),
    });
    // validation needed
    calendar.save((err, cal) => {
      if (err) { return res.status(500); }
      console.log(cal);
    });
  }
  return res.status(201).send(`${req.params.number_of_calendar} new calendar created!`);
});

router.post('/event/:number_of_events', async (req, res) => {
  // // get an array of
  const total = await Event.estimatedDocumentCount();
  // const list = Array(Math.round(Math.random() * process.env.SEED_EVENT_USER_SIZE)).fill();
  // return Event.findOne().skip(Math.floor(Math.random() * total));
  const list = Array(3).fill();
  const eventList = list.map(() => {
    return Event.findOne().skip(Math.floor(Math.random() * total));
  });

  const event = await Promise.all(eventList);

  const eventIdList = await event.map((event) => event._id);


  console.log(eventIdList);
  return res.status(200).send('good');
});


module.exports = router;
