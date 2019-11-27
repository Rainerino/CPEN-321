FROM node:12.13.1-alpine

EXPOSE 8080

EXPOSE 3000

WORKDIR /starter
ENV NODE_ENV production

COPY package.json /starter/package.json

RUN npm install --production

COPY .env.example /starter/.env.example
COPY . /starter

CMD ["npm","run", "start"]
