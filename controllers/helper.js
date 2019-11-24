const log4js = require('log4js');
const dotenv = require('dotenv');

dotenv.config({ path: '../.env.example' });

// helper function to get the logger

// trace, debug, info, warn, error, fatal
exports.getMyLogger = (type) => {
  const logger = log4js.getLogger(type);
  logger.level = process.env.LOGGER_MESSAGE_LEVEL;
  return logger;
};

exports.checkNullArgument = (length, ...args) => {
  for (let i = 0; i < length; i++) {
    if (!args[i]) {
      return false;
    }
  }
  return true;
};
