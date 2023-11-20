import Grid from '@mui/material/Grid';
import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import GslbAndVpnGraph from './components/GslbAndVpnGraph';
import AcrossServiceMonitoring from './monitoring/AcrossServiceMonitoring';
import MainCard from '@/components/cards/MainCard';
import { AcrossServiceData, useAcrossServiceDetail } from '@/network/apis/useAcrossServiceDetail';
import { useGetAcrossPingData } from '@/network/apis/useMonitoringAcrossService';
import ServiceListGrid from '@/pages/acrossService/components/ServiceListGrid';

export default function AcrossServiceDetailView() {
  const location = useLocation();
  const acrossServiceId = location.pathname.split('/')[2];
  const detailData = useAcrossServiceDetail(acrossServiceId);
  const [hostNames, setHostNames] = useState<string[]>([]);
  const [vmNames, setVmNames] = useState<string[]>([]);
  const { data: acrossPing } = useGetAcrossPingData(hostNames);
  const [acrossService, setAcrossService] = useState<AcrossServiceData[]>([]);
  // 연계서비스의 ping데이터 조회
  useEffect(() => {
    setHostNames(
      acrossService.map(across =>
        across.csp === 'AWS' ? 'ip-' + across.vmInstancePrivateIp.replace(/\./g, '-') : across.vmInstanceName
      )
    );
  }, [acrossService]);

  useEffect(() => {
    setVmNames(acrossService.map(across => across.vmInstanceName));
  }, [acrossService]);

  useEffect(() => {
    if (detailData.isSuccess) {
      setAcrossService(detailData.data);
    }
  }, [detailData.data, detailData.isSuccess]);

  return (
    <section>
      <Grid container rowSpacing={2} columnSpacing={3}>
        <Grid item xs={12} sm={12} md={4} lg={3}>
          <MainCard title="GSLB 및 VPN 현황" content={true} contentSX={{ minHeight: '150px' }}>
            <GslbAndVpnGraph acrossService={acrossService} pingData={acrossPing ? acrossPing : []} />
          </MainCard>
        </Grid>

        <Grid item xs={12} sm={12} md={8} lg={9}>
          <MainCard title="서비스 목록" content={true} contentSX={{ minHeight: '150px' }}>
            <ServiceListGrid data={acrossService} />
          </MainCard>
        </Grid>

        {/* 연계서비스 모니터링 */}
        <Grid item xs={12} sm={12} md={12} lg={12}>
          <AcrossServiceMonitoring vmNames={vmNames} hostNames={hostNames} />
        </Grid>
      </Grid>
    </section>
  );
}
//#endregion
