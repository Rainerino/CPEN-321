const router = require('express-promise-router')();
const chalk = require('chalk');
const faker = require('faker');
const Group = require('../models/group');
const Calendar = require('../models/calendar');
const User = require('../models/user');

router.use((req, res, next) => {
  console.log('%s', chalk.red.bold('Creating group!'));
  next();
});

router.post('/:number_of_group', (req, res) => {
  User.estimatedDocumentCount((err, userCount) => {
    if (err) return res.status(500);
    const randomUserNumber = Math.floor(Math.random() * process.env.SEED_GROUP_USER_SIZE);
    let i;
    for (i = 0; i < req.params.number_of_group; i++) {
      const group = new Group({
        groupName: faker.lorem.word(),
        groupDescription: faker.lorem.sentence(),
        location: {
          type: 'Point',
          coordinate: [faker.address.longitude(), faker.address.latitude()]
        },
      });
      group.save((err, createdGroup) => {
        if (err) { return res.status(500); }
      });
    }
    res.status(201).send(`${req.params.number_of_group} new groups created!`);
  });
});
module.exports = router;
