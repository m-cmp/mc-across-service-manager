import { useGetThreadTransition } from '@apis/useMonitoringMain';
import dayjs from 'dayjs';
import { SeriesOption } from 'echarts';
import { useEffect, useState } from 'react';
import LineChart from '@/components/charts/LineChartComp';

export default function ThreadTransitionChart() {
  const threadTrnsition = useGetThreadTransition();

  const [series, setSeries] = useState<SeriesOption[]>([]);
  const [x, setX] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);

    function getThreadSeries(timeValues: string[]): SeriesOption[] | undefined {
      if (threadTrnsition.isSuccess) {
        const fieldList = new Map();
        for (const item of threadTrnsition.data) {
          fieldList.set(item['host'], item['host']);
        }
        const datas = threadTrnsition.data.reduce<{ [key: string]: number[] }>((acc, item) => {
          const field = item['host'];
          if (!acc[field]) {
            acc[field] = new Array(timeValues.indexOf(dayjs(item._time).format('YYYY-MM-DD HH:mm:ss'))).fill(null); // 데이터가 시작되기 전까지 null로 채움
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

    function getThreadXAxis(): string[] | undefined {
      if (threadTrnsition.isSuccess) {
        const timeValues = threadTrnsition.data
          .map(item => {
            return dayjs(item._time).format('YYYY-MM-DD HH:mm:ss');
          })
          .sort();
        const setTime = new Set(timeValues);
        return Array.from(setTime);
      }
    }

    const timeValues = getThreadXAxis();
    const data = getThreadSeries(timeValues ? timeValues : []);
    const data2 = timeValues;

    data ? setSeries(data) : null;
    data2 ? setX(data2) : null;
    setLoading(false);
  }, [threadTrnsition.data, threadTrnsition.isSuccess]);

  if (!series || series.length === 0) {
    return <div>데이터가 존재하지 않습니다.</div>;
  }

  return <LineChart loading={loading} xAxis={x} series={series} />;
}
