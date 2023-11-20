import { deleteOne, get } from '@axios/axios';
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
 * 서비스 목록 조회 데이터
 * webserver.IntegratedServiceListVO
 */
export interface ServiceListData {
  rowId: string; //ROW NUMBER
  serviceId: number; // 서비스 id
  serviceName: string; // 서비스명
  csp: string; // 서비스 CSP
  serviceStatus: string; //서비스 상태(INIT/RUNNING/STOPPED/ERROR)
  deleteYn: string; // 서비스 삭제 여부
  serviceCreateDate: string; // 서비스 생성일시
}

/**
 * 서비스 목록 조회
 * @returns
 */
export const useServiceList = (): UseQueryResult<ServiceListData[]> => {
  //Get
  const queryKey = `ServiceListData`;
  return useQuery<ServiceListData[]>(
    [queryKey],
    async () => {
      const result = await get<ServiceListData[]>(`${bffAPIUrl}/service`);
      return result;
    },
    {
      refetchOnWindowFocus: true,
      // staleTime: 1000 * 60 * 60, //1분
      refetchInterval: 30000,
      onError: error => {
        console.log('서비스 목록 조회 요청 실패', error);
      },
    }
  );
};

/**
 * 서비스 Health Check
 * @returns
 */
export const useServiceListHealthCheck = (): UseQueryResult<ServiceListData[]> => {
  //Get
  const queryKey = `useServiceListHealthCheck`;
  return useQuery<ServiceListData[]>(
    [queryKey],
    async () => {
      const result = await get<ServiceListData[]>(`${orchestratorAPIUrl}/services`);
      return result;
    },
    {
      retry: false,
      // refetchInterval: 20000,
    }
  );
};

/**
 * 서비스 삭제
 * @param selectionModel  삭제할 service_id 리스트 (number[])
 * @returns ApiResponse
 */
export const useDeleteService = (selectionModel: GridRowId[]): UseQueryResult<ApiResponse> => {
  console.log(selectionModel.map(item => item.toString().split('-')[0]).toString());
  //DELETE
  const queryKey = `useServiceDetail-${selectionModel.sort().toString()}`;
  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      const result = await deleteOne<ApiResponse>(`${orchestratorAPIUrl}/services`, {
        service_id: selectionModel.map(item => item.toString().split('-')[0]).toString(),
      });

      responseApiLog(result, '서비스 삭제');
      return result;
    },
    {
      // refetchOnWindowFocus: true
      // staleTime: 1000 * 60 * 60,
      retry: false, // 재시도 안함
      enabled: false,
      onError: error => {
        console.error('서비스 삭제 API 요청 실패:', error);
        alert('삭제 요청에 실패했습니다.');
      },
    }
  );
};
