FROM node:slim

WORKDIR /opt/app

RUN npm i express pg

COPY src/. .
CMD node server.js