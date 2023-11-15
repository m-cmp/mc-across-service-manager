import { get } from '@axios/axios';
import { UseQueryResult, useQuery } from 'react-query';

interface MonitoringData {
  result: string;
  table: number;
  _start: string;
  _stop: string;
  _time: string;
  _value: number | string;
  _field: string;
  _measurement: string;
  cpu: string;
  host: string;
}

// 네트워크 인터페이스 패킷 자료형
interface NetworkPacketData extends MonitoringData {
  interface: string;
}

// Ping 모니터링 자료형
export interface PingData extends MonitoringData {
  url: string;
  _value: number;
}

// Nginx 모니터링 자료형
export interface NginxData extends MonitoringData {
  port: string;
  server: string;
  _value: number;
}

const basicOption = {
  refetchOnWindowFocus: true,
  staleTime: 30000, // 30초 이내에는 캐시된 결과를 사용
  refetchInterval: 30000, //30초마다 refetch
};

/**
 * CPU 사용량 (%) 모니터링 By 인스턴스명
 * @param instanceName
 * @returns
 */
export const useGetCPUUsedByInsName = (instanceName: string): UseQueryResult<MonitoringData[]> => {
  const queryKey = `cpuUsedAVGByInstanceName-${instanceName}`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>(`/api/v1/bff/monitoring/cpu/used/${instanceName}`);
      return result;
    },
    { ...basicOption, enabled: instanceName ? true : false }
  );
};

/**
 * 메모리 사용량 (%) 모니터링 By 인스턴스명
 * @param instanceName
 * @returns
 */
export const useGetMemUsedByInsName = (instanceName: string): UseQueryResult<MonitoringData[]> => {
  const queryKey = `memUsedAVGByInstanceName-${instanceName}`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>(`/api/v1/bff/monitoring/memory/used/${instanceName}`);
      return result;
    },
    {
      ...basicOption,
      enabled: instanceName ? true : false,
    }
  );
};

/**
 * 디스크 사용량 (%) 모니터링 By 인스턴스명
 * @param instanceName
 * @returns
 */
export const useGetDiskUsedByInsName = (instanceName: string): UseQueryResult<MonitoringData[]> => {
  const queryKey = `diskUsedAVGByInstanceName-${instanceName}`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>(`/api/v1/bff/monitoring/disk/used/${instanceName}`);
      return result;
    },
    { ...basicOption, enabled: instanceName ? true : false }
  );
};

/**
 * CPU Usage 자원 모니터링 By 인스턴스명
 * @param instanceName
 * @returns
 */
export const useGetCpuUsageByInsName = (instanceName: string): UseQueryResult<MonitoringData[]> => {
  const queryKey = `cpuUsageByInstanceName-${instanceName}`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>(`/api/v1/bff/monitoring/cpu/usage/${instanceName}`);
      return result;
    },
    { ...basicOption, enabled: instanceName ? true : false }
  );
};

/**
 * 메모리 상태별 상세 (mb) 모니터링 By 인스턴스명
 * @param instanceName
 * @returns
 */
export const useGetMemResourceByInsName = (instanceName: string): UseQueryResult<MonitoringData[]> => {
  const queryKey = `memResourceByInsName-${instanceName}`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>(`/api/v1/bff/monitoring/memory/resource/${instanceName}`);
      return result;
    },
    { ...basicOption, enabled: instanceName ? true : false }
  );
};

/**
 * 인스턴스의 프로세스 상태별 수 추이
 * @param instanceName
 * @returns
 */
export const useGetProStatusTransByIns = (instanceName: string): UseQueryResult<MonitoringData[]> => {
  const queryKey = `processStatusTransitionByInstance-${instanceName}`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>(`/api/v1/bff/monitoring/process/status/transition/${instanceName}`);
      return result;
    },
    { ...basicOption, enabled: instanceName ? true : false }
  );
};

/**
 * 시간 내 인스턴스 필드(프로세스, 스레드)의 평균 수
 * @param instanceName 인스턴스명
 * @param time 분
 * @param field 'total' 프로세스 | 'total_thread' 스레드
 * @returns
 */
export const useGetFieldCntByIns = (
  instanceName: string,
  time: number,
  field?: 'total' | 'total_threads'
): UseQueryResult<MonitoringData[]> => {
  const queryKey = `fieldCntByInstance-${field}-${instanceName}`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>(`/api/v1/bff/monitoring/process/cnt/${instanceName}`, { time, field });
      return result;
    },
    { ...basicOption, enabled: field && instanceName ? true : false }
  );
};

/**
 * 인스턴스의 uptime 시간
 * @param instanceName
 * @returns
 */
export const useGetSystemUptimeByIns = (instanceName: string): UseQueryResult<MonitoringData[]> => {
  const queryKey = `systemUptimeByInstance-${instanceName}`;
  return useQuery<MonitoringData[]>(
    [queryKey],
    async () => {
      const result = await get<MonitoringData[]>(`/api/v1/bff/monitoring/system/uptime/${instanceName}`);
      return result;
    },
    { ...basicOption, enabled: instanceName ? true : false }
  );
};

/**
 * 인스턴스의 네트워크 인터페이스별 패킷 추이
 * @param instanceName
 * @returns
 */
export const useGetNetPacketTransByIns = (instanceName: string): UseQueryResult<NetworkPacketData[]> => {
  const queryKey = `networkPacketTransitionByInstance-${instanceName}`;
  return useQuery<NetworkPacketData[]>(
    [queryKey],
    async () => {
      const result = await get<NetworkPacketData[]>(`/api/v1/bff/monitoring/network/packet/transition/${instanceName}`);
      return result;
    },
    { ...basicOption, enabled: instanceName ? true : false }
  );
};

/**
 * 1개 인스턴스의 TCP&UDP 추이
 * @param instanceName
 * @returns
 */
export const useGetTCPUDPTransByIns = (instanceName: string): UseQueryResult<NetworkPacketData[]> => {
  const queryKey = `tcpudpTransitionByInstance-${instanceName}`;
  return useQuery<NetworkPacketData[]>(
    [queryKey],
    async () => {
      const result = await get<NetworkPacketData[]>(`/api/v1/bff/monitoring/network/tcpudp/transition/${instanceName}`);
      return result;
    },
    { ...basicOption, enabled: instanceName ? true : false }
  );
};

// Ping

/**
 * 연계서비스 상세 조회 > 연계서비스 ping 모니터링 조회
 * @param string[]
 * @returns
 */
export const useGetAcrossPingData = (hosts: string[]): UseQueryResult<PingData[]> => {
  const queryKey = `acrossServiceDetail-${hosts}`;
  return useQuery<PingData[]>(
    [queryKey],
    async () => {
      const result = await get<PingData[]>(`/api/v1/bff/monitoring/ping/across`, { hosts: hosts.toString() });
      return result;
    },
    {
      ...basicOption,
      enabled: hosts.length > 0,
    }
  );
};

// Nginx

/**
 * 연계서비스 상세 조회 > TPS 수
 * @param string[]
 * @returns
 */
export const useGetTpsByInstance = (instanceName: string): UseQueryResult<NginxData[]> => {
  const queryKey = `tpsCnt-${instanceName}-${instanceName}`;
  return useQuery<NginxData[]>(
    [queryKey],
    async () => {
      const result = await get<NginxData[]>(`/api/v1/bff/monitoring/nginx/tps/${instanceName}`);
      return result;
    },
    {
      ...basicOption,
    }
  );
};

/**
 * 연계서비스 상세 조회 > TPS 추이
 * @param string[]
 * @returns
 */
export const useGetTpsTranstionByIns = (instanceName: string): UseQueryResult<NginxData[]> => {
  const queryKey = `tpsTransiton-${instanceName}-${instanceName}`;
  return useQuery<NginxData[]>(
    [queryKey],
    async () => {
      const result = await get<NginxData[]>(`/api/v1/bff/monitoring/nginx/tps/transition/${instanceName}`);
      return result;
    },
    {
      ...basicOption,
    }
  );
};

/**
 * 연계서비스 상세 조회 > Active 수
 * @param string[]
 * @returns
 */
export const useGetActiveByInstance = (instanceName: string): UseQueryResult<NginxData[]> => {
  const queryKey = `activeCnt-${instanceName}-${instanceName}`;
  return useQuery<NginxData[]>(
    [queryKey],
    async () => {
      const result = await get<NginxData[]>(`/api/v1/bff/monitoring/nginx/active/${instanceName}`);
      return result;
    },
    {
      ...basicOption,
    }
  );
};

/**
 * 연계서비스 상세 조회 > Active 추이
 * @param string[]
 * @returns
 */
export const useGetActiveTransByIns = (instanceName: string): UseQueryResult<NginxData[]> => {
  const queryKey = `activeTransiton-${instanceName}-${instanceName}`;
  return useQuery<NginxData[]>(
    [queryKey],
    async () => {
      const result = await get<NginxData[]>(`/api/v1/bff/monitoring/nginx/active/transition/${instanceName}`);
      return result;
    },
    {
      ...basicOption,
      enabled: instanceName ? true : false,
    }
  );
};

// Net Response

/**
 * 연계서비스 상세 조회 > Net Response 시간 추이
 * @param string[]
 * @returns
 */
export const useGetNetResTimeTranstion = (instanceName: string): UseQueryResult<NginxData[]> => {
  const queryKey = `netResponseTimeTrans-${instanceName}-${instanceName}`;
  return useQuery<NginxData[]>(
    [queryKey],
    async () => {
      const result = await get<NginxData[]>(`/api/v1/bff/monitoring/net-response/time/transition/${instanceName}`);
      return result;
    },
    {
      ...basicOption,
      enabled: instanceName ? true : false,
    }
  );
};
