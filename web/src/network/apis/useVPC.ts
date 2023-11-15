import { get, post } from '@axios/axios';
import { UseQueryResult, useQuery } from 'react-query';
import { Data as TemplateData } from '@/network/apis/useTamplate';

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

export interface VPCData {
  vpcId: number;
  serviceInstanceId: string;
  vpcCidr: string;
  subnetCidr: string;
  vpcCreateDate: string;
  vpnTunnelIp: string;
  vpnTunnelCreateDate: string;
  csp: string | number;
}

export const useGetVPC = (): UseQueryResult<VPCData[]> => {
  //Get
  const queryKey = `useGetVPC`;
  return useQuery<VPCData[]>(
    [queryKey],
    async () => {
      const result = await get<VPCData[]>('/api/v1/bff/vpc');
      return result;
    },
    {
      refetchOnWindowFocus: true,
      // staleTime: 1000 * 60 * 60,
      refetchInterval: 300000,
      onError: () => {
        console.log('VPC 목록 요청 실패');
      },
    }
  );
};

/**
 * 연계 서비스 생성
 * @param data: TemplateData
 * @returns
 */
export const useDeployAcrossService = (data: TemplateData | null, vpc: VPCData[]): UseQueryResult<ApiResponse> => {
  //PUT
  let queryKey = '';
  let url = '';
  let serviceTemplateId;
  let vpcId: number[] | null = null;
  const acrossServiceData = {
    serviceTemplateId: 0,
    vpc_id: [] as number[],
  };

  if (data) {
    serviceTemplateId = data.serviceTemplateId;
    queryKey = `useDeployAcrossService-${serviceTemplateId}`;
    url = `/api/v1/orchestrator/across-services`;

    if (vpc !== null) {
      vpcId = vpc.filter(vpcData => vpcData.vpcId).map(vpcData => vpcData.vpcId);
    }

    acrossServiceData.serviceTemplateId = serviceTemplateId;
    acrossServiceData.vpc_id = vpcId !== null ? vpcId : ([] as number[]);
  }

  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      try {
        const result = await post<ApiResponse>(url, acrossServiceData);
        responseApiLog(result, '연계 서비스 생성');
        return result;
      } catch (error) {
        console.error('연계 서비스 생성 요청 실패:', error);
        throw error;
      }
    },
    {
      staleTime: 1000 * 60 * 60,
      retry: 0, // 재시도 안함
      onError: () => {
        console.log('연계 서비스 생성 요청 실패');
      },
      enabled: false,
    }
  );
};
