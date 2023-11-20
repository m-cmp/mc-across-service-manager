import { get, post, put } from '@axios/axios';
import { UseQueryResult, useQuery } from 'react-query';

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
 * 인스턴스 데이터
 */
export interface Data {
  serviceInstanceId: string; // 서비스 인스턴스 id
  acrossServiceId?: number; // 연계 서비스 id
  serviceId?: number; // 서비스 id
  vpcId?: number; // vpc id
  csp: string; // 클라우드 서비스 프로바이더 타입
  vmInstanceId: string; // vm 인스턴스 id
  vmInstanceName: string; // vm 인스턴스명
  vmInstanceStatus: string; // vm 인스턴스 상태(health)
  vmInstancePublicIp?: string; // vm 인스턴스 public ip
  vmInstancePrivateIp?: string; // vm 인스턴스 private ip
  vmInstanceCreateDate: Date; // vm 인스턴스 생성일시
  vmMemoryType?: string; // vm memory type
  agentActivateYn: string; // agent 상태(health)
  agentDeployYn: string; // agent 배포 여부(Y/N)
  agentDeployDate?: Date; // agent 배포 일시
  tfstateFilePath: string; // 서비스 템플릿 파일 경로
  gcpHealthcheckFlag?: string; // gcp healthcheck 플래그(gcp project id)}
}

/**
 * 인스턴스 목록 조회
 */
export const useGetInstanceList = (): UseQueryResult<Data[]> => {
  //Get
  const queryKey = `useGetInstanceList`;
  return useQuery<Data[]>(
    [queryKey],
    async () => {
      try {
        const result = await get<Data[]>('/api/v1/bff/instance');
        return result;
      } catch (error) {
        console.error('인스턴스 데이터 가져오기 오류:', error);
        throw error;
      }
    },
    {
      refetchOnWindowFocus: true,
      refetchInterval: 5000,
      onError: error => {
        console.error('인스턴스 데이터 가져오기 오류:', error);
      },
    }
  );
};

/**
 * 인스턴스 Agent 배포
 * @param Data
 */
export const useDeployAgent = (data: Data | null): UseQueryResult<ApiResponse> => {
  let url = '';
  let queryKey = '';
  if (data) {
    const serviceInstanceId = data.serviceInstanceId;
    const acrossServiceId = data.acrossServiceId;
    queryKey = `agentDeploy-${serviceInstanceId}-${acrossServiceId}-${data.agentDeployYn}`;

    if (data.acrossServiceId) {
      url = `/api/v1/orchestrator/across-services/across-service-instances/${data.acrossServiceId}/collectors`;
    } else {
      url = `/api/v1/orchestrator/services/service-instances/${data.serviceInstanceId}/collectors`;
    }
  }

  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      try {
        console.log('URL:', url);
        const result = await post<ApiResponse>(url);
        responseApiLog(result, 'Agent 배포');
        return result;
      } catch (error) {
        console.log('URL:', url);
        console.error('서버 에러:', error);
        throw error;
      }
    },
    {
      staleTime: 1000 * 60 * 60,
      retry: 0,
      onError: error => {
        console.log('Agent 배포 요청 실패', error);
      },
      enabled: false,
    }
  );
};

/**
 * 인스턴스 Agent 활성화
 * @param Data
 * @returns
 */
export const useActiveAgent = (data: Data | null): UseQueryResult<ApiResponse> => {
  let url = '/api/v1/controller/services/collectors/activation';
  let queryKey = '';
  let agentActivateYn;
  let serviceInstanceId;

  if (data) {
    queryKey = `ActiveAgent-${serviceInstanceId}`;
    serviceInstanceId = data.serviceInstanceId;
    if (data.acrossServiceId) {
      url = `/api/v1/controller/across-services/collectors/activation`;
    }

    if (data.agentActivateYn === 'Y') {
      agentActivateYn = 'N';
    } else if (data.agentActivateYn === 'N') {
      agentActivateYn = 'Y';
    }
  }
  const AgentActivation = {
    service_instance_id: serviceInstanceId,
    agent_active_yn: agentActivateYn,
  };
  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      try {
        const result = await put<ApiResponse>(url, AgentActivation);
        responseApiLog(result, 'Agent 활성화');
        return result;
      } catch (error) {
        console.error('Agent 활성화 요청 실패:', error);
        throw error;
      }
    },

    {
      staleTime: 1000 * 60 * 60,
      retry: 0,
      onError: error => {
        console.log('Agent 활성화 실패', error);
      },
      enabled: false,
    }
  );
};
