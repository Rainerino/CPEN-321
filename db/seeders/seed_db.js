
module.exports = async function (app) {
  app.use('/seed/user', require('./seed_user'));
  app.use('/seed/event', require('./seed_event'));
  app.use('/seed/group', require('./seed_group'));
  await app.use('/seed/calendar', require('./seed_calendar'));
};
