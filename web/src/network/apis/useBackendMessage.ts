import { get } from '@axios/axios';
import { UseQueryResult, useQuery } from 'react-query';

export interface BackendMessage {
  from: string;
  data: string;
}

/**
 * 벡엔드 메시지 감시
 * @returns
 */
export const useBackendMessage = (): UseQueryResult<BackendMessage> => {
  //Get
  const queryKey = `ServiceListData`;
  return useQuery<BackendMessage>(
    [queryKey],
    async () => {
      const result = await get<BackendMessage>(`/api/v1/bff/backend`);
      return result;
    },
    {
      refetchOnWindowFocus: true,
      refetchInterval: 3000,
    }
  );
};
