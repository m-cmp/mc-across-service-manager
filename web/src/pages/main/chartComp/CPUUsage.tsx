import { useGetCPUUsage } from '@apis/useMonitoringMain';
import dayjs from 'dayjs';
import { SeriesOption } from 'echarts';
import { useEffect, useState } from 'react';
import LineChart from '@/components/charts/LineChartComp';

export default function CPUUsageChart() {
  const cpuUsage = useGetCPUUsage();

  const [series, setSeries] = useState<SeriesOption[]>([]);
  const [x, setX] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    function getCPUUsageSeries(): SeriesOption[] | undefined {
      if (cpuUsage.isSuccess) {
        const fieldList = new Map();
        for (const item of cpuUsage.data) {
          fieldList.set(item['host'], item['host']);
        }
        const datas = cpuUsage.data.reduce<{ [key: string]: number[] }>((acc, item) => {
          const field = item['host'];
          if (!acc[field]) {
            acc[field] = [];
          }

          acc[field].push(Math.round(item._value * 100) / 100);
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

    function getCPUUsageXAxis() {
      if (cpuUsage.isSuccess) {
        const timeValues = cpuUsage.data
          .map(item => {
            return dayjs(item._time).format('YYYY-MM-DD HH:mm:ss');
          })
          .sort();
        const setTime = new Set(timeValues);
        return Array.from(new Set(setTime));
      }
    }
    const data = getCPUUsageSeries();
    const data2 = getCPUUsageXAxis();
    data ? setSeries(data) : null;
    data2 ? setX(data2) : null;
    setLoading(false);
  }, [cpuUsage.data, cpuUsage.isSuccess]);

  if (!series || series.length === 0) {
    return <div>데이터가 존재하지 않습니다.</div>;
  }
  return <LineChart loading={loading} xAxis={x} series={series} />;
}
