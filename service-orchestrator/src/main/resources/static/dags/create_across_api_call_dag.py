from datetime import datetime
from airflow import DAG
from airflow.providers.http.operators.http import SimpleHttpOperator
from airflow.operators.python import PythonOperator
from airflow.operators.python import BranchPythonOperator
from airflow.utils.task_group import TaskGroup
from airflow.hooks.base_hook import BaseHook
from airflow.decorators import task
import json
import logging

default_args = {
    'start_date':datetime(2023, 1, 1)
}

# 연계서비스 생성 중 각 단계가 끝나면 연계서비스 테이블의 상태값(across_service_status) 업데이트
def task_status(task_name, **context):
    import requests
    logger = logging.getLogger(__name__)

    url = BaseHook.get_connection('orchestrator').get_uri()
    headers = {'Content-Type': 'application/json'}

    dag_run = context.get('dag_run')
    ti = context.get('ti')
    across_service_id = dag_run.conf.get('across_service_id')

    # 직전에 수행된 task들 조회
    upstream_tasks = ti.task.get_direct_relatives(True)

    # 조회된 task 리스트 set으로 변환
    upstream_task_ids = {t.task_id for t in upstream_tasks}

    # 현재 실행 중인 dag_run에서 실패한 모든 task들 조회
    failed_task_instances = dag_run.get_task_instances(state='failed')
    upstream_failed_task_instances = dag_run.get_task_instances(state='upstream_failed')
    failed_task_instances.extend(upstream_failed_task_instances)

    # 직전에 수행된 task들 중에서 실패한 task들이 있는지 체크
    failed_upstream_task_ids = [failed_task.task_id for failed_task in failed_task_instances if failed_task.task_id in upstream_task_ids]
    
    status = "running"
    count = len(failed_upstream_task_ids)
    if count > 0:
        status = "_FAIL"
    else:
        status = "_SUCCESS"

    logger.info("[across workflow] task_name : %s, status : %s]", task_name, status)
    logger.info("[across workflow] orchestrator url : %s]", url + '/api/v1/orchestrator/info/across-services/' + str(across_service_id) + '/state')
    response = requests.put(
        url + '/api/v1/orchestrator/info/across-services/' + str(across_service_id) + '/state',
        headers = headers,
        data = json.dumps({
            "state" : task_name + status
        })
    )

    if count > 0:
        from airflow.exceptions import AirflowFailException
        multi_cloud_url = BaseHook.get_connection('cloud_if').get_uri()
        logger.info("[across workflow fail] multi cloud url : %s]", multi_cloud_url + "/api/v1/multi-cloud/services")

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
            logger.info("[across workflow fail] vpc info : %s, type : %s, length : %s]", records, type(records), len(records))

            if len(records) != 0 or records:
                logger.info("[across workflow fail] request multi-cloud")
                response = requests.delete(
                    multi_cloud_url + "/api/v1/multi-cloud/across-services?vm_only_yn=N",
                    headers = headers,
                    data=json.dumps(records)
                )
        # vm 생성 이후 에러가 날 경우 바로 연계서비스 아이디로 오케스트레이터에 삭제 요청
        else :
            logger.info("[across workflow fail] request orchestrator")
            response = requests.delete(
                url + "/api/v1/orchestrator/across-services?vm_only_yn=N&across_service_id=" + across_service_id,
                headers = headers
            )
        logger.info("[across workflow fail] response : %s]", response.text)

        raise AirflowFailException

# 생성해야될 csp 로 분기점
def csp_branch(**context):

    csp_list = []
    dag_run = context['dag_run']

    for service in dag_run.conf.get('service_list'):
        csp = service.get('csp').lower()
        csp_list.append(csp + "_instantiation." + csp + "_vpc")
    return csp_list

# 생성해야될 연계서비스 타입에 따라 분기점
def across_type_branch(**context):

    csp_list = []
    dag_run = context['dag_run']
    across_type = dag_run.conf.get('across_service_type')

    # vpn 터널링일 경우
    if across_type.lower() == "vpn_tunnel":
        for service in dag_run.conf.get('service_list'):
            csp = service.get('csp').lower()
            if csp == "aws":
                return "vpn_tunneling.aws_vpn"
            else:
                csp_list.append("vpn_tunneling." + csp + "_vpn")
        return csp_list
    # gslb 일 경우
    elif across_type.lower() == "gslb":
        for service in dag_run.conf.get('service_list'):
            csp = service.get('csp').lower()
            if service.get('tf_path').get('gslb') != '' and service.get('tf_path').get('gslb') != None:
                csp_list.append("gslb." + csp + "_gslb")
        return csp_list

def type_check(**context):
    dag_run = context['dag_run']
    across_type = dag_run.conf.get('across_service_type')
    if across_type == "vpn_tunnel":
        return "vpn_tunnel_ping_check"
    else:
        return "get_status()" 
    
with DAG(
    dag_id = 'create_across_api_call_template_dag', 
    tags = ["api_template"], 
    default_args = default_args, 
    schedule_interval = None, 
    is_paused_upon_creation=False,
    catchup = False) as dag:

    # 데이터 미리 json화
    @task
    def get_data(**context):

        task_instance = context['task_instance']
        dag_run = context['dag_run']
        service_list = dag_run.conf.get('service_list').copy()
        across_type = dag_run.conf.get('across_service_type')
        print("==================연계타입===================")
        print(across_type.lower())
        for service in service_list:
            csp = service.get('csp').lower()
            tf_path = service.get('tf_path')

            # vm tf파일 넣기
            service['tf_path'] = dict(vm=tf_path.get('vm'))
            task_instance.xcom_push(key=csp +'_vm', value = json.dumps(service))
            
            # 연계 서비스 타입 체크 - vpn_tunnel일 경우
            if across_type.lower() == 'vpn_tunnel':
                # vpn tf파일만 넣기
                service['tf_path'] = dict(vpn=tf_path.get('vpn'))
                task_instance.xcom_push(key=csp+'_vpn', value = json.dumps(service))
                # csp 체크 - azure일 경우 vgw tf파일도 같이 넣기
                if csp.lower() == 'azure':
                    service['tf_path'] = dict(vpc=tf_path.get('vpc'), vgw=tf_path.get('vgw'))
                    task_instance.xcom_push(key=csp+'_vpc', value = json.dumps(service))
                # csp 체크 - aws, gcp일 경우 vpc tf파일만 넣기
                else: 
                    service['tf_path'] = dict(vpc=tf_path.get('vpc'))
                    task_instance.xcom_push(key=csp+'_vpc', value = json.dumps(service))
            # 연계 서비스 타입 체크 - gslb일 경우
            elif across_type.lower() == 'gslb':
                # vpc tf파일 넣기
                service['tf_path'] = dict(vpc=tf_path.get('vpc'))
                task_instance.xcom_push(key=csp+'_vpc', value = json.dumps(service))

                gslb=tf_path.get('gslb')
                # gslb tf파일 경로 null 체크
                if gslb != None and gslb != '':
                    # gslb tf 파일 넣기
                    service['tf_path'] = dict(gslb=tf_path.get('gslb'))
                    task_instance.xcom_push(key=csp+'_gslb', value = json.dumps(service))
    
    csp_point = BranchPythonOperator(
        task_id = "csp_branch",
        python_callable = csp_branch
    )

    # AWS vpc, vm 생성 태스크 그룹
    with TaskGroup(group_id='aws_instantiation') as AWS:

        # aws vpc 생성 요청
        aws_vpc_request = SimpleHttpOperator(
            task_id = "aws_vpc",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services/vpc",
            data = "{{ti.xcom_pull(task_ids='get_data', key='aws_vpc')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            do_xcom_push=True
        )

        # aws vm 생성 요청
        aws_vm_request = SimpleHttpOperator(
            task_id = "aws_vm",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services",
            data = "{{ti.xcom_pull(task_ids='get_data', key='aws_vm')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            do_xcom_push=True
        )

        aws_vpc_request >> aws_vm_request

    # GCP vpc, vm 생성 태스크 그룹
    with TaskGroup(group_id='gcp_instantiation') as GCP:
               
        # gcp vpc 생성 요청
        gcp_vpc_request = SimpleHttpOperator(
            task_id = "gcp_vpc",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services/vpc",
            data = "{{ti.xcom_pull(task_ids='get_data', key='gcp_vpc')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        ) 

        # gcp vm 생성 요청
        gcp_vm_request = SimpleHttpOperator(
            task_id = "gcp_vm",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services",
            data = "{{ti.xcom_pull(task_ids='get_data', key='gcp_vm')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        )

        gcp_vpc_request >> gcp_vm_request

    # AZURE vpc, vgw, vm 생성 태스크 그룹
    with TaskGroup(group_id='azure_instantiation') as AZURE:
        
        # azure vpc 생성 요청
        azure_vpc_request = SimpleHttpOperator(
            task_id = "azure_vpc",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services/vpc",
            data = "{{ti.xcom_pull(task_ids='get_data', key='azure_vpc')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        ) 

        # azure vm 생성 요청
        azure_vm_request = SimpleHttpOperator(
            task_id = "azure_vm",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services",
            data = "{{ti.xcom_pull(task_ids='get_data', key='azure_vm')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        )
        
        azure_vpc_request >> azure_vm_request   

    # 상태 업데이트
    task_alert_task = PythonOperator(
        task_id = "update_instantiation",
        trigger_rule = "all_done",
        python_callable = task_status,
        provide_context = True,
        op_kwargs = {"task_name" : "VM_INIT"}
    )
    
    # 연계 서비스 타입 체크
    across_type = BranchPythonOperator(
        task_id = "across_type_branch",
        python_callable = across_type_branch,
        trigger_rule = "all_success"
    )

    # vpn 터널링 생성 태스크 그룹
    with TaskGroup(group_id='vpn_tunneling') as vpn_tunneling:
        
        # aws vpn 생성 - aws의 경우 순서가 중요해 따로 분기점을 두었음.
        with_aws = SimpleHttpOperator(
            task_id = "aws_vpn",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services/vpn?customer_tunnel_ip={{ti.xcom_pull(task_ids='gcp_instantiation.gcp_vpc').get('tunnel_ip') if ti.xcom_pull(task_ids='gcp_instantiation.gcp_vpc') else ti.xcom_pull(task_ids='azure_instantiation.azure_vpc').get('tunnel_ip')}}&customer_vpc_cidr={{ti.xcom_pull(task_ids='gcp_instantiation.gcp_vpc').get('vpc_cidr') if ti.xcom_pull(task_ids='gcp_instantiation.gcp_vpc') else ti.xcom_pull(task_ids='azure_instantiation.azure_vpc').get('vpc_cidr')}}",
            data = '{{ti.xcom_pull(task_ids="get_data", key="aws_vpn")}}',
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        ) 

        # gcp/azure vpn 생성 - aws vpn 생성 이후 실행
        other_vpn = SimpleHttpOperator(
            task_id = "other_vpn",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services/vpn?customer_tunnel_ip={{ti.xcom_pull(task_ids='vpn_tunneling.aws_vpn').get('tunnel_ip')}}&customer_vpc_cidr={{ti.xcom_pull(task_ids='aws_instantiation.aws_vpc').get('vpc_cidr')}}",
            data = "{{ti.xcom_pull(task_ids='get_data', key='azure_vpn') if ti.xcom_pull(task_ids='get_data', key='azure_vpn') else ti.xcom_pull(task_ids='get_data', key='gcp_vpn')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        )

        # gcp vpn 생성 - gcp 와 azure만 있는 경우 순서가 상관없기 때문에 동시에 진행되는 분기
        gcp_vpn = SimpleHttpOperator(
            task_id = "gcp_vpn",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services/vpn?customer_tunnel_ip={{ti.xcom_pull(task_ids='azure_instantiation.azure_vpc').get('tunnel_ip')}}&customer_vpc_cidr={{ti.xcom_pull(task_ids='azure_instantiation.azure_vpc').get('vpc_cidr')}}",
            data = "{{ti.xcom_pull(task_ids='get_data', key='gcp_vpn')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        ) 

        # azure vpn 생성 - gcp 와 azure만 있는 경우 순서가 상관없기 때문에 동시에 진행되는 분기
        azure_vpn = SimpleHttpOperator(
            task_id = "azure_vpn",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services/vpn?customer_tunnel_ip={{ti.xcom_pull(task_ids='gcp_instantiation.gcp_vpc').get('tunnel_ip')}}&customer_vpc_cidr={{ti.xcom_pull(task_ids='gcp_instantiation.gcp_vpc').get('vpc_cidr')}}",
            data = "{{ti.xcom_pull(task_ids='get_data', key='azure_vpn')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        ) 
        
        with_aws >> other_vpn

    # 상태 업데이트
    task_alert_task_vpn = PythonOperator(
        task_id = "update_vpn_tunnel_task",
        trigger_rule = "one_done",
        python_callable = task_status,
        provide_context = True,
        op_kwargs = {"task_name" : "VPN_TUNNEL"}
    )
    
    # gslb 생성 태스크 그룹
    with TaskGroup(group_id='gslb') as gslb:
        
        # aws gslb 생성 요청
        aws_gslb = SimpleHttpOperator(
            task_id = "aws_gslb",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services/gslb?public_ip={{ti.xcom_pull(task_ids='aws_instantiation.aws_vm').get('public_ip')}}&customer_public_ip={{ti.xcom_pull(task_ids='gcp_instantiation.gcp_vm').get('public_ip') if ti.xcom_pull(task_ids='gcp_instantiation.gcp_vm') else ti.xcom_pull(task_ids='azure_instantiation.azure_vm').get('public_ip')}}",
            data = "{{ti.xcom_pull(task_ids='get_data', key='aws_gslb')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        ) 

        # azure gslb 생성 요청
        azure_gslb = SimpleHttpOperator(
            task_id = "azure_gslb",
            http_conn_id = "cloud_if",
            headers = {
                "Content-Type" : "application/json",
            },
            method = "POST",
            endpoint = "/api/v1/multi-cloud/across-services/gslb?public_ip={{ti.xcom_pull(task_ids='azure_instantiation.azure_vm').get('public_ip')}}&customer_public_ip={{ti.xcom_pull(task_ids='aws_instantiation.aws_vm').get('public_ip') if ti.xcom_pull(task_ids='aws_instantiation.aws_vm') else ti.xcom_pull(task_ids='gcp_instantiation.gcp_vm').get('public_ip')}}",
            data = "{{ti.xcom_pull(task_ids='get_data', key='azure_gslb')}}",
            log_response = True,
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            retries=0,
            do_xcom_push=True
        )

    # 상태 업데이트
    task_alert_task_gslb = PythonOperator(
        task_id = "update_gslb_task",
        trigger_rule = "one_done",
        python_callable = task_status,
        provide_context = True,
        op_kwargs = {"task_name" : "GSLB"}
    )

    # 테라폼 vm 헬스체크 요청 그룹
    with TaskGroup(group_id='terraform_health_check') as vm_health_check:

        @task(trigger_rule="none_failed")
        def get_service_instances(**context):

            dag_run = context['dag_run']
            service_list = dag_run.conf.get('service_list')

            dynamic_endpoint = []
            base_url = "/api/v1/multi-cloud"
            endpoint = ""

            for service in service_list:
                csp = service.get('csp').lower()
                vm = context['task_instance'].xcom_pull(csp + '_instantiation.' + csp + '_vm').copy()
                print(vm)
                if vm:
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
            trigger_rule="none_failed",
            log_response = True,
            response_check=lambda response: True if json.loads(response.text).get("data").get('vm_instance_status').upper() == 'RUNNING' else False,
            response_filter=lambda response: json.loads(response.text).get("data"),
            do_xcom_push = True
        ).expand(endpoint=get_service_instances())   
    
    # app 배포 태스크 그룹
    with TaskGroup(group_id='ansible_task_group') as ansible:
        
        # app 데이터 추출 및 json 으로 변환
        @task(trigger_rule="none_failed_min_one_success")
        def get_applications(**context):

            dag_run = context['dag_run']
            service_list = dag_run.conf.get('service_list').copy()

            dynamic_data = []

            for service in service_list:
                csp = service.get('csp').lower()
                vm_init_result = context['task_instance'].xcom_pull(csp + '_instantiation.' + csp + '_vm')
                print(vm_init_result)
                service['vm_instance_public_ip'] = vm_init_result.get('public_ip') if vm_init_result else ""
                dynamic_data.append(json.dumps(service))

            return dynamic_data

        # app 배포 요청
        ansible_request = SimpleHttpOperator.partial(
            task_id = "request_ansible_installation",
            http_conn_id = "ansible",
            headers = {
                "Content-Type" : "application/json",
            },
            log_response = True,
            method = "POST",
            response_check=lambda response: True if response.status_code in [200, 201] else False,
            # endpoint = "/api/v1/controller/" + "{{'services/' if dag_run.conf.get('across_service_type').lower() == 'gslb' else 'across-services/'}}" + "{{dag_run.conf.get('across_service_id')}}/applications",
            endpoint = "/api/v1/controller/across-services/{{dag_run.conf.get('across_service_id')}}/applications",
            retries=0,
            do_xcom_push=True
        ).expand(data=get_applications())

        # 상태 업데이트
        task_alert_task3 = PythonOperator(
            task_id = "update_configuration",
            trigger_rule = "none_skipped",
            python_callable = task_status,
            provide_context = True,
            op_kwargs = {"task_name" : "APP_DEPLOY"}
        )

        ansible_request >> task_alert_task3

    # health check 요청
    health_check = SimpleHttpOperator(
        task_id = "request_health_check",
        http_conn_id = "orchestrator",
        headers = {
            "Content-Type" : "application/json",
        },
        log_response = True,
        method = "GET",
        retries=0,
        endpoint = "/api/v1/orchestrator/across-services/{{dag_run.conf.get('across_service_id')}}",
    )

    get_data() >> csp_point
    csp_point >> [aws_vpc_request, gcp_vpc_request, azure_vpc_request]
    [aws_vm_request, gcp_vm_request, azure_vm_request] >> task_alert_task
    task_alert_task >> across_type
    across_type >> [with_aws, gcp_vpn, azure_vpn, aws_gslb, azure_gslb]
    [other_vpn, gcp_vpn, azure_vpn] >> task_alert_task_vpn
    [aws_gslb, azure_gslb] >> task_alert_task_gslb
    [task_alert_task_vpn, task_alert_task_gslb] >> vm_health_check >> ansible >> health_check
