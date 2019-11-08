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

router.post('/:calendarId/event/:numberOfEvents', async (req, res) => {
  const total = await Event.estimatedDocumentCount();
  const list = await Array(Number(req.params.numberOfEvents)).fill();
  const eventList = await list.map(() => Event.findOne().skip(Math.floor(Math.random() * total)));
  const event = await Promise.all(eventList);
  const eventIdList = await event.map((event) => event._id);
  const calendar = await Calendar.findByIdAndUpdate(req.params.calendarId,
    { $addToSet: { eventList: eventIdList } });

  await res.status(200).json(calendar);
  console.log(eventIdList);
  console.log(calendar);
});


module.exports = router;
