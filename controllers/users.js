const fs = require('fs'); //NOT NECESSARY, for calendar testing
const JWT = require('jsonwebtoken');
const User = require('../models/user');
const { JWT_SECRET, oauth } = require('../configuration');
const { google } = require('googleapis');
const googleAuth = require('google-auth-library');


const TOKEN_PATH = 'calendar-nodejs-quickstart.json';
const REDIRECT_URL = "http://localhost:3000/users/oauth/google";

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
        // Going through oauth to get calendar
        const client_id = oauth.google.clientID;
        const client_secret = oauth.google.clientSecret;

        var oauth2Client = new googleAuth.OAuth2Client(
            client_id,
            client_secret,
            REDIRECT_URL
        );

        const google_token = fs.readFileSync(TOKEN_PATH);
        oauth2Client.setCredentials(JSON.parse(google_token));

        // Got calendar
        var calendar = google.calendar('v3');

        calendar.events.list({
            auth: oauth2Client,
            calendarId: "nimanasirisoccerguy@gmail.com", //Need to grab a calendar ID
            timeMin: "2019-11-01T00:00:00.000Z", 
            timeMax: "2019-11-01T23:59:59.000Z"
        }, function(err, response) {
            if (err) {
                console.log('The API returned an error: ' + err);
                return;
            }
            var events = response.data.items;
            events.forEach(function(event) {
                var start = event.start.dateTime || event.start.date;
                console.log('%s - %s', start, event.summary);
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