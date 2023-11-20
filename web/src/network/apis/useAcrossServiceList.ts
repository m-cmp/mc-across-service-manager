import { get, deleteOne } from '@axios/axios';
import { GridRowId } from '@mui/x-data-grid';
import { UseQueryResult, useQuery } from 'react-query';

const bffAPIUrl = '/api/v1/bff';
const orchestratorAPIUrl = '/api/v1/orchestrator';

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
 * 인스턴스, 애플리케이션 통합 데이터
 * webserver.IntegratedAcrossServiceListVO
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
  agentStatus: 'RUNNING' | 'STANDBY' | 'STOPPED' | 'NONE'; // agent 상태
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
 * 연계 서비스 통합 조회 데이터
 * webserver.IntegratedAcrossServiceListVO
 */
export interface AcrossServiceListData extends InstanceData {
  acrossServiceName: string; // 연계 서비스명
  acrossType: string; // 연계 서비스 타입
  acrossStatus: string; //연계 서비스 상태(INIT/RUNNING/STOPPED/ERROR)
  gslbDomain: string; // GSLB 도메인
  gslbCsp: string; // 메인 GSLB의 CSP
  gslbWeight: number; // GSLB 가중치
  customerGslbWeight: number; // 타겟 GSLB 가중치
  deleteYn: string;
  acrossCreateDate: string;
}

/**
 * 연계 서비스 목록 조회
 * @returns
 */
export const useAcrossServiceList = (): UseQueryResult<AcrossServiceListData[]> => {
  //Get
  const queryKey = `useAcrossServiceList`;
  return useQuery<AcrossServiceListData[]>(
    [queryKey],
    async () => {
      const result = await get<AcrossServiceListData[]>(`${bffAPIUrl}/across-service`);
      return result;
    },
    {
      refetchOnWindowFocus: true,
      // staleTime: 1000 * 60 * 60,
      refetchInterval: 10000,
      onError: () => {
        console.log('연계서비스 목록 요청 실패');
      },
    }
  );
};

/**
 * 연계 서비스 삭제
 * @param selectionModel  삭제할 across_service_id 리스트 (number[])
 * @returns ApiResponse
 */
export const useDeleteAcrossService = (
  vmOnlyYn: string | null,
  selectionModel: GridRowId[]
): UseQueryResult<ApiResponse> => {
  const queryKey = `useDeleteAcrossService-${selectionModel.sort().toString()}`;
  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      const result = await deleteOne<ApiResponse>(`${orchestratorAPIUrl}/across-services`, {
        vm_only_yn: vmOnlyYn,
        across_service_id: selectionModel.sort().toString(),
      });

      responseApiLog(result, '연계 서비스 삭제');

      return result;
    },
    {
      retry: 0,
      enabled: selectionModel && vmOnlyYn !== undefined && vmOnlyYn !== null,
    }
  );
};
