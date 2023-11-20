import { get, post, put } from '@axios/axios';
import { UseQueryResult, useQuery } from 'react-query';

/**
 * 인스턴스, 애플리케이션 통합 조회 데이터
 * webserver.IntegratedInstanceVO
 */

interface InstanceData {
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
  agentActivateYn: 'Y' | 'N';
  agentDeployYn: 'Y' | 'N'; // agent 배포 여부
  agentDeployDate: Date; // agent 배포 일시
  tfstateFilePath: string; // 서비스 템플릿 파일 경로
  gcpHealthcheckFlag: string; // gcp healthcheck 플래그(gcp project id)
  applicationId: number; // 애플리케이션 id
  applicationName: string; // 애플리케이션명
  applicationType: string; // 애플리케이션 타입
  // applicationStatus: 'RUNNING' | 'STANDBY' | 'STOPPED' | 'NONE'; // 애플리케이션 상태
  applicationActivateYn: 'Y' | 'N'; // 애플리케이션 활성화 여부
  applicationCreateDate: Date; // 애플리케이션 생성일시
}

/**
 * 연계 서비스 상세 조회 데이터
 * webserver.IntegratedAcrossServiceVO
 */
export interface AcrossServiceData extends InstanceData {
  acrossType: string; // 연계서비스 타입
  gslbDomain: string; // GSLB 도메인
  gslbCsp: string; // 메인 GSLB의 CSP
  gslbWeight: number; // GSLB 가중치
  customerGslbWeight: number; // 타겟 GSLB 가중치
  mainGslb: string; // 메인 GSLB 여부 (Y/N)
  vpnTunnelIp: string; // VPN 터널 IP
  vpnTunnelCreateDate: string;
}

export interface ActivationService {
  application_id: number;
  application_activate_yn: string;
  vm_instance_public_ip?: string;
  vm_instance_private_ip?: string;
  application_type: string;
  across_type: string;
}

export interface ActivationAgent {
  serviceInstanceId: string;
  vmInstancePublicIp: string;
  agentActivateYN: string;
  pingTargetUrl: string;
  applicationType: string;
  acrossType: string;
}

interface Response<T> {
  code: number;
  message: string;
  data?: T;
}

/**
 * 연계서비스 상세 조회
 * @param acrossServiceId
 * @returns
 */
export const useAcrossServiceDetail = (acrossServiceId: string): UseQueryResult<AcrossServiceData[]> => {
  //Get
  const queryKey = `acrossServiceDetail-${acrossServiceId}`;
  return useQuery<AcrossServiceData[]>(
    [queryKey],
    async () => {
      const result = await get<AcrossServiceData[]>(`/api/v1/bff/across-service/${acrossServiceId}`);
      return result;
    },
    {
      refetchOnWindowFocus: true,
      refetchInterval: 10000,
      onError: () => {
        console.log('연계서비스 상세 조회 요청 실패');
      },
    }
  );
};

/**
 * 연계서비스 활성화
 * @param acrossServiceId
 * @param params
 * @returns
 */
export const useActiveAcrossService = (
  acrossServiceId: number,
  params: ActivationService[]
): UseQueryResult<Response<string>> => {
  const queryKey = `acrossServiceActivation-${acrossServiceId}`;
  return useQuery<Response<string>>(
    [queryKey],
    async () => {
      const result = await put<Response<string>>(
        `/api/v1/controller/across-services/${acrossServiceId}/applications/activation`,
        params
      );
      return result;
    },
    {
      retry: false,
      refetchOnWindowFocus: false,
      enabled: params && params.length > 0,
      onError: () => {
        console.log('연계서비스 활성화 오류');
      },
    }
  );
};

/**
 * 연계서비스 Agent 배포
 * @param acrossServiceId
 * @returns
 */
export const useDeployAcrossSVCAgents = (acrossServiceId: number): UseQueryResult<Response<string>> => {
  const queryKey = `deployAcrossSVCAgents-${acrossServiceId}`;
  return useQuery<Response<string>>(
    [queryKey],
    async () => {
      const result = await post<Response<string>>(`/api/v1/orchestrator/across-services/${acrossServiceId}/collectors`);
      return result;
    },
    {
      retry: false,
      refetchOnWindowFocus: false,
      enabled: false,
      onError: () => {
        console.log('연계서비스 Agent 배포 오류');
      },
    }
  );
};

/**
 * 연계서비스 agent 활성화
 * @param acrossServiceId
 * @param params
 * @returns
 */
export const useActiveAcrossAgent = (
  acrossServiceId: number,
  agentActivationParam: ActivationAgent[]
): UseQueryResult<Response<string>> => {
  const queryKey = `acrossServiceActivation-${acrossServiceId}`;
  return useQuery<Response<string>>(
    [queryKey],
    async () => {
      const result = await put<Response<string>>(
        `/api/v1/controller/across-services/collectors/activation`,
        agentActivationParam
      );
      return result;
    },
    {
      retry: false,
      refetchOnWindowFocus: false,
      enabled: agentActivationParam && agentActivationParam.length > 0,
      onError: () => {
        console.log('연계서비스 활성화 오류');
      },
    }
  );
};
