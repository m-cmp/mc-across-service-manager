import { get, post, put } from '@axios/axios';
import { GridRowId } from '@mui/x-data-grid';
import { UseQueryResult, useQuery } from 'react-query';

/**
 * API BASE URL
 */
const bffAPIUrl = 'api/v1/bff';
const controllerAPIUrl = 'api/v1/controller';
const orchestratorAPIUrl = 'api/v1/orchestrator';

/**
 * API ResponseEntity Response 데이터
 */
export interface ApiResponse {
  status: number; //code (200/404/500...)
  data: object; //DTO
}

/**
 * API ResponseEntity Response 로그
 */
function responseApiLog(response: ApiResponse, func: string): void {
  console.log(`[${func} API Response]`, response);
}

/**
 * 서비스 목록 조회 API Request 데이터
 * webserver.IntegratedInstanceVO
 */
export interface InstanceData {
  serviceInstanceId: string; // 인스턴스 id
  acrossServiceId: number; // 연계 서비스 id
  serviceId: number; // 서비스 id
  vpcId: number; // vpc id
  csp: string; // 클라우드 서비스 프로바이더 타입
  vmInstanceId: string; // vm 인스턴스 id
  vmInstanceName: string; // vm 인스턴스명
  vmInstanceStatus: string; // vm 인스턴스 상태(health)
  vmInstancePublicIp: string; // vm 인스턴스 public ip
  vmInstancePrivateIp: string; // vm 인스턴스 private ip
  vmInstanceCreateDate: Date; // vm 인스턴스 생성일시
  vmMemoryType: string; // vm memory type
  agentActivateYn: 'Y' | 'N'; // agent 활성화 여부
  agentDeployYn: 'Y' | 'N'; // agent 배포 여부
  agentDeployDate: Date; // agent 배포 일시
  tfstateFilePath: string; // 서비스 템플릿 파일 경로
  gcpHealthcheckFlag: string; // gcp healthcheck 플래그(gcp project id)
  applicationId: number; // 애플리케이션 id
  applicationName: string; // 애플리케이션명
  applicationType: string; // 애플리케이션 타입
  applicationStatus: 'RUNNING' | 'STANDBY' | 'STOPPED' | 'NONE'; // 애플리케이션 상태
  applicationActivateYn: 'Y' | 'N'; // 애플리케이션 활성화 여부
  applicationCreateDate: Date; // 애플리케이션 생성일시
}

/**
 * 서비스 상세 조회 API Request 데이터
 * webserver.IntegratedServiceVO
 */
export interface ServiceDetailData extends InstanceData {
  serviceId: number; // 서비스 id
  serviceName: string; // 서비스명
  serviceTemplateId: number;
  csp: string; // 서비스 CSP
  serviceStatus: string; //서비스 상태(INIT/RUNNING/STOPPED/ERROR)
  deleteYn: string; // 서비스 삭제 여부
  serviceCreateDate: string; // 서비스 생성일시
  templateDTOs: ServiceTemplateData[]; //추가됨 // 마이그레이션 템플릿 리스트
}

export interface ServiceTemplateData {
  serviceTemplateId: number;
  serviceTemplateName: string;
  acrossType: string;
  targetCsp1: string;
  targetCsp2: string;
  serviceTemplatePath: string;
  serviceTemplateCreateDate: string;
}

/**
 * Migration API Reqeust 데이터
 * To orchestrator
 */
export interface MigrationServiceData {
  serviceTemplateId: number;
}

/**
 * Agent 활성화 API Reqeust 데이터
 * To orchestrator
 */
export interface ActivateAgentData {
  service_instance_id: string;
  agent_activate_yn: string;
}

/**
 * 서비스 활성화(APP 활성화) API Reqeust 데이터
 * To controller
 */
export interface ActivateServiceData {
  application_id: number;
  vm_instance_public_ip: string;
  application_activate_yn: string;
}

/**
 * 서비스 상세 조회
 * @param serviceId
 * @returns ServiceDetailData
 */
export const useServiceDetail = (serviceId: number): UseQueryResult<ServiceDetailData> => {
  // export const useServiceDetail = (serviceId: string): UseQueryResult<ServiceDetailData> => {
  //Get
  const queryKey = `useServiceDetail-${serviceId}`;
  return useQuery<ServiceDetailData>(
    [queryKey],
    async () => {
      // const result = await get<ServiceDetailData>(`/${bffAPIUrl}/service/1000`); //exception test
      const result = await get<ServiceDetailData>(`/${bffAPIUrl}/service/${serviceId}`);

      // responseApiLog(result, '서비스 조회');
      console.log(result);
      return result;
    },
    {
      refetchOnWindowFocus: true,
      refetchInterval: 10000,
      onError: () => {
        console.log('서비스 상세 조회 요청 실패');
      },
    }
  );
};

/**
 * 서비스 Migration
 * @param serviceId
 * @returns
 */
export const useMigrationService = (params: {
  serviceId: string;
  serviceTemplateId: GridRowId[];
}): UseQueryResult<ApiResponse> => {
  //PUT
  const queryKey = `useMigrationService`;
  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      const result = await put<ApiResponse>(`/${orchestratorAPIUrl}/services/${params.serviceId}/migration`, {
        serviceTemplateId: params.serviceTemplateId[0].valueOf(),
      });

      responseApiLog(result, '서비스 마이그레이션');

      return result;
    },
    {
      // refetchOnWindowFocus: true
      // staleTime: 1000 * 60 * 60,
      retry: 0, // 재시도 안함
      enabled: false,
      onError: () => {
        console.log('Migration 요청 실패');
        alert('Migration 요청이 실패했습니다.');
      },
    }
  );
};

/**
 * Agent 배포
 * @param serviceId
 * @returns
 */
export const useDeployAgent = (serviceId: number): UseQueryResult<ApiResponse> => {
  //PUT
  const queryKey = `useDeployAgent-${serviceId}`;
  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      const result = await post<ApiResponse>(`/${orchestratorAPIUrl}/services/${serviceId}/collectors`);

      responseApiLog(result, 'Agent 배포');

      return result;
    },
    {
      // refetchOnWindowFocus: true
      staleTime: 1000 * 60 * 60,
      retry: 0, // 재시도 안함
      onError: () => {
        console.log('Agent 배포 요청 실패');
      },
      enabled: false, // 렌더링 시 API 호출 되지 않도록 설정. 버튼 클릭 시 호출
    }
  );
};

/**
 * Agent 활성화/비활성화
 * @param serviceId
 * @param ActivateAgentData
 * @returns
 */
export const useActivateAgent = (serviceId: number, params: ActivateAgentData): UseQueryResult<ApiResponse> => {
  //PUT
  const queryKey = `useDeployAgent-${serviceId}`;
  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      const result = await put<ApiResponse>(`/${controllerAPIUrl}/services/collectors/activation`, params);

      responseApiLog(result, 'Agent 활성화');

      return result;
    },
    {
      // refetchOnWindowFocus: true
      staleTime: 1000 * 60 * 60,
      retry: 0, // 재시도 안함
      onError: () => {
        console.log('Agent 배포 요청 실패');
      },
      enabled: false, // 렌더링 시 API 호출 되지 않도록 설정. 버튼 클릭 시 호출
    }
  );
};

/**
 * 서비스(APP) 활성화/비활성화
 * @param serviceId
 * @param ActivateServiceData
 * @returns
 */
export const useActivateService = (serviceId: number, params: ActivateServiceData): UseQueryResult<ApiResponse> => {
  //PUT
  const queryKey = `useActivateService-${serviceId}`;
  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      const result = await put<ApiResponse>(
        `/${controllerAPIUrl}/services/${serviceId}/applications/activation`,
        params
      );

      responseApiLog(result, '서비스 활성화');

      return result;
    },
    {
      // refetchOnWindowFocus: true
      staleTime: 1000 * 60 * 60,
      retry: 0, // 재시도 안함
      onError: () => {
        console.log('서비스 활성화 요청 실패');
      },
      enabled: false, // 렌더링 시 API 호출 되지 않도록 설정. 버튼 클릭 시 호출
    }
  );
};

/**
 * 마이그레이션 타겟 템플릿 목록 조회
 * @param serviceTemplateId
 * @returns ServiceDetailData
 */
export const useMigrationTargetServiceTemplateList = (
  serviceTemplateId: number
): UseQueryResult<ServiceTemplateData[]> => {
  //Get
  const queryKey = `useMigrationTargetServiceTemplateList-${serviceTemplateId}`;
  return useQuery<ServiceTemplateData[]>(
    [queryKey],
    async () => {
      const result = await get<ServiceTemplateData[]>(`/${bffAPIUrl}/template/migration/${serviceTemplateId}`);

      // responseApiLog(result, '마이그레이션 타겟 템플릿 목록 조회');

      return result;
    },
    {
      // refetchOnWindowFocus: true
      // staleTime: 1000 * 60 * 60,/
      retry: 0, // 재시도 안함
      onError: () => {
        console.log('마이그레이션 타겟 템플릿 목록 조회 요청 실패');
      },
      // enabled: false, // 렌더링 시 API 호출 되지 않도록 설정. 버튼 클릭 시 호출
    }
  );
};
