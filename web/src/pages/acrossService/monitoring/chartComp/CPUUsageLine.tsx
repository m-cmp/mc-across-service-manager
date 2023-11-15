import { useGetCpuUsageByInsName } from '@apis/useMonitoringAcrossService';
import dayjs from 'dayjs';
import { SeriesOption } from 'echarts';
import { useEffect, useState } from 'react';
import MainCard from '@/components/cards/MainCard';
import LineChart from '@/components/charts/LineChartComp';

interface Props {
  instanceName: string;
}

export default function CPUUsageChart({ instanceName }: Props) {
  const cpuUsage = useGetCpuUsageByInsName(instanceName);

  const [series, setSeries] = useState<SeriesOption[]>([]);
  const [x, setX] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    function getSeries(): SeriesOption[] | undefined {
      if (cpuUsage.isSuccess) {
        const fieldList = new Map();
        for (const item of cpuUsage.data) {
          fieldList.set(item['_field'], item['_field']);
        }
        const datas = cpuUsage.data.reduce<{ [key: string]: number[] }>((acc, item) => {
          const key = item['_field'];
          if (!acc[key]) {
            acc[key] = [];
          }
          if (typeof item._value === 'number') {
            acc[key].push(Math.round(item._value * 100) / 100);
          }
          return acc;
        }, {});
        const seriesData = Object.keys(datas).map(dataKey => ({
          name: dataKey,
          data: datas[dataKey],
          type: 'line',
        }));
        return seriesData as SeriesOption[];
      }
    }

    function getXAxis() {
      if (cpuUsage.isSuccess) {
        const timeValues = cpuUsage.data.reduce<string[]>((acc, item) => {
          acc.push(dayjs(item._time).format('YYYY-MM-DD HH:mm:ss'));
          return acc;
        }, []);
        // 중복 제거
        const uniqueTimeValues = new Set(timeValues);
        return [...uniqueTimeValues];
      }
    }
    const data = getSeries();
    const data2 = getXAxis();
    data ? setSeries(data) : null;
    data2 ? setX(data2) : null;
    setLoading(false);
  }, [cpuUsage.data, cpuUsage.isSuccess]);

  if (series.length === 0) {
    return (
      <MainCard title="CPU 상태별 사용률(%) - 1h" content={true}>
        <div>데이터가 없습니다.</div>
      </MainCard>
    );
  }
  return (
    <MainCard title="CPU 상태별 사용률(%) - 1h" content={true}>
      <LineChart loading={loading} xAxis={x} series={series} />
    </MainCard>
  );
}
