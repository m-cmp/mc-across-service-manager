//   * Line Chart Component
//   참고 : https://echarts.apache.org/examples/en/index.html
import { init, getInstanceByDom } from 'echarts';
import type { EChartsOption, ECharts, SetOptionOpts, HeatmapSeriesOption } from 'echarts';
import type { CSSProperties } from 'react';
import { useEffect, useRef, useState } from 'react';
import styled from 'styled-components';

export interface ReactEChartsProps {
  option?: HeatmapSeriesOption;
  style?: CSSProperties;
  settings?: SetOptionOpts;
  loading?: boolean;
  theme?: 'light' | 'dark';
  dataset?: object;
  data?: number[][];
  xAxis?: string[];
  yAxis?: string[];
}

const Line = styled.div`
  min-height: 200px;
  min-width: 250px;
  width: 100%;
  height: 100%;
`;

export default function HeatMapComp({ settings, loading, data, xAxis, yAxis }: ReactEChartsProps): JSX.Element {
  const chartRef = useRef<HTMLDivElement>(null);
  const [option, setOption] = useState<EChartsOption>({});

  useEffect(() => {
    setOption({
      tooltip: {
        trigger: 'item',
      },

      grid: {
        height: '80%',
        width: '100%',
        top: '3%',
        bottom: '3%',
      },
      xAxis: {
        type: 'category',
        data: xAxis,
        splitArea: {
          show: true,
        },
      },
      legend: {
        type: 'scroll',
        textStyle: {
          color: 'gray',
        },
        pageTextStyle: {
          color: 'gray',
        },
      },
      // Y축
      yAxis: {
        type: 'category',
        min: value => {
          return value.min;
        },
        max: value => {
          return value.max;
        },
        splitArea: {
          show: true,
        },
        data: yAxis,
        // show: true,
      },
      visualMap: {
        min: 0,
        max: 10,
        show: false,
        bottom: '0%',
      },
      series: [
        {
          type: 'heatmap',
          data: data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    });
  }, [data, xAxis, yAxis]);

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
  useEffect(() => {
    if (chartRef.current) {
      const chart = getInstanceByDom(chartRef.current);
      loading ? chart?.showLoading() : chart?.hideLoading();
    }
  }, [loading]);

  return <Line ref={chartRef} />;
}
