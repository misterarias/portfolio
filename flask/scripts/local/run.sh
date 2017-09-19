#!/bin/sh
APP=flask-app
EXPOSED_PORT=8080
TOPDIR=$(cd "$(dirname $0)/../.." && pwd)

echo "Launching '${APP}' in htpp://0.0.0.0:${EXPOSED_PORT}"
docker run  --rm -v ${TOPDIR}/server:/opt/flask -p ${EXPOSED_PORT}:80 -ti ${APP}
