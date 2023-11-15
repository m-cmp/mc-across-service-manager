import { useGetProcessTransition } from '@apis/useMonitoringMain';
import dayjs from 'dayjs';
import { SeriesOption } from 'echarts';
import { useEffect, useState } from 'react';
import LineChart from '@/components/charts/LineChartComp';

export default function ProcessTransitionChart() {
  const processTrnsition = useGetProcessTransition();

  const [series, setSeries] = useState<SeriesOption[]>([]);
  const [x, setX] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);

    function getProcessSeries(timeValues: string[]): SeriesOption[] | undefined {
      if (processTrnsition.isSuccess) {
        const fieldList = new Map();
        for (const item of processTrnsition.data) {
          fieldList.set(item['host'], item['host']);
        }
        const datas = processTrnsition.data.reduce<{ [key: string]: number[] }>((acc, item) => {
          const field = item['host'];
          if (!acc[field]) {
            acc[field] = new Array(timeValues.indexOf(dayjs(item._time).format('YYYY-MM-DD HH:mm:ss'))).fill(null);
          }
          acc[field].push(item._value);
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

    function getProcessXAxis(): string[] | undefined {
      if (processTrnsition.isSuccess) {
        const timeValues = processTrnsition.data
          .map(item => {
            return dayjs(item._time).format('YYYY-MM-DD HH:mm:ss');
          })
          .sort();
        const setTime = new Set(timeValues);
        return Array.from(setTime);
      }
    }

    const timeValues = getProcessXAxis();
    const data = getProcessSeries(timeValues ? timeValues : []);
    const data2 = timeValues;

    data ? setSeries(data) : null;
    data2 ? setX(data2) : null;
    setLoading(false);
  }, [processTrnsition.data, processTrnsition.isSuccess]);

  if (!series || series.length === 0) {
    return <div>데이터가 존재하지 않습니다.</div>;
  }
  return <LineChart loading={loading} xAxis={x} series={series} />;
}
