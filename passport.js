const passport = require('passport');
const JwtStrategy = require('passport-jwt').Strategy;
const { ExtractJwt } = require('passport-jwt');
const LocalStrategy = require('passport-local').Strategy;
const GooglePlusTokenStrategy = require('passport-google-plus-token');
const FacebookTokenStrategy = require('passport-facebook-token');


const config = require('./configuration');
const { JWT_SECRET } = require('./configuration');
const User = require('./models/user');


// JWT Strategy: We extract JWT, and have payload passed in/done returned;
passport.use(new JwtStrategy({
    jwtFromRequest: ExtractJwt.fromHeader('authorization'),
    secretOrKey: JWT_SECRET
}, async (payload, done) => { 
    try {
        // Find the user specified in token
        const user = await User.findById(payload.sub);
        if (!user) {
            return done(null, false);
        }

        // Otherwise, return the user
        done(null, user);
    } catch(error){
        done(error, false);
    }
}));

// GOOGLE OAUTH STRATEGY
// Whenever we use passport.authenticate it will understand that googleToken refers to the whole strategy.
passport.use('googleToken', new GooglePlusTokenStrategy({
    clientID: config.oauth.google.clientID,
    clientSecret: config.oauth.google.clientSecret
}, async (accessToken, refreshToken, profile, done) => {
    try {
        console.log('profile', profile);
        console.log('accessToken', accessToken);
        console.log('refreshToken', refreshToken);
        // Check whether this current user exists in our DB
        const existingUser = await User.findOne({ "google.id": profile.id });
        if (existingUser) {
            console.log('User already exists in our DB');
            return done(null, existingUser);
        }
        
        console.log('User doesn\'t exist in our DB, making a new one');
        // If new account
        const newUser = new User({
            method: 'google',
            google: {
                id: profile.id,
                email: profile.emails[0].value
            }
        });
        await newUser.save()
        done(null, newUser);

    } catch(error) {
        done(error, false, error.message);
    }
}));

passport.use('facebookToken', new FacebookTokenStrategy({
    clientID: config.oauth.facebook.clientID,
    clientSecret: config.oauth.facebook.clientSecret
}, async (accessToken, refreshToken, profile, done) => {
    try {
        console.log('profile', profile);
        console.log('accessToken', accessToken);
        console.log('refreshToken', refreshToken);
    } catch(error) {
        done(error, false, error.message);
    }
}));

// Local Strategy: Authorize using username and password
passport.use(new LocalStrategy({
    usernameField: 'email'
}, async (email, password, done) => {
    try {
        // Find the user given the email
        const user = await User.findOne({ "local.email": email });

        // If not, handle it
        if (!user) {
            return done(null, false);
        }

        // Check if the password is correct
        const isMatch = await user.isValidPassword(password);
    
        // If not, handle it
        if (!isMatch) {
            return done(null, false);
        }
    
        // Otherwise reutrn user
        done(null, user);
    } catch (error) {
        done(error, false);
    }
}))