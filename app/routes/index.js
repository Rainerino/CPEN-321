// const express = require('express')
// const router = express.Router()
// const { catchErrors } = require('../helpers/errorHandler')

// router.get('/', function (req, res) {
//     res.send('Birds home page')
//   })

  
// //   var amount = 5
  
// //   app.get('/wat', (req, res) => res.status(200).send({
// //     message: 'Welcome to the beginning of nothingness.',
// //     test: 'where is $(amount) tips'
// //   }));

// module.exports = router
const userController = require('../controllers/user')
const User = require("../../models").User;

var router = require('express').Router();

router.post('/api/user/create',function (req, res) {
  User.create({ 
    first_name: req.body.first_name,
    last_name: req.body.last_name,
    bio: req.body.bio,
    email: req.body.email,
  })
    .then(function (user) {
      res.json(user);
    });
});


router.get('/api/user/list', function(req, res) {
  User.findAll()
    .then(function (users) {
      res.json(users);
    });
});


router.get('/api', (req, res) => res.status(200).send(
{
  message: 'waht'
}));



module.exports = router
