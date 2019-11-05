/** Study Buddy main backend program
 * @module app
 * @requires express
 * @requires bodyParser
 * @requires morgan
 * @requires chalk
 * @requires express-session
 * @requires errorHandler
 * @requires dotenv
 * @requires connect-mongo
 * @requires express-flash
 * @requires path
 * @requires mongoose
 * @requires express-status-monitor
 * @requires socket.io
 */
const express = require("express");
//const session = require("express-session");
const bodyParser = require("body-parser");
const logger = require("morgan");
//const chalk = require("chalk");
const errorHandler = require("errorhandler");
const dotenv = require("dotenv");
//const MongoStore = require("connect-mongo")(session);
const flash = require("express-flash");
const path = require("path");
const mongoose = require("mongoose");
const expressStatusMonitor = require("express-status-monitor");

const app = express();
const http = require("http");

const server = http.createServer(app);
const io = require("socket.io").listen(server);

const ONE_YEAR = 315576 * 100000;

/**
 * Load environment variables from .env file, where API keys and passwords are configured.
 */
dotenv.config({ path: ".env.example" });
/**
 * API keys and Passport configuration.
 */
/**
 * Create Express server.
 */
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
/**
 * Connect to MongoDB.
 */
mongoose.set("useFindAndModify", false);
mongoose.set("useCreateIndex", true);
mongoose.set("useNewUrlParser", true);
mongoose.set("useUnifiedTopology", true);

mongoose.connect(process.env.MONGODB_URI);

mongoose.connection.on("error", () => {
  process.exit();
});
/**
 * Express configuration.
 */
app.set("host", process.env.OPENSHIFT_NODEJS_IP || "0.0.0.0");
app.set("port", process.env.PORT || process.env.OPENSHIFT_NODEJS_PORT || 8080);
app.set("views", path.join(__dirname, "views"));
app.set("view engine", "pug");
app.use(expressStatusMonitor());
app.use(logger("dev"));


// app.use(session({
//   resave: true,
//   saveUninitialized: true,
//   secret: process.env.SESSION_SECRET,
//   cookie: { maxAge: 1209600000 }, // two weeks in milliseconds
//   store: new MongoStore({
//     url: process.env.MONGODB_URI,
//     autoReconnect: true,
//   })
// }));

// app.use((req, res, next) => {
//   res.locals.user = req.user;
//   next();
// });
app.use(flash());

app.use("/", express.static(path.join(__dirname, "public"), { maxAge: ONE_YEAR }));

/**
 * Primary app routes.
 */
/**
 * Routes for users
 */
app.use("/user", require("./routes/user"));
/**
 * Routes for groups
 */
app.use("/group", require("./routes/group"));
/**
 * Routes for events
 */
app.use("/event", require("./routes/event"));
/**
 * Routes for events
 */
app.use("/calendar", require("./routes/calendar"));
/**
 * Seeding routes
 */
//const seed = require("./db/seeders/seed_db")(app);

/**
 * Error Handler.
 */
if (process.env.NODE_ENV === "development") {
  // only use in development
  app.use(errorHandler());
} else {
  app.use((err, res) => {
    res.status(500).send("Server Error", err);
  });
}

app.get("/", (res) => { res.send("Wubba Lubba Dub Dub!"); });

/**
 * Socket io connector
 */
io.on("connection", (socket) => {

  socket.on("join", (userNickname) => {

    socket.broadcast.emit("userjoinedthechat", `${userNickname} : has joined the chat `);
  });

  socket.on("messagedetection", (senderUserId, receiverUserId, messageContent) => {
    const message = {
      message: messageContent,
      senderId: senderUserId,
      receiverId: receiverUserId
    };
    io.emit("message", message);
  });

  socket.on("chatroomDestroy", (userId) => {
    // use username instead
    socket.broadcast.emit("userdisconnect", `${userId} user has left`);
  });
  socket.on("disconnect", () => {
    // ??
  });
});

server.listen(3000);

/**
 * Start Express server.
 */
app.listen(app.get("port"), () => {
});

module.exports = app;
