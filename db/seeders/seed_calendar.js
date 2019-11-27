const router = require("express-promise-router")();
//const chalk = require("chalk");
const faker = require("faker");
const Calendar = require("../models/calendar");

router.use((next) => {
  next();
});

router.post("/:number_of_calendar", (req, res) => {
  let i;
  for (i = 0; i < req.params.number_of_calendar; i++) {
    const calendar = new Calendar({
      calendarName: faker.lorem.word(),
      calendarDescription: faker.lorem.sentence(),
    });
    // validation needed
    calendar.save((err) => {
      if (err) { return res.status(500); }
    });
  }
  return res.status(201).send(`${req.params.number_of_calendar} new calendar created!`);
});

module.exports = router;
