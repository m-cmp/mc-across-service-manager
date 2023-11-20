from datetime import datetime
from airflow import DAG
from airflow.providers.http.operators.http import SimpleHttpOperator
from airflow.operators.python import PythonOperator
from airflow.hooks.base_hook import BaseHook
from airflow.decorators import task
from airflow.utils.task_group import TaskGroup
import json
import logging

default_args = {
    'start_date':datetime(2023, 1, 1)
}

# 진행 상황 db 업데이트 요청
def task_status(task_name, **context):
    import requests
    logger = logging.getLogger(__name__)

    url = BaseHook.get_connection('orchestrator').get_uri()
    headers = {'Content-Type': 'application/json'}

    dag_run = context.get('dag_run')
    ti = context.get('ti')
    service_id = dag_run.conf.get('service_id')

    # 직전에 수행된 task들 조회
    upstream_tasks = ti.task.get_direct_relatives(True)

    # 조회된 task 리스트 set으로 변환
    upstream_task_ids = {t.task_id for t in upstream_tasks}

    # 현재 실행 중인 dag_run에서 실패한 모든 task들 조회
    failed_task_instances = dag_run.get_task_instances(state='failed')
    upstream_failed_task_instances = dag_run.get_task_instances(state='upstream_failed')
    failed_task_instances.extend(upstream_failed_task_instances)

    # 실패한 task들 중에서 직전에 수행된 task들 있는지 체크
    failed_upstream_task_ids = [failed_task.task_id for failed_task in failed_task_instances if failed_task.task_id in upstream_task_ids]

    status = "running"
    count = len(failed_upstream_task_ids)
    if count > 0:
        status = "_FAIL"
    else:
        status = "_SUCCESS"

    logger.info("[migration workflow] task_name : %s, status : %s]", task_name, status)
    logger.info("[migration workflow] orchestrator url : %s]", url + '/api/v1/orchestrator/info/services/' + str(service_id) + '/state')
    # 워크플로우 상태 업데이트 요청
    response = requests.put(
        url + '/api/v1/orchestrator/info/services/' + str(service_id) + '/state',
        headers = headers,
        data = json.dumps({
            "state" : task_name + status
        })
    )

    if status == "_FAIL":
        from airflow.exceptions import AirflowFailException

        # vm 생성 이후 실패할 경우 새로 생성한 vm 삭제
        if task_name in ["DATA_LOAD", "APP_DEPLOY", "MIGRATION"]:
            multi_cloud_url = BaseHook.get_connection('cloud_if').get_uri()
            logger.info("[migration workflow fail] multi cloud url : %s]", multi_cloud_url + "/api/v1/multi-cloud/services")

            response = requests.delete(
                multi_cloud_url + '/api/v1/multi-cloud/services',
                headers = headers,
                data = ti.xcom_pull(task_ids='ansible_data_migration_group.get_migration_datas')
            )
            logger.info("[migration workflow fail] response : %s]", response.text)

        # 워크플로우가 더이상 진행되지 않도록 에러 발생시킴
        raise AirflowFailException

with DAG(
    dag_id = 'migrate_service_api_call_template_dag', 
    tags = ["api_template"], 
    default_args = default_args, 
    schedule_interval = None, 
    is_paused_upon_creation=False,
    catchup = False) as dag:

    # 앤서블 데이터 백업 및 비활성화 요청 그룹
    with TaskGroup(group_id='ansible_data_migration_group') as ansible_data_migration:

        @task
        def get_migration_datas(**context):

            dag_run = context['dag_run']
            task_instance = context['task_instance']
            original_id = dag_run.conf.get('original_service_instance_id')
            # 기존의 서비스 데이터는 유지하기 위해 깊은 복사한 서비스 데이터
            service = dag_run.conf.get('service').copy()
            # 앤서블에서 새로운 vm 인스턴스에 백업 데이터 다운로드하기 위한 새 vm 인스턴스 아이디 
            task_instance.xcom_push(key='new_service', value=json.dumps(service))

            service['service_instance_id'] = original_id
            
            # 기존의 서비스 인스턴스만 뽑아서 앤서블에 보내줌
            task_instance.xcom_push(key='original_service_instance_id', value=json.dumps({'service_instance_id' : original_id}))

            return json.dumps(service)
        
        # 앤서블 데이터 백업 요청
        ansible_request = SimpleHttpOperator(
            task_id = "request_ansible_migrate_data",
            http_conn_id = "ansible",
            headers = {
                "Content-Type" : "application/json",
            },
            log_response = True,
            method = "PUT",
            endpoint = "/api/v1/controller/migration/{{dag_run.conf.get('service_id')}}",
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text),
            do_xcom_push=True,
            retries=0,
            data="{{ti.xcom_pull(task_ids='ansible_data_migration_group.get_migration_datas', key='original_service_instance_id')}}"
        )

        task_alert_task = PythonOperator(
            task_id = "update_ansible_task",
            trigger_rule="one_done",
            python_callable = task_status,
            provide_context = True,
            op_kwargs = {"task_name" : "DATA_BACKUP"}
        )

        get_migration_datas() >> ansible_request >> task_alert_task

    # 테라폼 vm 생성 요청 그룹
    with TaskGroup(group_id='terraform_task_group') as terraform:

        @task
        def get_tf_paths(**context):

            dag_run = context['dag_run']
            service = dag_run.conf.get('service')
            service.get('tf_path').pop('vgw', None)
            service.get('tf_path').pop('vpn', None)
            service.get('tf_path').pop('gslb', None)

            return json.dumps(service)
        
        # 테라폼 vm 생성 요청
        terraform_request = SimpleHttpOperator(
            task_id = "request_terraform_instantiation",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            trigger_rule="all_success",
            endpoint = "/api/v1/multi-cloud/services",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            do_xcom_push=True,
            retries=0,
            data=get_tf_paths()
        )

        task_alert_task = PythonOperator(
            task_id = "update_terraform_task",
            trigger_rule = "one_done",
            python_callable = task_status,
            provide_context = True,
            op_kwargs = {"task_name" : "VM_INIT"}
        )

        terraform_request >> task_alert_task

    # 테라폼 vm 헬스체크 요청 그룹
    with TaskGroup(group_id='terraform_health_check') as vm_health_check:

        @task
        def get_service_instances(**context):

            dag_run = context['dag_run']
            service = dag_run.conf.get('service')
            vm = context['task_instance'].xcom_pull("terraform_task_group.request_terraform_instantiation")

            base_url = "/api/v1/multi-cloud"

            endpoint = base_url + "/services?service_instance_id=" + service.get('service_instance_id') + "&csp=" + service.get('csp') + "&vm_instance_id=" + vm.get('vm_instance_id')

            return endpoint

        # 테라폼 vm 헬스체크 요청
        terraform_request = SimpleHttpOperator(
            task_id = "request_terraform_health_check",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "GET",
            endpoint=get_service_instances(),
            retries=3,
            retry_delay=10,
            log_response = True,
            response_check=lambda response: True if json.loads(response.text).get("data").get('vm_instance_status').upper() == 'RUNNING' else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            do_xcom_push = True
        )

    # 앤서블 어플리케이션 배포 요청 그룹
    with TaskGroup(group_id='ansible_configuration_group') as ansible_configuration:

        @task
        def get_applications(**context):

            dag_run = context['dag_run']
            service = dag_run.conf.get('service').copy()
            instance = context['task_instance'].xcom_pull("terraform_task_group.request_terraform_instantiation")
            
            service.pop('vm_public_ip', None)
            service['vm_instance_public_ip'] = instance.get('public_ip')
            service['application_activate_yn'] = "Y"

            return json.dumps(service)
        
        # 앤서블 어플리케이션 배포 요청
        ansible_request = SimpleHttpOperator(
            task_id = "request_ansible_installation",
            http_conn_id = "ansible",
            headers = {
                "Content-Type" : "application/json",
            },
            log_response = True,
            method = "POST",
            endpoint = "/api/v1/controller/services/{{dag_run.conf.get('service_id')}}/applications",
            do_xcom_push=True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text),
            retries=0,
            data=get_applications()
        )

        task_alert_task = PythonOperator(
            task_id = "update_ansible_task",
            trigger_rule = "one_done",
            python_callable = task_status,
            provide_context = True,
            op_kwargs = {"task_name" : "APP_DEPLOY"}
        )

        ansible_request >> task_alert_task

    # 앤서블 어플리케이션 활성화 요청 그룹
    with TaskGroup(group_id='ansible_activation_group') as ansible_activation:
        
        @task
        def get_applications(**context):

            dag_run = context['dag_run']
            service = dag_run.conf.get('service').copy()
            instance = context['task_instance'].xcom_pull("terraform_task_group.request_terraform_instantiation")
            
            service.pop('vm_public_ip', None)
            service['vm_instance_public_ip'] = instance.get('public_ip')
            service['application_id'] = context['task_instance'].xcom_pull("ansible_configuration_group.request_ansible_installation").get('application_id')
            service['application_activate_yn'] = "Y"

            return json.dumps(service)

        # 앤서블 어플리케이션 활성화 요청
        ansible_activation_request = SimpleHttpOperator(
            task_id = "ansible_activation_request",
            http_conn_id = "ansible",
            headers = {
                "Content-Type" : "application/json",
            },
            log_response = True,
            method = "PUT",
            endpoint = "/api/v1/controller/services/{{dag_run.conf.get('service_id')}}/applications/activation",
            do_xcom_push = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text),
            retries=0,
            data=get_applications()
        )

        task_alert_task = PythonOperator(
            task_id = "update_ansible_migration",
            trigger_rule = "one_done",
            python_callable = task_status,
            provide_context = True,
            op_kwargs = {"task_name" : "DATA_LOAD"}
        )

        ansible_activation_request >> task_alert_task

    # 앤서블 백업 데이터 다운로드 요청 그룹
    with TaskGroup(group_id='ansible_download_group') as ansible_download:

        @task
        def get_xcom_datas(**context):

            data = context['task_instance'].xcom_pull("ansible_data_migration_group.request_ansible_migrate_data")
            instance = context['task_instance'].xcom_pull("terraform_task_group.request_terraform_instantiation")

            data['service_instance_id'] = instance.get('service_instance_id')

            return json.dumps(data)

        # 앤서블 백업 데이터 다운로드 요청
        ansible_download_request = SimpleHttpOperator(
            task_id = "request_ansible_download_data",
            http_conn_id = "ansible",
            headers = {
                "Content-Type" : "application/json",
            },
            log_response = True,
            method = "POST",
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            endpoint = "/api/v1/controller/migration/{{dag_run.conf.get('service_id')}}",
            do_xcom_push=True,
            retries=0,
            data=get_xcom_datas()
        )

        task_alert_task = PythonOperator(
            task_id = "update_ansible_download",
            trigger_rule = "one_done",
            python_callable = task_status,
            provide_context = True,
            op_kwargs = {"task_name" : "MIGRATION"}
        )

        ansible_download_request >> task_alert_task

    # 서비스에 속한 기존 리소스들 삭제 요청
    service_termination_request = SimpleHttpOperator(
        task_id = "orginal_service_termination_request",
        http_conn_id = "orchestrator",
        headers = {
            "Content-Type" : "application/json",
        },
        log_response = True,
        method = "DELETE",
        endpoint = "/api/v1/orchestrator/services/{{dag_run.conf.get('service_id')}}",
        do_xcom_push = True,
        retries=0,
        data = "{{ti.xcom_pull(task_ids='ansible_data_migration_group.get_migration_datas')}}"
    )

    ansible_data_migration >> terraform >> vm_health_check >> ansible_configuration >> ansible_activation >> ansible_download >> service_termination_request