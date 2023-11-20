import { init } from 'echarts';
import type { EChartsOption, ECharts, SetOptionOpts } from 'echarts';
import type { CSSProperties } from 'react';
import { useEffect, useRef, useState } from 'react';
import styled from 'styled-components';

export interface ReactEChartsProps {
  option?: EChartsOption;
  style?: CSSProperties;
  settings?: SetOptionOpts;
  loading?: boolean;
  theme?: 'light' | 'dark';
  dataset?: object;
  // series?: SeriesOption[];
  data: number;
  xAxis?: string[];
}

const Gauge = styled.div`
  min-height: 200px;
  min-width: 200px;
  width: 100%;
  height: 100%;
`;

export default function GaugeChartComp({ settings, data }: ReactEChartsProps): JSX.Element {
  const chartRef = useRef<HTMLDivElement>(null);
  const [option, setOption] = useState<EChartsOption>({});

  useEffect(() => {
    let color;
    if (data <= 70) {
      color = '#2626cf';
    } else if (data <= 90) {
      color = '#ecd344';
    } else {
      color = '#ff2c2c';
    }
    setOption({
      grid: {
        width: '100%',
        height: '100%',
        top: 0,
        bottom: 0,
        left: 0,
        right: 0,
      },
      series: [
        {
          type: 'gauge',
          progress: {
            show: true,
            width: 10,
            itemStyle: {
              color: color,
            },
          },
          axisLine: {
            lineStyle: {
              width: 10,
              color: [
                [0.7, '#a9ccc6'],
                [0.9, '#ecd344'],
                [1, '#ff2c2c'],
              ],
            },
          },
          axisTick: {
            show: false,
          },
          splitLine: {
            length: 5,
            lineStyle: {
              width: 1,
              color: '#999',
            },
          },
          axisLabel: {
            distance: 14,
            color: '#999',
            fontSize: 10,
          },
          anchor: {
            show: true,
            showAbove: true,
            size: 5,
            itemStyle: {
              borderWidth: 10,
            },
          },
          title: {
            show: false,
          },
          detail: {
            valueAnimation: true,
            fontSize: 14,
            offsetCenter: [0, '30%'],
          },
          data: [
            {
              value: data,
            },
          ],
        },
      ],
    });
  }, [data]);

  const drawChart = () => {
    let chart: ECharts | undefined;
    if (chartRef.current) {
      chart = init(chartRef.current, 'lignt');
    }
    return chart;
  };

  useEffect(() => {
    const chart = drawChart();
    if (chart) {
      chart.setOption(option, settings);
    }
    // 차트 리사이즈 이벤트
    function resizeChart() {
      chart?.resize();
    }
    window.addEventListener('resize', resizeChart);

    // 이벤트 클린업
    return () => {
      chart?.dispose();
      window.removeEventListener('resize', resizeChart);
    };
  }, [option, settings]);

  return <Gauge ref={chartRef} />;
}
