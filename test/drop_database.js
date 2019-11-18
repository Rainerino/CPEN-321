const mongoose = require('mongoose');
const dotenv = require('dotenv');
const chalk = require('chalk');

dotenv.config({ path: '.env.example' });


const mongodbconnection = require('mongoose');

mongodbconnection.connect(process.env.MONGODB_TEST_URI, {
  useNewUrlParser: true
});
const { connection } = mongodbconnection;
connection.once('open', () => {
  console.log('*** MongoDB got connected ***');
  console.log(`Our Current Database Name : ${connection.db.databaseName}`);
  mongodbconnection.connection.db.dropDatabase(console.log(`${connection.db.databaseName} database dropped.`));
  process.exit();
});
// mongoose.connect(process.env.MONGODB_TEST_URI, { useNewUrlParser: true }, (err) => {
//     if (err) console.log(err);
//     mongoose.connection.db.dropDatabase();
//     console.log('%s', chalk.red.bold('Database dropped'));
//     process.exit();
// });
