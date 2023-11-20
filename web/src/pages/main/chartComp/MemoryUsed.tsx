import { useGetMemoryUsed } from '@apis/useMonitoringMain';
import dayjs from 'dayjs';
import { SeriesOption } from 'echarts';
import { useEffect, useState } from 'react';
import LineChart from '@/components/charts/LineChartComp';

export default function MemoryUsedChart() {
  const memUsed = useGetMemoryUsed();

  const [series, setSeries] = useState<SeriesOption[]>([]);
  const [x, setX] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    function getMemUsageSeries(): SeriesOption[] | undefined {
      if (memUsed.isSuccess) {
        const fieldList = new Map();
        for (const item of memUsed.data) {
          fieldList.set(item['host'], item['host']);
        }
        const datas = memUsed.data.reduce<{ [key: string]: number[] }>((acc, item) => {
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

        // Add a type guard to check the value of the `type` property.
        // return seriesData.filter(item => item.type === 'line') as SeriesOption[];
      }
    }

    function getMemUsageXAxis() {
      if (memUsed.isSuccess) {
        const timeValues = memUsed.data
          .map(item => {
            return dayjs(item._time).format('YYYY-MM-DD HH:mm:ss');
          })
          .sort();
        const setTime = new Set(timeValues);
        return Array.from(new Set(setTime));
      }
    }
    const data = getMemUsageSeries();
    const data2 = getMemUsageXAxis();
    data ? setSeries(data) : null;
    data2 ? setX(data2) : null;
    setLoading(false);
  }, [memUsed.data, memUsed.isSuccess]);

  if (!series || series.length === 0) {
    return <div>데이터가 존재하지 않습니다.</div>;
  }
  return <LineChart loading={loading} xAxis={x} series={series} />;
}
