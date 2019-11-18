const mongoose = require('mongoose');
const chalk = require('chalk');
const dotenv = require('dotenv');
const complex = require('../../core/preference');
const User = require('../../db/models/user');
dotenv.config({ path: '.env.example' });
mongoose.connect(process.env.MONGODB_TEST_URI, { useNewUrlParser: true });

mongoose.connection.on('error', (err) => {
    console.error(err);
    console.log('%s MongoDB connection error. Please make sure MongoDB is running.', chalk.red.bold('Failed:'));
    process.exit();
});

console.log('%s MongoDB is connected at %s.', chalk.blue.bold('Connected:'), process.env.MONGODB_TEST_URI);



async function f() {
    const yiyi_user = await User.findOne({email: 'albertyanyy@gmail.com'});
    const albert_user = await User.findOne({email: 'yiyi@gmail.com'});
    const nima_user = await User.findOne({email: 'nima@gmail.com'});
    const yuyi_user = await User.findOne({email: 'Yuyi@gmail.com'});
    const joe_user = await User.findOne({email: 'joe@gmail.com'});

    const suggested_distance  = await complex.collectNearestFriends(yiyi_user._id);
    await console.log(suggested_distance);

    let start = new Date('2019-11-11T07:00:00.000+00:00');
    let end = new Date('2019-11-11T08:00:00.000+00:00');

    let suggested_time = await complex.collectFreeFriends(yiyi_user._id, start, end);
    await console.log(suggested_time);


    start = new Date('2019-11-11T08:00:00.000+00:00');
    end = new Date('2019-11-11T09:00:00.000+00:00');
    suggested_time = await complex.collectFreeFriends(yiyi_user._id, start, end);
    await console.log(suggested_time);

    start = new Date('2019-11-11T19:00:00.000+00:00');
    end = new Date('2019-11-11T20:00:00.000+00:00');
    suggested_time = await complex.collectFreeFriends(yiyi_user._id, start, end);
    await console.log(suggested_time);


}
f();