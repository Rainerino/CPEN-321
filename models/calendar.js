'use strict';
module.exports = (sequelize, DataTypes) => {
  const Calendar = sequelize.define('Calendar', {
    calendarName: DataTypes.STRING
  }, {});
  Calendar.associate = function(models) {
    Calendar.belongsTo(models.User, {
      foreignKey: 'userId',
      onDelete: 'CASCADE',
    }),
    Calendar.belongsTo(models.Group, {
      foreignKey: 'groupId',
      onDelete: 'CASCADE',
    }),
    Calendar.hasMany(models.Event, {
      foreignKey: 'eventId',
      as: 'events'
    })
    // associations can be defined here
  };
  return Calendar;
};