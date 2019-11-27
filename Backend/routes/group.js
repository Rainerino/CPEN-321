/**
 * @module Group routine.
 */
const router = require('express-promise-router')();
const passport = require('passport');
const groupController = require('../controllers/group');

const passportJWT = passport.authenticate('jwt', { session: false });

const userController = require('../controllers/user/user');
// middleware that is specific to this router
router.use((req, res, next) => {
  // console.log('Time: ', Date.now());
  next();
});

// create new group
router.post('/create', passportJWT, groupController.createGroup);
// get group's object
router.get('/:groupId', passportJWT, groupController.getGroup);
// add one user to group
router.put('/add/user', passportJWT, userController.putGroup);
// remove user from the group
router.delete('/delete/user', passportJWT, userController.deleteGroup);

module.exports = router;
