'use strict';

module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define('User', {
    firstName: DataTypes.STRING,
    lastName: DataTypes.STRING,
    email: {
      type: DataTypes.STRING,
      allowNull: false,
      validate: {
        isEmail: true
      }
      
    },
    userName: DataTypes.STRING,
    password: {
      type: DataTypes.STRING,
      allowNull: false
    },
    facebookAPIToken: DataTypes.STRING
  }, {});
  User.associate = function(models) {
    User.hasMany(models.Calendar, {
      foreignKey: 'calendarId',
      as: 'calendars',
      constraints: false
    }),
    User.hasMany(models.Group, {
      foreignKey: 'groupId',
      as: 'groups',
      constraints: false
    }),
    {}
  };
  return User;
};