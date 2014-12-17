#!/bin/bash

mvn clean install assembly:single
cp evaluation.properties target
cp start-local.sh target
cp start-server.sh target
cp start-worker.sh target

