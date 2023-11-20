from datetime import datetime
from airflow import DAG
from airflow.providers.http.operators.http import SimpleHttpOperator
from airflow.operators.python import PythonOperator
from airflow.utils.task_group import TaskGroup
from airflow.decorators import task
from airflow.hooks.base_hook import BaseHook
import json
import logging

default_args = {
    'start_date':datetime(2023, 1, 1)
}

# 현재 진행 상황 db 업데이트 요청
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

    logger.info("[service workflow] task_name : %s, status : %s]", task_name, status)
    logger.info("[service workflow] orchestrator url : %s]", url + '/api/v1/orchestrator/info/services/' + service_id + '/state')
    response = requests.put(
        url + '/api/v1/orchestrator/info/services/' + service_id + '/state',
        headers = headers,
        data = json.dumps({
            "state" : task_name + status
        })
    )

    if count > 0:
        from airflow.exceptions import AirflowFailException
        multi_cloud_url = BaseHook.get_connection('cloud_if').get_uri()
        logger.info("[service workflow fail] multi cloud url : %s]", multi_cloud_url + "/api/v1/multi-cloud/services")

        # vm 생성 중 에러가 날 경우 DB에서 정보 조회 후 삭제 요청
        if task_name == 'VM_INIT':
            from airflow.hooks.mysql_hook import MySqlHook

            service_list = dag_run.conf.get('service_list')

            id_list = []
            for service in service_list:
                id_list.append(service.get("service_instance_id"))
                
            sql = "SELECT service_instance_id, tfstate_file_path FROM inventory.tb_vpc WHERE service_instance_id IN %s"
            mysql_hook = MySqlHook(mysql_conn_id = 'inventory', schema = 'inventory')
            records = mysql_hook.get_records(sql, (tuple(id_list),))
            logger.info("[service workflow fail] vpc info : %s, type : %s, length : %s]", records, type(records), len(records))

            if len(records) != 0 or records:
                logger.info("[service workflow fail] request multi-cloud")
                response = requests.delete(
                    multi_cloud_url + "/api/v1/multi-cloud/services",
                    headers = headers,
                    data=json.dumps(records)
                )
        # vm 생성 이후 에러가 날 경우 바로 서비스 아이디로 오케스트레이터에 삭제 요청
        else :
            logger.info("[service workflow fail] request orchestrator")
            response = requests.delete(
                url + "/api/v1/orchestrator/services?service_id=" + service_id,
                headers = headers
            )
        logger.info("[service workflow fail] response : %s]", response.text)

        raise AirflowFailException

with DAG(
    dag_id = 'create_api_call_template_dag', 
    tags = ["api_template"], 
    default_args = default_args, 
    schedule_interval = None, 
    is_paused_upon_creation=False,
    catchup = False) as dag:

    # 테라폼 vm 생성 요청 그룹
    with TaskGroup(group_id='terraform_task_group') as terraform:

        @task
        def get_tf_paths(**context):

            dag_run = context['dag_run']
            service_list = dag_run.conf.get('service_list')

            dynamic_data = []

            for service in service_list:
                service['tf_path']=dict(vpc=service['tf_path']['vpc'], vm=service['tf_path']['vm'])
                dynamic_data.append(json.dumps(service))

            return dynamic_data

        # vm 생성 요청
        terraform_request = SimpleHttpOperator.partial(
            task_id = "request_terraform_instantiation",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/services",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            do_xcom_push=True,
            retries=0
        ).expand(data=get_tf_paths())

        task_alert_task = PythonOperator(
            task_id = "update_terraform_task",
            trigger_rule = "all_done",
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
            service_list = dag_run.conf.get('service_list')
            vm_list = context['task_instance'].xcom_pull("terraform_task_group.request_terraform_instantiation")

            dynamic_endpoint = []
            base_url = "/api/v1/multi-cloud"
            endpoint = ""

            for service in service_list:
                for vm in vm_list:
                    print(vm)
                    if vm.get('service_instance_id') == service.get('service_instance_id'):
                        endpoint = base_url + "/services?service_instance_id=" + service.get('service_instance_id') + "&csp=" + service.get('csp') + "&vm_instance_id=" + vm.get('vm_instance_id')

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
            retries=3,
            retry_delay=10,
            log_response = True,
            response_check=lambda response: True if json.loads(response.text).get("data").get('vm_instance_status').upper() == 'RUNNING' else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            do_xcom_push = True
        ).expand(endpoint=get_service_instances())   

    # 앤서블 어플리케이션 배포 요청 그룹
    with TaskGroup(group_id='ansible_task_group') as ansible:

        @task
        def get_applications(**context):

            dag_run = context['dag_run']
            service_list = dag_run.conf.get('service_list')
            ip_list = context['task_instance'].xcom_pull("terraform_task_group.request_terraform_instantiation")

            dynamic_data = []

            for service in service_list:
                for ip in ip_list:
                    if ip.get('service_instance_id') == service.get('service_instance_id'):
                        service['vm_instance_public_ip'] = ip.get('public_ip')
                        dynamic_data.append(json.dumps(service))

            return dynamic_data
        
        # 앤서블 어플리케이션 배포 요청
        ansible_request = SimpleHttpOperator.partial(
            task_id = "request_ansible_installation",
            http_conn_id = "ansible",
            headers = {
                "Content-Type" : "application/json",
            },
            log_response = True,
            method = "POST",
            endpoint = "/api/v1/controller/services/{{dag_run.conf.get('service_id')}}/applications",
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text),
            do_xcom_push=True,
            retries=0
        ).expand(data=get_applications())

        task_alert_task = PythonOperator(
            task_id = "update_ansible_task",
            trigger_rule = "all_done",
            python_callable = task_status,
            provide_context = True,
            op_kwargs = {"task_name" : "APP_DEPLOY"}
        )

        ansible_request >> task_alert_task

    # orchestrator로 헬스체크 트리거 요청
    health_check = SimpleHttpOperator(
        task_id = "request_health_check",
        http_conn_id = "orchestrator",
        headers = {
            "Content-Type" : "application/json",
        },
        log_response = True,
        method = "GET",
        endpoint = "/api/v1/orchestrator/services/{{dag_run.conf.get('service_id')}}",
    )

    terraform >> vm_health_check >> ansible >> health_check