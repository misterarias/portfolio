#!/bin/sh

TOPDIR=$(cd "$(dirname $0)/../.." && pwd)

LOCAL_APP=${LOCAL_APP?:No app name defined}

echo "Building '${LOCAL_APP}' for release"
docker build -t "${LOCAL_APP}" -f "${TOPDIR}/image/Dockerfile.release" "${TOPDIR}"
