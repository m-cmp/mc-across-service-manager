//   * Line Chart Component
//   참고 : https://echarts.apache.org/examples/en/index.html
import { init } from 'echarts';
import type { EChartsOption, ECharts, SetOptionOpts } from 'echarts';
import type { CSSProperties } from 'react';
import { useEffect, useRef, useState } from 'react';
import styled from 'styled-components';

export interface PizzaData {
  value: number;
  name: string;
}

export interface ReactEChartsProps {
  option?: EChartsOption;
  style?: CSSProperties;
  settings?: SetOptionOpts;
  loading?: boolean;
  theme?: 'light' | 'dark';
  dataset?: object;
  data: PizzaData[];
  xAxis?: string[];
}

const Gauge = styled.div`
  min-height: 200px;
  min-width: 200px;
  width: 100%;
  height: 100%;
`;

export default function PizzaChart({ settings, data }: ReactEChartsProps): JSX.Element {
  const chartRef = useRef<HTMLDivElement>(null);
  const [option, setOption] = useState<EChartsOption>({});

  useEffect(() => {
    setOption({
      tooltip: {
        trigger: 'item',
      },
      legend: {
        top: '0%',
        left: 'center',
      },
      series: [
        {
          // name: 'Access From',
          type: 'pie',
          radius: '50%',
          data: data,
          labelLine: {
            show: false,
          },
          itemStyle: {
            borderRadius: 5,
            borderColor: '#fff',
            borderWidth: 3,
          },
          // data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
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

  // Update chart
  // useEffect(() => {
  //   if (chartRef.current) {
  //     const chart = getInstanceByDom(chartRef.current);
  //     // loading ? chart?.showLoading() : chart?.hideLoading();
  //   }
  // }, []);

  return <Gauge ref={chartRef} />;
}
