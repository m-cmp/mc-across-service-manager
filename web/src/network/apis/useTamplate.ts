import { get, deleteOne, post } from '@axios/axios';
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

export interface Data {
  serviceTemplateId: number;
  serviceTemplateName: string;
  templateServiceType: string;
  acrossType: string;
  targetCsp1: string;
  targetCsp2: string;
  serviceTemplatePath: string;
  serviceTemplateCreateDate: string;
}

export const useGetTamplate = (): UseQueryResult<Data[]> => {
  //Get
  const queryKey = `useGetTamplate`;
  return useQuery<Data[]>(
    [queryKey],
    async () => {
      const result = await get<Data[]>('/api/v1/bff/template');
      return result;
    },
    {
      refetchOnWindowFocus: false,
      staleTime: 1000 * 60 * 60,
      refetchInterval: 5000,

      retry: 0,
      onError: () => {
        console.log('템플릿 목록 요청 실패');
      },
    }
  );
};

/**
 * 서비스 생성
 * @param data: TemplateData
 * @returns
 */
export const useDeployService = (data: Data | null): UseQueryResult<ApiResponse> => {
  //PUT
  let queryKey = '';
  let url = '';
  let serviceTemplateId;
  const serviceData = {
    serviceTemplateId: 0,
  };

  if (data) {
    serviceTemplateId = data.serviceTemplateId;
    queryKey = `useDeployService-${serviceTemplateId}`;
    url = `/api/v1/orchestrator/services`;
    serviceData.serviceTemplateId = serviceTemplateId;
  }

  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      const result = await post<ApiResponse>(url, serviceData);

      responseApiLog(result, '서비스 생성');

      return result;
    },
    {
      // refetchOnWindowFocus: true
      staleTime: 1000 * 60 * 60,
      retry: 0, // 재시도 안함
      onError: () => {
        console.log('서비스 생성 요청 실패');
      },
      enabled: false, // 렌더링 시 API 호출 되지 않도록 설정. 버튼 클릭 시 호출
    }
  );
};

export const useDeleteTemplate = (serviceTemplateId: number): UseQueryResult<ApiResponse> => {
  const queryKey = `useDeleteTemplate`;

  const url = `/api/v1/bff/template/delete`;

  return useQuery<ApiResponse>(
    [queryKey],
    async () => {
      const result = await deleteOne<ApiResponse>(url, {
        serviceTemplateId,
      });

      responseApiLog(result, '템플릿 삭제');

      return result;
    },
    {
      retry: 0,
      enabled: false,
      onError: () => {
        console.log('템플릿 삭제 요청 실패');
      },
    }
  );
};
