'use strict';
module.exports = (sequelize, DataTypes) => {
  const Group = sequelize.define('Group', {
    groupName: DataTypes.STRING,
  }, {});
  Group.associate = function(models) {
    Group.hasMany(models.User, {
      foreignKey: "userId",
      as: "users",
    }),
    Group.hasOne(models.Calendar, {
      foreignKey: "calendarId",
      as: "calendar",
      constraints: false
    })
  };
  return Group;
};
