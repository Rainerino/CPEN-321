const router = require('express-promise-router')();
const chalk = require('chalk');
const faker = require('faker');
const Event = require('../models/event');
const Calendar = require('../models/calendar');
const User = require('../models/user');

router.use((req, res, next) => {
  console.log('%s', chalk.red.bold('Creating Events!'));
  next();
});

router.post('/:number_of_event', (req, res) => {
  User.estimatedDocumentCount((err, userCount) => {
    if (err) return res.status(500);
    let i;
    for (i = 0; i < req.params.number_of_event; i++) {
      const start = faker.date.between(new Date('2012-01-28'), new Date('2012-01-29'));
      const end = new Date(start);
      end.setHours(end.getHours() + 1);
      const event = new Event({
        eventName: faker.lorem.word(),
        eventDescription: faker.lorem.sentence(),
        startTime: start,
        endTime: end,
        repeatType: 'WEEKLY'
      });
      console.log(event);
      event.save((err, createdGroup) => {
        if (err) { return res.status(500); }
      });
    }
    res.status(201).send(`${req.params.number_of_event} new event created!`);
  });
});
module.exports = router;
