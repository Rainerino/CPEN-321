FROM node:8-slim

EXPOSE 8080

WORKDIR /starter
ENV NODE_ENV production

COPY package.json /starter/package.json

RUN npm install --production

COPY .env.example /starter/.env.example
COPY . /starter

CMD ["npm","run", "start"]
