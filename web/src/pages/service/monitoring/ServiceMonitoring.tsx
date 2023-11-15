import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import ActiveCnt from '@/pages/acrossService/monitoring/chartComp/ActiveCnt';
import ActiveTransitionChart from '@/pages/acrossService/monitoring/chartComp/ActiveTransitionLine';
import CPUAvgGauge from '@/pages/acrossService/monitoring/chartComp/CPUAvgGauge';
import CPUUsageChart from '@/pages/acrossService/monitoring/chartComp/CPUUsageLine';
import DiskAvgGauge from '@/pages/acrossService/monitoring/chartComp/DiskAvgGauge';
import MemAvgGauge from '@/pages/acrossService/monitoring/chartComp/MemoryAvgGauge';
import MemoryResourceChart from '@/pages/acrossService/monitoring/chartComp/MemoryResourceLine';
import NetResponseHeat from '@/pages/acrossService/monitoring/chartComp/NetResponseTransHeat';
import NetworkPacketChart from '@/pages/acrossService/monitoring/chartComp/NetworkPacketLine';
import ProcessCnt from '@/pages/acrossService/monitoring/chartComp/ProcessCnt';
import ProcessTransitionChart from '@/pages/acrossService/monitoring/chartComp/ProcessStatusTransLine';
import TCPUDPTransitionChart from '@/pages/acrossService/monitoring/chartComp/TCPUDPTransLine';
import TPSCnt from '@/pages/acrossService/monitoring/chartComp/TPSCnt';
import TPSTransitionChart from '@/pages/acrossService/monitoring/chartComp/TPSTransitionLine';

interface Props {
  hostNames: string[];
  vmNames: string[];
}

export default function ServiceMonitoring({ vmNames, hostNames }: Props) {
  const renderHost = (hostName: string) => (
    <Grid item xs={12} sm={12} md={12} lg={12}>
      <Typography variant="h6" sx={{ marginBottom: 2 }}>
        {...vmNames}
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
