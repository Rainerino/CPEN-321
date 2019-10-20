# Backend

<div align=center>

![travis ci status](https://travis-ci.org/Rainerino/CPEN-321.svg?branch=backend)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8c21d927da1d4ac0b30a041415aef552)](https://www.codacy.com/manual/Rainerino/CPEN-321?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Rainerino/CPEN-321&amp;utm_campaign=Badge_Grade)

</div>

## Set up

### installation
```bash
npm install 
```

### Express

https://expressjs.com/en/starter/basic-routing.html



### Socketio

https://socket.io/get-started/chat



## Database development 

http://www.mysqltutorial.org/mysql-data-types.aspx

https://www.sitepoint.com/using-node-mysql-javascript-client/

https://dev.to/achowba/build-a-simple-app-using-node-js-and-mysql-19me

https://medium.com/@prajramesh93/getting-started-with-node-express-and-mysql-using-sequelize-ed1225afc3e0

welcome to window: > C:\Users\yanyi\AppData\Roaming\npm\node_modules\sequelize-cli\lib\sequelize

```bash
sequelize model:create --name User --attributes username:string

```



## Testing

https://blog.logrocket.com/a-quick-and-complete-guide-to-mocha-testing-d0e0ea09f09d/

https://github.com/docker/labs/blob/master/developer-tools/nodejs/porting/2_application_image.md




## Docker
https://www.digitalocean.com/community/tutorials/how-to-build-a-node-js-application-with-docker

https://dev.mysql.com/doc/mysql-installation-excerpt/5.5/en/docker-mysql-getting-started.html#docker-download-image

```bash
docker run --rm -d --name=studybuddy -p 8080:8000/tcp rainerino/studybuddy        
docker run --rm --name=my-mysql --env MYSQL_ROOT_PASSWORD=password --detach --publish 3306:3306 mysql:5.7.24
docker-compose run 
```

https://medium.com/@chrischuck35/how-to-create-a-mysql-instance-with-docker-compose-1598f3cc1bee

## Code structure

https://codeburst.io/fractal-a-nodejs-app-structure-for-infinite-scale-d74dda57ee11



## User Login

 https://medium.com/createdd-notes/starting-with-authentication-a-tutorial-with-node-js-and-mongodb-25d524ca0359 