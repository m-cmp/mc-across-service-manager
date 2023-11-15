from datetime import datetime
from airflow import DAG
from airflow.providers.http.operators.http import SimpleHttpOperator
from airflow.decorators import task
import json

default_args = {
    'start_date':datetime(2023, 1, 1)
}

with DAG(
    dag_id = 'deploy_agent_api_call_template_dag', 
    tags = ["api_template"], 
    default_args = default_args, 
    schedule_interval = None, 
    is_paused_upon_creation=False,
    catchup = False) as dag:

    @task
    def get_data(**context):

        dag_run = context['dag_run']
        service_list = dag_run.conf.get('service_List')

        for service in service_list:
            service['vm_instance_public_ip'] = service['vm_public_ip']

        return json.dumps(service_list)

    # 앤서블로 agent 배포 요청
    ansible_request = SimpleHttpOperator(
        task_id = "request_ansible_playbook_run",
        http_conn_id = "ansible",
        headers = {
            "Content-Type" : "application/json",
        },
        method = "POST",
        endpoint = "/api/v1/controller/services/collectors",
        data = get_data(),
        log_response = True,
        response_check=lambda response: True if response.status_code in [200, 201] else False,
        retries=0,
    )
       
    ansible_request
