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

var express = require('express');
var router = express.Router();
let user = require("./user");


// middleware that is specific to this router
// router.use(function timeLog (req, res, next) {
//   console.log('Time: ', Date.now())
//   next()
// })
// define the home page route
router.get('/', function (req, res) {
  res.send('Birds home page')
})
// define the about route

// find user

router.route("/user").get(user.getUser).post(user.postUser)

module.exports = router