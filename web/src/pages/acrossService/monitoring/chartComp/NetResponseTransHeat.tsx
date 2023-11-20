import { useGetNetResTimeTranstion } from '@apis/useMonitoringAcrossService';
import dayjs from 'dayjs';
import timezone from 'dayjs/plugin/timezone.js';
import utc from 'dayjs/plugin/utc.js';
import { useCallback, useEffect, useState } from 'react';
import MainCard from '@/components/cards/MainCard';
import HeatMapComp from '@/components/charts/HeatMapComp';

interface Props {
  instanceName: string;
}

export default function NetResponseHeat({ instanceName }: Props) {
  const result = useGetNetResTimeTranstion(instanceName);
  const [value, setValue] = useState<number[][]>([]);
  const [x, setX] = useState<string[]>([]);
  const [y, setY] = useState<string[]>([]);
  dayjs.extend(utc);
  dayjs.extend(timezone);

  // 임의로 X축 구간 정하기
  const getXAxis = useCallback(() => {
    if (result.isSuccess) {
      const timeValues = result.data.reduce<number[]>((acc, item) => {
        item._time ? acc.push(dayjs(item._time).unix()) : null;
        return acc;
      }, []);
      // 중복 제거
      const uniqueTimeValues = Array.from(new Set(timeValues));
      const len = uniqueTimeValues.length - 1;
      // 구간 설정
      const xStep = Number(uniqueTimeValues[len] - uniqueTimeValues[0]) / 30;
      const xData = Array.from({ length: 31 }, (_, i) => {
        const startTime = uniqueTimeValues[i - 1]
          ? uniqueTimeValues[i - 1] + xStep * i
          : uniqueTimeValues[i] + xStep * i;
        const endTime = uniqueTimeValues[i] + xStep * (i + 1);
        return `${startTime}~${endTime}`;
      });

      return xData;
    } else {
      return [];
    }
  }, [result.data, result.isSuccess]);

  // 임의로 Y축 구간 정하기
  const getYAxis = useCallback(() => {
    if (result.isSuccess) {
      const yValues = result.data.reduce<number[]>((acc, item) => {
        if (typeof item._value === 'number') {
          acc.push(Math.round(item._value * 1000));
        }
        return acc;
      }, []);
      // 중복 제거 후 _value의 크기대로 정렬
      const newY = [...new Set(yValues)];
      newY.sort((a, b) => a - b);

      // 구간 설정
      const range = 10;
      const yRange = yValues[newY.length - 1] - newY[0];
      const yStep = yRange / range;
      const yData = Array.from({ length: range + 1 }, (_, i) => [
        Math.round(newY[i - 1] ? newY[i - 1] + i * yStep : newY[i] + i * yStep),
        Math.round(newY[i] + (i + 1) * yStep),
      ]);

      // string으로 변환
      const stringYData = yData.map(item => `${item[0]}~${item[1]}`);
      return stringYData;
    } else {
      return [];
    }
  }, [result.data, result.isSuccess]);

  // x축과 y축 구간 해당하는 곳 찾기
  const getHeatMapDatas = useCallback(
    (xData: string[], yData: string[]) => {
      if (!result.isSuccess || !yData || !xData) {
        return null;
      }
      const heatDatas = result.data.map(data => {
        let xIndex = -1; // 초기값을 -1로 설정
        xData.forEach((xd, idx) => {
          const [before, after] = xd.split('~').map(Number);

          const dataVal = data._time ? dayjs(data._time).unix() : null;
          if (dataVal && before <= dataVal && dataVal <= after) {
            xIndex = idx; // xIndex를 설정
          }
        });

        let yIndex = -1; // 초기값을 -1로 설정
        yData.forEach((yd, idx) => {
          const [before, after] = yd.split('~').map(Number);
          const dataVal = Math.round(data._value * 1000);
          if (before <= dataVal && dataVal <= after) {
            yIndex = idx; // yIndex를 설정
          }
        });
        return [xIndex, yIndex, 1];
      });
      return heatDatas;
    },
    [result.data, result.isSuccess]
  );

  // x축과 y축의 value 중복 카운트 세기
  const countHeatDatas = (dataList: number[][]) => {
    const result: { [key: string]: number } = {};
    const newList: number[][] = [];
    dataList.forEach(item => {
      const index = `${item[0]},${item[1]}`;
      if (item[2]) {
        // newList.push([item[0]])
        result[index] = (result[index] || 0) + item[2];
      }
    });
    Object.keys(result).map(obj => {
      const [xx, yy] = obj.split(',');

      const xtime = Number(xx);
      const yValue = Number(yy);
      if (typeof xtime === 'number' && typeof yValue === 'number') {
        newList.push([xtime, yValue, result[obj]]);
      }
    });

    return newList;
  };

  useEffect(() => {
    const xData = getXAxis();

    const yData = getYAxis();
    const data = getHeatMapDatas(xData, yData);
    if (data) {
      const count = countHeatDatas(data);
      setValue(count);
    }
    xData
      ? setX(
          xData.map(value => {
            const [before, after] = String(value).split('~');
            return `${dayjs.unix(Number(before)).format('YYYY-MM-DD HH:mm:ss')} \n ~ ${dayjs
              .unix(Number(after))
              .format('YYYY-MM-DD HH:mm:ss')}`;
          })
        )
      : null;
    yData ? setY(yData) : null;
  }, [getHeatMapDatas, getXAxis, getYAxis]);

  if (value.length === 0) {
    return (
      <MainCard title="Net 응답 시간 추이(ms) - 10m" content={true}>
        <div>데이터가 없습니다.</div>
      </MainCard>
    );
  }
  return (
    <MainCard title="Net 응답 시간 추이(ms) - 10m" content={true}>
      <HeatMapComp loading={false} xAxis={x} yAxis={y} data={value} />
    </MainCard>
  );
}
