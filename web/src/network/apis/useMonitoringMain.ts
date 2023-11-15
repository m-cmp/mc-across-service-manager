import { get } from '@axios/axios';
import { UseQueryResult, useQuery } from 'react-query';

interface MonitoringData {
  result: string;
  table: number;
  _start: string;
  _stop: string;
  _time: string;
  _value: number;
  _field: string;
  _measurement: string;
  cpu: string;
  host: string;
}
export interface CSPCount {
  csp: string;
  vpcCnt: number;
  instanceCnt: number;
  appCnt: number;
  serviceCnt: number;
  gslbCnt: number;
  vpnCnt: number;
  acrossServiceCnt: number;
}

export interface Count {
  status: string;
  cnt: number;
}
export interface CSPStatusCount {
  instance: Count[];
  agent: Count[];
  service: Count[];
  acrossService: Count[];
}

const basicOption = {
  refetchOnWindowFocus: true,
  staleTime: 30000, // 30초 이내에는 캐시된 결과를 사용
  refetchInterval: 30000, //30초마다 refetch
};

/**
 * 1시간 내 CPU Usage 인스턴스별 모니터링
 * @returns
 */
export const useGetCPUUsage = (): UseQueryResult<MonitoringData[]> => {
  const queryKey = `cpuUsage`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>('/api/v1/bff/monitoring/cpu/usage');
      return result;
    },
    basicOption
  );
};

/**
 * 1시간 내 메모리 사용량 인스턴스별 모니터링
 * @returns
 */
export const useGetMemoryUsed = (): UseQueryResult<MonitoringData[]> => {
  const queryKey = `memoryUsed`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>('/api/v1/bff/monitoring/memory/used');
      return result;
    },
    basicOption
  );
};

/**
 * 1시간 내 디스크 사용량 인스턴스별 모니터링
 * @returns
 */
export const useGetDiskUsed = (): UseQueryResult<MonitoringData[]> => {
  const queryKey = `diskUsed`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>('/api/v1/bff/monitoring/disk/used');
      return result;
    },
    basicOption
  );
};

/**
 * 1시간 내 프로세스 추이 인스턴스별 모니터링
 * @returns
 */
export const useGetProcessTransition = (): UseQueryResult<MonitoringData[]> => {
  const queryKey = `processTransiton`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>('/api/v1/bff/monitoring/process/transition');
      return result;
    },
    basicOption
  );
};

/**
 * 1시간 내 스레드 추이 인스턴스별 모니터링
 * @returns
 */
export const useGetThreadTransition = (): UseQueryResult<MonitoringData[]> => {
  const queryKey = `threadTransiton`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>('/api/v1/bff/monitoring/process/thread/transition');
      return result;
    },
    basicOption
  );
};

/**
 * CSP별 자원 현황 조회
 * @returns
 */
export const useGetCSPCnt = (): UseQueryResult<CSPCount[]> => {
  const queryKey = `cspCnt`;
  return useQuery<CSPCount[]>(
    [queryKey],
    async () => {
      const result = await get<CSPCount[]>('/api/v1/bff/monitoring/csp/resource/cnt');
      return result;
    },
    basicOption
  );
};

/**
 * 모든 CSP 상태별 수 모니터링 조회
 * @returns
 */
export const useGetCSPStatusCnt = (): UseQueryResult<CSPStatusCount> => {
  const queryKey = `cspStatusCnt`;
  return useQuery<CSPStatusCount>(
    [queryKey],
    async () => {
      const result = await get<CSPStatusCount>('/api/v1/bff/monitoring/csp/status/cnt');
      return result;
    },
    {
      ...basicOption,
    }
  );
};
