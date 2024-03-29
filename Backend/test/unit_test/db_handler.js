// tests/db-handler.js

const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');

const mongod = new MongoMemoryServer();
// const init = require('../../db/seeders/init_database');
/**
 * Connect to the in-memory database.
 */
module.exports.connect = async () => {
  const uri = await mongod.getConnectionString();
  console.log('uri: ', uri);

  const mongooseOpts = {
    useNewUrlParser: true,
    autoReconnect: true,
    reconnectTries: Number.MAX_VALUE,
    reconnectInterval: 1000
  };

  await mongoose.connect(uri, mongooseOpts);
};

/**
 * Drop database, close the connection and stop mongod.
 */
module.exports.closeDatabase = async () => {
  await mongoose.connection.dropDatabase();
  await mongoose.connection.close();
  await mongod.stop();
};

/**
 * Remove all the data for all db collections.
 */
module.exports.clearDatabase = async () => {
  const { collections } = mongoose.connection;

  for (const key in collections) {
    const collection = collections[key];
    await collection.deleteMany();
  }
};
