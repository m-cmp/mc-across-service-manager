//   * Line Chart Component
//   참고 : https://echarts.apache.org/examples/en/index.html
import { init, getInstanceByDom } from 'echarts';
import type { EChartsOption, ECharts, SetOptionOpts, SeriesOption } from 'echarts';
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
  series?: SeriesOption[];
  xAxis?: string[];
  isLegendShow?: boolean;
}

export interface ISeries {
  name: string;
  type: string;
  data: number[];
}

const Line = styled.div`
  min-height: 200px;
  min-width: 250px;
  width: 100%;
  height: 100%;
`;

export default function LineChartComp({
  settings,
  loading,
  series,
  xAxis,
  isLegendShow,
}: ReactEChartsProps): JSX.Element {
  const chartRef = useRef<HTMLDivElement>(null);
  const [option, setOption] = useState<EChartsOption>({});

  useEffect(() => {
    setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          label: {
            backgroundColor: '#6a7985',
          },
        },
      },
      legend: {
        show: isLegendShow === undefined ? true : isLegendShow,
        type: 'scroll',
        textStyle: {
          color: 'gray',
        },
      },
      rader: { center: ['50%', '50%'] },
      grid: {
        // show: false,
        top: isLegendShow === false ? '3%' : '20%',
        left: '3%',
        right: '3%',
        bottom: '0%',
        containLabel: true,
      },
      // X축
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: xAxis,
        axisLabel: {
          formatter: (value: string | undefined) => {
            if (value && value.length > 10) {
              return `${value.substring(0, 10)} 
${value.substring(10, value.length)}`;
            }
            return '';
          },
        },
      },
      // Y축
      yAxis: {
        type: 'value',
        min: value => {
          return value.min;
        },
        max: value => {
          return value.max;
        },
        // show: true,
      },
      // 데이터들
      series: series,
    });
  }, [isLegendShow, series, xAxis]);

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
  }, [option, settings, series]);

  // Update chart
  useEffect(() => {
    if (chartRef.current) {
      const chart = getInstanceByDom(chartRef.current);
      loading ? chart?.showLoading() : chart?.hideLoading();
    }
  }, [loading]);

  return <Line ref={chartRef} />;
}
