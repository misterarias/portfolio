#!/bin/sh

set -euxo

TOPDIR=$(cd "$(dirname $0)/../.." && pwd)

export LOCAL_APP=flask-app-release
export DOCKER_TAG=1.0.0
export REGISTRY=103206785366.dkr.ecr.eu-west-1.amazonaws.com/ariasfreire
export IMAGE_NAME="${REGISTRY}/portfolio:${DOCKER_TAG}"

$(dirname "$0")/build.sh

# Login to private repository
$(aws --profile ariasfreire ecr get-login --no-include-email --region eu-west-1)

docker tag "${LOCAL_APP}" "${IMAGE_NAME}"
docker push "${IMAGE_NAME}"
