const log4js = require('log4js');
const dotenv = require('dotenv');

dotenv.config({ path: '../.env.example' });

// helper function to get the logger

// trace, debug, info, warn, error, fatal
exports.getMyLogger = (type) => {
  log4js.configure({
    appenders: { console: { type: 'console' } },
    categories: { default: { appenders: [ 'console' ], level: 'debug' } }
  });
  return log4js.getLogger(type);
};

exports.checkNullArgument = (length, ...args) => {
  for (let i = 0; i < length; i++) {
    if (!args[i]) {
      return false;
    }
  }
  return true;
};

/**
 * Helper function for complex logic
 * @param array1
 * @param array2
 * @returns {[]} - common elements of the two arrays
 */
exports.findCommonElement = (array1, array2) => {
  const result = [];
  for (let i = 0; i < array1.length; i++) {
    for (let j = 0; j < array2.length; j++) {
      if (array1[i].equals(array2[j])) {
        result.push(array1[i]);
      }
    }
  }
  return result;
};
