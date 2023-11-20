import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import ActiveCnt from './chartComp/ActiveCnt';
import ActiveTransitionChart from './chartComp/ActiveTransitionLine';
import CPUAvgGauge from './chartComp/CPUAvgGauge';
import CPUUsageChart from './chartComp/CPUUsageLine';
import DiskAvgGauge from './chartComp/DiskAvgGauge';
import MemAvgGauge from './chartComp/MemoryAvgGauge';
import MemoryResourceChart from './chartComp/MemoryResourceLine';
import NetResponseHeat from './chartComp/NetResponseTransHeat';
import NetworkPacketChart from './chartComp/NetworkPacketLine';
import ProcessCnt from './chartComp/ProcessCnt';
import ProcessTransitionChart from './chartComp/ProcessStatusTransLine';
import TCPUDPTransitionChart from './chartComp/TCPUDPTransLine';
import TPSCnt from './chartComp/TPSCnt';
import TPSTransitionChart from './chartComp/TPSTransitionLine';

interface Props {
  vmNames: string[];
  hostNames: string[];
}

export default function AcrossServiceMonitoring({ vmNames, hostNames }: Props) {
  const renderHost = (hostName: string, index: number) => (
    <Grid item xs={12} sm={12} md={6} lg={6} key={hostName}>
      <Typography variant="h6" sx={{ marginBottom: 2 }}>
        {vmNames[index]}
      </Typography>
      <Grid container rowSpacing={2} columnSpacing={1}>
        <Grid item xs={12} sm={12} md={3} lg={2.8}>
          <ProcessCnt title="Uptime" instanceName={hostName} />
        </Grid>
        <Grid item xs={6} sm={6} md={6} lg={2.3}>
          <ProcessCnt title="Process 수" instanceName={hostName} type="total" />
        </Grid>
        <Grid item xs={6} sm={6} md={6} lg={2.3}>
          <ProcessCnt title="Thread 수" instanceName={hostName} type="total_threads" />
        </Grid>
        <Grid item xs={6} sm={6} md={6} lg={2.3}>
          <ActiveCnt title="Active 수" instanceName={hostName} />
        </Grid>
        <Grid item xs={6} sm={6} md={6} lg={2.3}>
          <TPSCnt title="TPS" instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={12} md={12} lg={6}>
          <TPSTransitionChart instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={12} md={12} lg={6}>
          <ActiveTransitionChart instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={12} md={12} lg={12}>
          <NetResponseHeat instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={12} md={12} lg={6}>
          <ProcessTransitionChart instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={12} md={12} lg={6}>
          <TCPUDPTransitionChart instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={6} md={6} lg={4}>
          <CPUAvgGauge instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={6} md={6} lg={4}>
          <MemAvgGauge instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={6} md={6} lg={4}>
          <DiskAvgGauge instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={12} md={12} lg={6}>
          <CPUUsageChart instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={12} md={12} lg={6}>
          <MemoryResourceChart instanceName={hostName} />
        </Grid>
        <Grid item xs={12} sm={12} md={12} lg={12}>
          <NetworkPacketChart instanceName={hostName} />
        </Grid>
      </Grid>
    </Grid>
  );

  return (
    <Grid container rowSpacing={4.5} columnSpacing={2}>
      {/* <Grid item xs={12}>
        <Typography variant="h5">모니터링</Typography>
      </Grid> */}

      {hostNames.map(renderHost)}
      {/* <Grid md={0.1} lg={0.1}>
        <Divider orientation="vertical" flexItem sx={{ height: '100%' }} />
      </Grid> */}
    </Grid>
  );
}
