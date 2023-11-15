import { useEffect, useState } from 'react';
import GraphChart, { GraphData, GraphLink } from '@/components/charts/GraphChartComp';
import { AcrossServiceData } from '@/network/apis/useAcrossServiceDetail';
import { PingData } from '@/network/apis/useMonitoringAcrossService';

interface Props {
  acrossService: AcrossServiceData[];
  pingData: PingData[];
}

export default function GslbAndVpnGraph({ acrossService, pingData }: Props) {
  const [useGSLB, setUseGSLB] = useState(false);
  const [graphData, setGraphData] = useState<GraphData[]>([]);
  const [graphLinks, setGraphLinks] = useState<GraphLink[]>([]);

  useEffect(() => {
    const colors = {
      VM: '#ffdcc3',
      GSLB: '#bfd3fd',
    };
    if (acrossService.length > 0) {
      const vmCount = acrossService.length; // VM의 갯수
      const step = vmCount % 2 === 1 ? 3 : 2;
      const defaultX = 10;
      const defaultY = 10.3;

      // 링크 생성 (Node <-> Node)
      const vmLinks = acrossService.map((vm, index) => {
        const nextVmIndex = (index + 1) % acrossService.length;
        return {
          source: vm.vmInstanceName,
          target: acrossService[nextVmIndex].vmInstanceName,
          tooltip: {
            show: false,
          },
          label: {
            show: true,
            // ping 데이터
            formatter: () => {
              if (pingData.length > 0 && vm.vpnTunnelCreateDate) {
                let text = null;
                pingData.forEach(data => {
                  if (data.url === acrossService[nextVmIndex].vmInstancePrivateIp) {
                    text = Math.round(data._value * 1000) / 100;
                  }
                });
                return text ? text + ' ms' : '';
              } else {
                return '';
              }
            },
          },
          lineStyle: {
            curveness: 0.1,
            width: 3,
            color: vm.vpnTunnelCreateDate ? '#f7bc84' : 'gray',
          },
        };
      });

      // 링크 생성 (GSLB -> Node)
      const gslbLinks = acrossService.map(gslb => {
        return {
          source: gslb.gslbDomain,
          target: gslb.vmInstanceName,
          tooltip: {
            show: false,
          },
          label: {
            show: true,
            fontWeight: 900,
            fontSize: 18,
            color: '#1a60f7',
            font: 'bold',
            formatter: () => {
              if (gslb.mainGslb === 'Y') {
                return gslb.gslbWeight + '%';
              } else {
                return gslb.customerGslbWeight + '%';
              }
            },
          },
          lineStyle: {
            width: 3,
            color: gslb.gslbDomain ? '#84a8f7' : 'gray',
          },
        };
      });

      // 모든 링크를 합쳐서 setGraphLinks에 전달
      setGraphLinks([...vmLinks, ...gslbLinks]);

      // 인스턴스 노드 생성
      const graphDataWithCoordinate = acrossService.map<GraphData>((data, index) => {
        return {
          id: data.vmInstanceName,
          name: 'VM',
          x: defaultX + index * step,
          y: index % step === 0 ? defaultY + index * step : defaultY,
          value: data.vmInstanceName,
          category: 'VM',
          itemStyle: { color: colors['VM'] },
          symbolSize: [40, 40],
        };
      });

      acrossService.forEach(data => {
        // GSLB 정보 구하기
        if (data.mainGslb === 'Y') {
          graphDataWithCoordinate.push({
            id: data.gslbDomain,
            name: 'GSLB',
            x: 11,
            y: 11,
            value: data.gslbDomain,
            category: 'GSLB',
            itemStyle: { color: colors['GSLB'] },
            symbolSize: [40, 40],
          });
          setUseGSLB(true);
        }
      });
      if (!useGSLB) {
        graphDataWithCoordinate.push({
          id: 'gslb',
          name: 'GSLB',
          x: 11,
          y: 11,
          category: 'GSLB',
          itemStyle: { color: '#c2c2c299' },
          symbolSize: [40, 40],
        });
      }
      setGraphData(graphDataWithCoordinate);
    }
  }, [acrossService, pingData, useGSLB]);

  return <GraphChart data={graphData} links={graphLinks} />;
}
