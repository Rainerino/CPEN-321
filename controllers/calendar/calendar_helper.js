const Event = require('../../db/models/event');
const helper = require('../helper');

const logger = helper.getMyLogger('Calendar Helper');

/**
 *
 * @param {Event} event
 * @param {Date} date
 */
function updateEventToDate(event, date) {
  const startTime = event.startTime;
  const endTime = event.endTime;

  // update start time
  startTime.setFullYear(date.getFullYear());
  startTime.setMonth(date.getMonth());
  startTime.setDate(date.getDate());

  // update end time
  endTime.setFullYear(date.getFullYear());
  endTime.setMonth(date.getMonth());
  endTime.setDate(date.getDate());

  // update event.
  Event.findByIdAndUpdate(event._id,
    { $set: { startTime, endTime } },
    { new: true, useFindAndModify: false },
    (err, event) => {
      if (err) throw err;
      if (!event) throw new Error('event not found');
      // logger.debug(event.toString());
    });
}
/**
 *
 * @param {Date} date1
 * @param {Date} date2
 * @returns {boolean|*}
 */
function checkSameWeek(date1, date2) {
  const diffInDay = Math.abs((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
  logger.debug(`Day in difference ${diffInDay}`);
  if (diffInDay % 7 !== 0) {
    return false;
  }
  return true;
}
/**
 *
 * @param {Date} date1
 * @param {Date} date2
 * @returns {boolean|*}
 */
function checkSameMonth(date1, date2) {
  let months;
  months = (date2.getFullYear() - date1.getFullYear()) * 12;
  months -= date1.getMonth() + 1;
  months += date2.getMonth();
  return months <= 0 ? 0 : months;
}
/**
 *
 * @param {Event} event
 * @param {Date} date - the date to check
 * @returns {Boolean} result - if the event repeated and updated or not
 */
exports.checkEventRepeatAndUpdate = (event, date) => {
  let result = true;
  switch (event.repeatType) {
    case 'DAILY':
      updateEventToDate(event, date);
      break;
    case 'WEEKLY':
      result = checkSameWeek(event.startTime, date);
      if (result) {
        updateEventToDate(event, date);
      }
      break;
    case 'MONTHLY':
      result = checkSameMonth(event.startTime, date);
      if (result) {
        updateEventToDate(event, date);
      }
      break;
    default:
      result = false;
  }
  return result;
};

/**
 * @param {List} inputEventIdList - list of event list.
 * @param {Date} date - the date to return from, also update the events.
 * @desc return a event of today, and update repeating events
 */
exports.getEventsOfDay = async (inputEventIdList, date) => {
  try {
    const eventIdList = await inputEventIdList.map((eventId) => {
      return Event.findById(eventId, (err, event) => {
        return this.checkEventRepeatAndUpdate(event, date);
      });
    });
    const resultList = await Promise.all(eventIdList);

    const eventList = await inputEventIdList.filter((eventId) =>
      resultList[inputEventIdList.indexOf(eventId)]);

    const objectList = await Event.id2ObjectList(eventList);

    return objectList;
  } catch (e) {
    logger.error(e.toString());
    throw e;
  }
};
