import GaugeChart from '@components/charts/GaugeChartComp';
import { useEffect, useState } from 'react';
import MainCard from '@/components/cards/MainCard';
import { useGetCPUUsedByInsName } from '@/network/apis/useMonitoringAcrossService';

interface Props {
  instanceName: string;
}

export default function CPUAvgGauge({ instanceName }: Props) {
  const result = useGetCPUUsedByInsName(instanceName);

  const [loading, setLoading] = useState(true);
  const [gaugeData, setGaugeData] = useState(0);
  useEffect(() => {
    setLoading(true);
    if (result.isSuccess) {
      if (result.data.length > 0 && typeof result.data[0]._value === 'number') {
        setGaugeData(Math.round(result.data[0]._value * 1000) / 100);
      }
    }
  }, [result.data, result.isSuccess]);
  return (
    <MainCard title="CPU 사용률(%) - 1m" content={true}>
      <GaugeChart loading={loading} data={gaugeData} />
    </MainCard>
  );
}
