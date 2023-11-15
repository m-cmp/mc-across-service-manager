import { init } from 'echarts';
import type { EChartsOption, ECharts, SetOptionOpts } from 'echarts';
import { GraphSeriesOption } from 'echarts/charts';
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
  series?: GraphSeriesOption[];
  data: GraphData[];
  links: GraphLink[];
  xAxis?: string[];
}
export interface GraphLink {
  source: string | number;
  target: string | number;
  label?: {
    show: boolean;
  };
  lineStyle?: {
    type?: 'solid' | 'dashed' | 'dotted';
    curveness?: number;
    width?: number;
    color?: string;
  };
}

export interface GraphData {
  id: string;
  name: string;
  x: number;
  y: number;
  value?: number | string;
  category?: string;
  symbolSize?: number[];
  itemStyle?: object;
}

const Gauge = styled.div`
  min-height: 200px;
  min-width: 200px;
  width: 100%;
  height: 100%;
`;

export default function GraphChart({ settings, data, links }: ReactEChartsProps): JSX.Element {
  const chartRef = useRef<HTMLDivElement>(null);
  const [option, setOption] = useState<EChartsOption>({});

  useEffect(() => {
    setOption({
      tooltip: { show: true },
      grid: {
        top: '3%',
        height: '100%',
        width: '100$',
        bottom: 0,
      },
      series: [
        {
          type: 'graph',
          // layout: 'none',
          symbol: 'roundRect',
          symbolSize: [40, 40],
          label: {
            show: true,
            fontSize: 12,
            fontWeight: 900,
          },
          edgeSymbol: ['none', 'arrow'],
          edgeSymbolSize: [1, 10],
          edgeLabel: {
            fontSize: 12,
          },
          data: data,
          links: links,
        },
      ],
    });
  }, [data, links]);

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
