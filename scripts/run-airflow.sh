#!/bin/bash

airflow_path=${BASE_DIR}/airflow

cd $airflow_path

# Run Airflow
sudo docker compose up -d

# Add Auth
sudo chmod a+w config/ dags/ logs/ plugins/

# Airflow Configuration
sudo docker exec airflow-airflow-scheduler-1 airflow connections add 'orchestrator' \
    --conn-json '{
        "conn_type": "http",
        "host": "'${IP}'",
        "port": "8090"
    }'
sudo docker exec airflow-airflow-scheduler-1 airflow connections add 'cloud_if' \
    --conn-json '{
        "conn_type": "http",
        "host": "'${IP}'",
        "port": "8091"
    }'
sudo docker exec airflow-airflow-scheduler-1 airflow connections add 'ansible' \
    --conn-json '{
        "conn_type": "http",
        "host": "'${IP}'",
        "port": "8092"
    }'

sudo docker exec airflow-airflow-scheduler-1 airflow connections add 'inventory' \
    --conn-json '{
        "conn_type": "mysql",
        "host": "'${IP}'",
        "port": "3306",
        "schema": "inventory",
        "login": "'${DB_USERNAME}'",
        "password": "'${DB_PASSWORD}'"
    }'

