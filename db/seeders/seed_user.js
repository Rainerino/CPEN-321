const router = require("express-promise-router")();
//const chalk = require("chalk");
const faker = require("faker");
const User = require("../models/user");

router.use((next) => {
  next();
});

function saveUser(err, createdUser) {
  if (err) { return res.status(500); }
  if (!createdUser) { return res.status(500).send("Create failed!");
}

function checkUserExists(err, existingUser) {
  if (err) { return res.status(500); }
  if (existingUser) {
    return res.status(403).send("Account with that email address already exists.");
  }
}

router.post("/:number_of_user", (req, res) => {
  let i;
  for (i = 0; i < req.params.number_of_user; i++) {
    const fullName = faker.name.findName().split(" ");
    const user = new User({
      firstName: fullName[0],
      lastName: fullName[1],
      email: faker.internet.email(),
      password: faker.internet.password(),
      location: {
        type: "Point",
        coordinate: [faker.address.longitude(), faker.address.latitude()],
        city: faker.address.city(),
        country: faker.address.country()
      }
    });
    User.findOne({ email: req.body.email }, (err, existingUser) => {
      checkUserExists(err, existingUser);
      // validation needed
      user.saveUser(err, createdUser);
    });
  }
  return res.status(201).send(`${req.params.number_of_user} new users created!`);
});

module.exports = router;
