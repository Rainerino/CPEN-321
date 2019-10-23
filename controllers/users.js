const JWT = require('jsonwebtoken');
const User = require('../models/user');
const { JWT_SECRET } = require('../configuration');

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