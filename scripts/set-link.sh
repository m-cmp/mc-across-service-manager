#!/bin/bash

ln -s ${PROJECT_ROOT}/service-orchestrator/src/main/resources/static/catalog/templates ${BASE_DIR}/airflow/catalog
ln -s ${PROJECT_ROOT}/service-orchestrator/src/main/resources/static/dags ${BASE_DIR}/airflow
ln -s ${PROJECT_ROOT}/ansible/* ${BASE_DIR}/ansible
ln -s ${PROJECT_ROOT}/terraform/source ${BASE_DIR}/terraform
