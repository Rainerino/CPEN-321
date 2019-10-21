'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
   const users = await queryInterface.sequelize.query(
    `SELECT id from Users;`
    );

  const userRows = users[0];
   return queryInterface.bulkInsert('Groups', 
   [{
     groupName: "The one and only",
     createdAt: new Date(),
     updatedAt: new Date()
   },
   {
    groupName: "The one and only with others", 
    createdAt: new Date(),
    updatedAt: new Date()
    // users: userRows[0].id
   }]
   )
  },

  down: (queryInterface, Sequelize) => {
    /*
      Add reverting commands here.
      Return a promise to correctly handle asynchronicity.

      Example:
      return queryInterface.bulkDelete('People', null, {});
    */
   return queryInterface.bulkDelete('Groups', null, {});
  }
};
