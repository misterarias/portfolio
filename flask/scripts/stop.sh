#!/bin/sh

APP=flask-app
docker stop $(docker ps -q  --filter=ancestor=${APP})
