
const db = require('../../models')
const User = db.User


module.exports = {
    create(req, res) {
        return User.create(
            {
                firstName: req.body.firstName,
                lastName: req.body.lastName,
                email: req.body.email,
                password: req.body.password,
                facebookAPIToken: req.body.facebookAPIToken
            }
        ).then(user => res.status(201).json(user))
        .catch(err => res.status(400).send(err))
    },
    get(req, res) {
        return User.findAll({
            raw: true
        })
          .then(function (users) {
            res.json(users);
          });
    }

    // get(req, res){
    //     return User()
    // }
}