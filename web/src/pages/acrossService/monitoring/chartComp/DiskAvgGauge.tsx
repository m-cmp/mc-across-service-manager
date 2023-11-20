import GaugeChart from '@components/charts/GaugeChartComp';
import { useEffect, useState } from 'react';
import MainCard from '@/components/cards/MainCard';
import { useGetDiskUsedByInsName } from '@/network/apis/useMonitoringAcrossService';

interface Props {
  instanceName: string;
}

export default function DiskAvgGauge({ instanceName }: Props) {
  const result = useGetDiskUsedByInsName(instanceName);

  const [loading, setLoading] = useState(true);
  const [gaugeData, setGaugeData] = useState(0);
  useEffect(() => {
    setLoading(true);
    if (result.isSuccess) {
      if (result.data.length > 0 && typeof result.data[0]._value === 'number') {
        setGaugeData(Math.round(result.data[0]._value * 100) / 100);
      }
    }
  }, [result.data, result.isSuccess]);
  return (
    <MainCard title="Disk 사용률(%) - 1m" content={true}>
      <GaugeChart loading={loading} data={gaugeData} />
    </MainCard>
  );
}
