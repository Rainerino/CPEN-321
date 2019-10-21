'use strict';

module.exports = {
  up: (queryInterface, Sequelize) => {
    /*
      Add altering commands here.
      Return a promise to correctly handle asynchronicity.

      Example:
      return queryInterface.bulkInsert('People', [{
        name: 'John Doe',
        isBetaMember: false
      }], {});
    */
   return queryInterface.bulkInsert('Users', 
   [{
    firstName: 'John',
    lastName: 'Doe',
    email: 'demo@demo.com',
    password: '321',
    facebookAPIToken: 'what are you talking about',
    createdAt: new Date(),
    updatedAt: new Date()
  }, 
  {
    firstName: 'Lam',
    lastName: 'what',
    email: 'albert@demo.com',
    password: '321',
    facebookAPIToken: 'what are you talking about?',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    firstName: 'Times',
    lastName: 'White',
    email: 'timmmmy@demo.com',
    password: '321',
    facebookAPIToken: 'what are you talking about?!',
    createdAt: new Date(),
    updatedAt: new Date()
  }],
  );
  },
  down: (queryInterface, Sequelize) => {
    /*
      Add reverting commands here.
      Return a promise to correctly handle asynchronicity.

      Example:
      return queryInterface.bulkDelete('People', null, {});
    */
   return queryInterface.bulkDelete('Users', null, {});
  }
};
