FROM node:8-slim

EXPOSE 8080

WORKDIR /starter
ENV NODE_ENV development

COPY package.json /starter/package.json

RUN npm install

COPY .env.example /starter/.env.example
COPY . /starter

CMD ["npm","run", "start"]
