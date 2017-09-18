#!/bin/sh
APP=flask-app
TOPDIR=$(cd "$(dirname $0)/.." && pwd)

echo "Building '${APP}'"
docker build -t "${APP}" "${TOPDIR}/image/"
