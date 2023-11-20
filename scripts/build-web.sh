#!/bin/bash

pids=$(ps -ef | grep vite | awk '{print $2}')
echo "Killing Vite processes: $pids"

for pid in "${pids[@]}"; do
    kill -9 "$pid"
done


cd ${PROJECT_ROOT}/web

npm i
echo "install web"

npm run build
echo "build web"

nohup npm run preview 2>&1 &
echo "preview web"

cd ${PROJECT_ROOT}/scripts
