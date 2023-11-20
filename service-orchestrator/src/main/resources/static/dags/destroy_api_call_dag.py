from datetime import datetime
from airflow import DAG
from airflow.providers.http.operators.http import SimpleHttpOperator
from airflow.utils.task_group import TaskGroup
from airflow.decorators import task
import json

default_args = {
    'start_date':datetime(2023, 1, 1)
}

with DAG(
    dag_id = 'destroy_api_call_template_dag', 
    tags = ["api_template"], 
    default_args = default_args, 
    schedule_interval = None, 
    is_paused_upon_creation=False,
    catchup = False) as dag:
    
    # 테러폼 vm 삭제 요청 그룹
    with TaskGroup(group_id='terraform_task_group') as terraform:

        @task
        def get_services(**context):

            dag_run = context['dag_run']
            service_list = dag_run.conf.get('service_list')

            dynamic_data = []

            for service in service_list:
                data = {
                    "service_instance_id" : service.get("service_instance_id"),
                    "tfstate_file_path" : service.get("tfstate_file_path"),
                }
                dynamic_data.append(json.dumps(data))

            return dynamic_data

        # 테러폼 vm 삭제 요청
        SimpleHttpOperator.partial(
            task_id = "request_terraform_destroy",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            method = "DELETE",
            retries=0,
            endpoint = "/api/v1/multi-cloud/services"
        ).expand(data=get_services())

    @task
    def get_json_list(**context):

        dag_run = context['dag_run']
        service_list = dag_run.conf.get('service_list')
        data = ''
        
        for service in service_list:
            if service_list.index(service) == len(service_list) - 1:
                data += service.get('service_id')
            else:
                data += service.get('service_id') + ","

        return data
    
    # orchestrator 서비스 데이터 삭제 요청
    delete_service_instance = SimpleHttpOperator(
        task_id = "request_delete_service_instance",
        http_conn_id = "orchestrator",
        headers = {
            "Content-Type" : "application/json",
        },
        log_response = True,
        method = "DELETE",
        retries=0,
        endpoint = "/api/v1/orchestrator/info/services?service_id={{ti.xcom_pull(task_ids='get_json_list')}}&is_migrating={{dag_run.conf.get('is_migrating') if dag_run.conf.get('is_migrating') else 'N'}}"
    )

    terraform >> get_json_list() >> delete_service_instance