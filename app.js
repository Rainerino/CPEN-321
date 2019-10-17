//Load express module with `require` directive
var express = require('express')
var mysql = require('mysql');

var app = express()

var con = mysql.createConnection({
  host: "ss",
  user: "admin",
  password: ""
});

con.connect(function(err) {
  if (err) throw err;
  console.log("Connected!");
});




//Define request response in root URL (/)
app.get('/', function (req, res) {
  res.send('Hello World!')
})

//Launch listening server on port 8081
app.listen(8081, function () {
  console.log('app listening on port 8081!')
})