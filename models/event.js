'use strict';
module.exports = (sequelize, DataTypes) => {
  const Event = sequelize.define('Event', {
    time: DataTypes.DATE,
    duration: DataTypes.INTEGER,
    eventName: DataTypes.STRING
  }, {});
  Event.associate = function(models) {
    Event.belongsTo(models.Calendar, {
      foreignKey: 'calendarId',
      onDelete: 'CASCADE'
    })
  };
  return Event;
};