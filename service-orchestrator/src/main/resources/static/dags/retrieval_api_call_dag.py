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
    dag_id = 'retrieval_api_call_template_dag', 
    tags = ["api_template"], 
    default_args = default_args, 
    schedule_interval = None, 
    is_paused_upon_creation=False,
    max_active_runs=5,
    catchup = False) as dag:
    
    # 테라폼 vm 헬스체크 요청 그룹
    with TaskGroup(group_id='terraform_health_check') as vm_health_check:

        @task
        def get_service_instances(**context):

            dag_run = context['dag_run']
            service_list = dag_run.conf.get('service_list')

            dynamic_endpoint = []
            base_url = "/api/v1/multi-cloud"
            endpoint = ""

            for service in service_list:
                endpoint = base_url + "/services?service_instance_id=" + service.get('service_instance_id') + "&csp=" + service.get('csp') + "&vm_instance_id=" + service.get('vm_instance_id')

                dynamic_endpoint.append(endpoint)

            return dynamic_endpoint

        # 테라폼 vm 헬스체크 요청
        terraform_request = SimpleHttpOperator.partial(
            task_id = "request_terraform_health_check",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "GET",
            retries=1,
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            do_xcom_push = True
        ).expand(endpoint=get_service_instances())    

    # vm 헬스체크 데이터 db 업데이트 요청 그룹
    with TaskGroup(group_id='vm_health_check') as vm_health_update:

        @task
        def get_vm_status(**context):
            vm_list = context['task_instance'].xcom_pull(task_ids='terraform_health_check.request_terraform_health_check')
            
            dict_vm_list = []

            for vm in vm_list if vm_list != None else None:
                dict_vm_list.append(vm)
            
            return json.dumps(dict_vm_list)

        # vm 헬스체크 데이터 db 업데이트 요청
        update_vm_service_instance = SimpleHttpOperator(
            task_id = "request_vm_update_service_instance",
            http_conn_id = "orchestrator",
            headers = {
                "Content-Type" : "application/json",
            },
            log_response = True,
            method = "PUT",
            endpoint = "/api/v1/orchestrator/info/status",
            data = get_vm_status()
        )

    # app 헬스체크 요청 그룹
    with TaskGroup(group_id='ansible_health_check') as app_health_check:

        @task
        def get_applications_for_check(**context):

            dag_run = context['dag_run']
            service_list = dag_run.conf.get('service_list')

            dynamic_endpoint = []
            base_url = "/api/v1/controller"
            endpoint = ''
            
            for service in service_list:
                application_id = service.get('application_id') if service.get('application_id') else None
                # application_activate_yn = service.get('application_activate_yn') if service.get('application_activate_yn') else None
                if application_id:
                    if service.get('service_id') != None:
                        endpoint = base_url + "/services/" + service.get('service_id') + "/applications?service_instance_id=" + service.get('service_instance_id') + "&application_id=" + application_id
                    else:
                        endpoint = base_url + "/across-services/" + service.get('across_service_id') + "/applications?service_instance_id=" + service.get('service_instance_id') + "&application_id=" + application_id
                    
                    dynamic_endpoint.append(endpoint)

            return dynamic_endpoint
        
        # app 헬스체크 요청
        ansible_request = SimpleHttpOperator.partial(
            task_id = "request_ansible_health_check",
            http_conn_id = "ansible",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "GET",
            log_response = True,
            retries=0,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text),
            do_xcom_push = True
        ).expand(endpoint=get_applications_for_check())

    # app 헬스체크 요청 데이터 db 업데이트 요청 그룹
    with TaskGroup(group_id='app_health_check') as app_health_update:

        @task
        def get_app_status(**context):
            app_list = context['task_instance'].xcom_pull(task_ids='ansible_health_check.request_ansible_health_check')
            
            dict_app_list = []

            for app in app_list:
                dict_app_list.append(app)
            
            return json.dumps(dict_app_list)

        # app 헬스체크 요청 데이터 db 업데이트 요청
        update_app_service_instance = SimpleHttpOperator(
            task_id = "request_app_update_service_instance",
            http_conn_id = "orchestrator",
            headers = {
                "Content-Type" : "application/json",
            },
            log_response = True,
            retries=0,
            method = "PUT",
            endpoint = "/api/v1/orchestrator/info/status",
            data = get_app_status()
        )

    vm_health_check >> vm_health_update >> app_health_check >> app_health_update