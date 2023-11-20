#!/bin/bash

username=$(whoami)
groupname=$(groups | awk '{print $1}')

# Install Airflow
cd ${BASE_DIR}
if [ ! -d airflow ]; then
  mkdir -p airflow/catalog
  echo "Create airflow and catalog dir"
fi

cd ${BASE_DIR}/airflow
curl -LfO 'https://airflow.apache.org/docs/apache-airflow/2.7.2/docker-compose.yaml'
#sudo chown -R $username:$groupname *
#sudo chmod a+w config/ dags/ logs/ plugins/
