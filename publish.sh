#!/bin/bash

PACT_BROKER_URL="http://localhost:9292/"
GIT_VERSION=$(eval "git rev-parse HEAD")

docker run --rm \
   -w ${PWD} \
   -v ${PWD}:${PWD} \
    pactfoundation/pact-cli:latest \
    publish \
    ${PWD}/target/pacts \
    --broker-base-url $PACT_BROKER_URL \
    --consumer-app-version $GIT_VERSION \
    --tag-with-git-branch
