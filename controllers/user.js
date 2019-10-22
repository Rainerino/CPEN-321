const User = require('../models/User');

/**
 * GET /login
 * login with default userid
 *
 */
exports.getLogin = (req, res) => {
  User.findById(req.params.userId, (err, existingUser) => {
    if (err) { return res.status(400); }
    if (existingUser) { return res.json(existingUser); }
    // TODO: add more password and account validation
    return res.status(404).send("Account with that email address doesn\'t exist.");
  });
};

/**
 * POST /login
 * Sign in using email and password.
 */
exports.postLogin = (req, res, next) => {
  console.log(req.body.email);
  User.find({ email: req.body.email }, (err, existingUser) => {
    if (err) { return next(err); }
    if (existingUser.length > 0) { return res.status(200).json(existingUser); }
    res.status(404).send('Account with that email address doesn\'t exist.');
  });
};
/**
 * POST /signup
 * Create a new local account.
 */
exports.postSignup = (req, res, next) => {
  console.log(req.body);
  const user = new User({
    email: req.body.email,
    password: req.body.password
  });
  User.findOne({ email: req.body.email }, (err, existingUser) => {
    if (err) { return next(err); }
    if (existingUser) {
      return res.status(403).send('Account with that email address already exists.');
    }
    // validation needed
    user.save((err, createdUser) => {
      if (err) { return next(err); }
      res.status(201).json(createdUser);
    });
  });
};
