#!/bin/bash

#pids = $(find /root/mcmp/pid -type f -name '*.pid')
pids=$(ps -ef | grep java | grep "webserver\|SAOrchestrator\|multi-cloud\|confController" | awk '{print $2}')
echo will be killed [${pids}]

for pid in ${pids}; do
    echo "kill -9 ${pid}"
    kill -9 ${pid}
done


jars=$(ls ${BUILD_DIR} | grep ".jar")
echo [${jars}]

cd $BUILD_DIR

for jar in ${jars}; do
    #echo "nohup java -Dspring.profiles.active=dev -jar ${jar} > /dev/null &"
    echo "nohup java -Dspring.profiles.active=dev -jar ${jar} > 2>&1 &"
    #nohup java -Dspring.profiles.active=dev -jar ${jar} > /dev/null &
    nohup java -jar -Dspring.profiles.active=dev ${jar} 2>&1 &
done

ps -ef | grep java
