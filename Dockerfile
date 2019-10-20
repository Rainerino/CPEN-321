# Use node 10.16.3 locally
FROM node:10.16.3-stretch-slim

RUN mkdir -p /home/node/app/node_modules && chown -R node:node /home/node/app

WORKDIR /home/node/app
# Copy source code
COPY package*.json ./

# we want to run the code in user mode. 
USER node
# Install dependencies
RUN npm install

# change the permission of the application code
COPY --chown=node:node . .

# Expose API port to the outside
EXPOSE 8000

# Launch application

ENTRYPOINT ["npm", "run", "test-docker"]



