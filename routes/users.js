const express = require('express');
const router = require('express-promise-router')();
const passport = require('passport');
const passportConf = require('../passport');

const { validateBody, schemas } = require('../helpers/routeHelpers');
//Do stuff with the post data
const UsersController = require('../controllers/users');
const passportSignIn = passport.authenticate('local', { session: false });
const passportJWT = passport.authenticate('jwt', { session: false});
const passportGoogle = passport.authenticate('googleToken', { session: false });

/* For each subdomain the request is routed to the respective controller. */

//first validateBody() is called, then UsersController.signUp (next() must be called in all functions before the last one)
router.route('/signup') 
    .post(validateBody(schemas.authSchema), UsersController.signUp);

router.route('/signin')
    .post(validateBody(schemas.authSchema), passportSignIn, UsersController.signIn);

router.route('/google-oauth')
    .post(passportGoogle, UsersController.googleOAuth);

router.route('/google-calendar')
    .post(UsersController.googleCalendar);

router.route('/secret')
    .get(passportJWT, UsersController.secret);

module.exports = router;