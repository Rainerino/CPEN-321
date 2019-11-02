const JWT = require('jsonwebtoken');
const User = require('../models/user');
const { JWT_SECRET, oauth } = require('../configuration');
const { google } = require('googleapis');
const googleAuth = require('google-auth-library');

const GOOGLE_REDIRECT_URL = "http://localhost:3000/users/oauth/google";
const ONE_WEEK = 7 * 24 * 60 * 60 * 1000; // One week's time in ms

// This is the payload
signToken = (user) => {
    return JWT.sign({
        iss: 'Nimanasiribrah',
        sub: user.id,
        iat: new Date().getTime(), // current time
        exp: new Date().setDate(new Date().getDate() + 1), // current time + 1 day
    }, JWT_SECRET);
}


module.exports = {
    signUp: async (req, res, next) => {
        const { email, password } = req.value.body;

        // Check if there is a user with the same email
        const foundUser = await User.findOne({ "local.email": email });
        if (foundUser) { 
            res.status(403).send({ error: 'Email already in use' });
        } else {
            //Create new user according to model, key and value are same so don't need email:email
            const newUser = new User({ 
                method: 'local', 
                local: {
                    email: email,
                    password: password
                }});
            await newUser.save();

            // Generate token, we only worry about payload.
            const token = signToken(newUser);

            // Respond with token
            res.status(200).json({ token });
        }
    },

    signIn: async (req, res, next) => {
        // Generate tokens
        const token = signToken(req.user);
        res.status(200).json({ token });
    },

    googleOAuth: async(req, res, next) => {
        var oauth2Client = new googleAuth.OAuth2Client(
            oauth.google.clientID,
            oauth.google.clientSecret,
            GOOGLE_REDIRECT_URL
        );

        // Check the OAuth 2.0 Playground to see request body example,
        // must have Calendar.readonly and google OAuth2 API V2 for user.email 
        // and user.info
        oauth2Client.setCredentials(req.body);

        var calendar = google.calendar('v3');

        // This date and time are both ahead by 9 hours, but we only worry 
        // about getting the recurring events throughout one week.
        let todays_date = new Date(Date.now());
        let next_week_date = new Date(todays_date.getTime() + ONE_WEEK);

        // Assuming user wants to use their "primary" calendar
        calendar.events.list({
            auth: oauth2Client,
            calendarId: 'primary', 
            timeMin: todays_date.toISOString(), 
            timeMax: next_week_date.toISOString(),
        }, function(err, response) {
            if (err) {
                console.log('The API returned an error: ' + err);
                return;
            }
            var events = response.data.items;
            events.forEach(function(event) {
                var start = event.start.dateTime || event.start.date;
                // If it's a recurring event, print it.
                if (event.recurrence) {
                console.log('%s - %s', start, event.summary);
                }
            });
        });

        // Generate token
        const token = signToken(req.user);
        res.status(200).json({ token });
    },

    facebookOAuth: async(req,res,next) => {
        console.log('Got here');
    },

    secret: async (req, res, next) => {
        console.log('I managed to get here');
        res.json({ secret: "resource"});
    }
}