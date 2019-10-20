'use strict';

module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define('User', {
    firstName: DataTypes.STRING,
    lastName: DataTypes.STRING,
    email: {
      type: DataTypes.STRING,
      allowNull: false
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
      foreignKey: "userId",
      as: "calendar"
    }),
    User.hasMany(models.Group, {
      foreignKey: "groupId",
      as: "group"
    }),
    User.belongsTo(models.Group, {
      foreignKey: "userId",
      as: "user"
    })
  };
  return User;
};