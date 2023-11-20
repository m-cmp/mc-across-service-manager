import Widget from '@components/WidgetComp';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import styled from 'styled-components';
import CPUUsage from './chartComp/CPUUsage';
import DiskUsedChart from './chartComp/DiskUsed';
import MemoryUsed from './chartComp/MemoryUsed';
import ProcessTransition from './chartComp/ProcessTransition';
import ThreadTransition from './chartComp/ThreadTransition';
import MainCard from '@/components/cards/MainCard';
import PizzaChart from '@/components/charts/PizzaChartComp';
import CountChip from '@/components/CountChip';
import { useGetCSPCnt, useGetCSPStatusCnt, Count } from '@/network/apis/useMonitoringMain';

const CspParagraph = styled.p`
  width: 100%;
  min-height: 30px;
  padding: 5px;
`;
interface Label {
  label: string;
  name: 'acrossServiceCnt' | 'serviceCnt' | 'appCnt' | 'vpcCnt' | 'vpnCnt' | 'gslbCnt' | 'instanceCnt';
}
// CSP별 자원 리스트
const resourceList: Label[] = [
  { label: '연계 서비스', name: 'acrossServiceCnt' },
  { label: '서비스', name: 'serviceCnt' },
  { label: 'APP', name: 'appCnt' },
  { label: 'VPC', name: 'vpcCnt' },
  { label: 'VPN', name: 'vpnCnt' },
  { label: 'GSLB', name: 'gslbCnt' },
  { label: '인스턴스', name: 'instanceCnt' },
];

// 상태 리스트
const statusGraphList: Array<{
  title: string;
  value: 'agent' | 'service' | 'acrossService' | 'instance';
  status: string[];
}> = [
  { title: '인스턴스 현황', value: 'instance', status: ['RUNNING', 'STOPPED'] },
  { title: '연계서비스 현황', value: 'acrossService', status: ['RUNNING', 'STOPPED'] },
  { title: '서비스 현황', value: 'service', status: ['RUNNING', 'STOPPED'] },
  { title: 'Agent 활성화 현황', value: 'agent', status: ['Activating', 'DeActivated'] },
];

export default function Main() {
  const { data: countList } = useGetCSPCnt();
  const { data: cspStatusData } = useGetCSPStatusCnt();

  const cspList = ['AWS', 'GCP', 'AZURE'];

  const getRunningCount = (statusObj: Count[]): number => {
    return statusObj.reduce((acc, obj) => {
      const currentStatus = obj.status.toUpperCase();
      if (currentStatus === 'RUNNING' || currentStatus === 'Y') {
        return acc + obj.cnt;
      }
      return acc;
    }, 0);
  };

  const getNotRunningCount = (statusObj: Count[]): number => {
    return statusObj.reduce((acc, obj) => {
      const currentStatus = obj.status.toUpperCase();
      if (currentStatus === 'N') {
        return acc + obj.cnt;
      } else if (currentStatus !== 'Y' && currentStatus !== 'RUNNING') {
        return acc + obj.cnt;
      }
      return acc;
    }, 0);
  };

  // 전체 상태별 모니터링 피자 차트 생성
  const renderCSPStatusCharts = () => {
    return statusGraphList.map(status => {
      // 상태별 데이터 리스트 생성
      if (cspStatusData) {
        const graphData = [
          {
            name: status.value === 'agent' ? 'ACTIVE' : 'RUNNING',
            value: getRunningCount(cspStatusData[status.value]),
            itemStyle: { color: '#2ab648c9' },
          },
          {
            name: status.value === 'agent' ? 'DEACTIVE' : 'STOPPED',
            value: getNotRunningCount(cspStatusData[status.value]),
            itemStyle: { color: '#f57260' },
          },
        ];
        return (
          <Grid item xs={12} sm={12} md={6} lg={3} key={status.title}>
            <MainCard title={status.title} content={true}>
              <PizzaChart data={graphData} />
            </MainCard>
          </Grid>
        );
      } else {
        const graphData = [
          { name: 'RUNNING', value: 0, itemStyle: { color: '#2ab648c9' } },
          { name: 'STOPPED', value: 0, itemStyle: { color: '#f57260' } },
        ];
        return (
          <Grid item xs={12} sm={12} md={6} lg={3} key={status.title}>
            <MainCard title={status.title} content={true}>
              <PizzaChart data={graphData} />
            </MainCard>
          </Grid>
        );
      }
    });
  };

  // CSP별 자원 현황 칩 생성
  const renderCSPResources = ({ label, name }: Label) => {
    return (
      <Grid item xs={12} sm={12} md={6} lg={1.5} key={label + name}>
        {(countList &&
          countList.length > 0 &&
          cspList.map(csp => {
            if (countList && countList.length > 0) {
              const count = countList.find(c => {
                if (c.csp === csp) {
                  return c;
                }
              });
              if (count) {
                return <CountChip key={`${csp}-${name}`} label={label} count={count[name] ? count[name] : 0} />;
              } else {
                return <CountChip key={`${csp}-${name}`} label={label} count={0} />;
              }
            }
          })) ||
          cspList.map(csp => {
            return <CountChip key={`${csp}-${name}`} label={label} count={0} />;
          })}
      </Grid>
    );
  };

  return (
    <Grid container rowSpacing={2} columnSpacing={2}>
      {/* row 1 */}
      <Grid item xs={12}>
        <Typography variant="h6">M-CMP 모니터링</Typography>
      </Grid>
      {/* 차트 */}
      <Grid item xs={12} sm={12} md={12} lg={12}>
        <Widget title="csp별 자원 현황">
          {/* 현황 */}
          <Grid container rowSpacing={2} columnSpacing={2}>
            <Grid item xs={1}>
              {cspList.map(csp => {
                return <CspParagraph key={csp}>{csp}</CspParagraph>;
              })}
            </Grid>
            <Grid item xs={0.2}>
              <Divider orientation="vertical" variant="middle" flexItem sx={{ height: '100%' }} />
            </Grid>
            {/* <Grid container sx={{ paddingLeft: 3, paddingTop: 2 }}> */}
            {resourceList.map(renderCSPResources)}
            {/* </Grid> */}
          </Grid>
        </Widget>
      </Grid>
      {/* 상태별 모니터링 현황 */}
      {renderCSPStatusCharts()}

      <Grid item md={12} sx={{ display: { sm: 'none', md: 'block', lg: 'none' } }} />

      {/* row 2 */}
      <Grid item xs={12}>
        <Typography variant="h6">인스턴스 별 모니터링</Typography>
      </Grid>

      {/* row 2 > 차트 영역 */}
      <Grid item xs={12} sm={12} md={4} lg={4}>
        <MainCard title="CPU Usage(%)" content={true}>
          <CPUUsage />
        </MainCard>
      </Grid>
      <Grid item xs={12} sm={12} md={4} lg={4}>
        <MainCard title="Memory Used(%)" content={true}>
          <MemoryUsed />
        </MainCard>
      </Grid>
      <Grid item xs={12} sm={12} md={4} lg={4}>
        <MainCard title="Disk Used(%)" content={true}>
          <DiskUsedChart />
        </MainCard>
      </Grid>
      <Grid item xs={12} sm={12} md={6} lg={6}>
        <MainCard title="프로세스 수 추이" content={true}>
          <ProcessTransition />
        </MainCard>
      </Grid>
      <Grid item xs={12} sm={12} md={6} lg={6}>
        <MainCard title="쓰레드 수 추이" content={true}>
          <ThreadTransition />
        </MainCard>
      </Grid>
    </Grid>
  );
}
