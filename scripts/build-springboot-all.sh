#!/bin/bash

sudo chmod +x ${PROJECT_ROOT}/*/gradlew

# webserver build
cd ${PROJECT_ROOT}/webserver
gradle wrapper
./gradlew build
echo "build webserver"

# sa-orchestrator build
cd ${PROJECT_ROOT}/service-orchestrator
gradle wrapper
./gradlew build
echo "build service-orchestrator"

# multi-cloud build
cd ${PROJECT_ROOT}/multi-cloud
gradle wrapper
./gradlew build
echo "build multi-cloud"

# conf-controller build
cd ${PROJECT_ROOT}/configuration-controller
gradle wrapper
./gradlew build
echo "build configuration-controller"

# move to build directory
find ${PROJECT_ROOT}/*/build/ -type f -name "*.jar" | xargs cp -t ${BUILD_DIR}/

echo "finished to move jar files"
