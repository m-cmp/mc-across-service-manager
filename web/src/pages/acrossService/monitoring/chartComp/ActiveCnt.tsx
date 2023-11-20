import WidgetComp from '@components/WidgetComp';
import { useEffect, useState } from 'react';
import { useGetActiveByInstance } from '@/network/apis/useMonitoringAcrossService';

interface Props {
  title: string;
  instanceName: string;
}

export default function ActiveCnt({ title, instanceName }: Props) {
  const result = useGetActiveByInstance(instanceName);

  const [data, setData] = useState<number | string>(0);

  useEffect(() => {
    if (result.isSuccess) {
      if (result.data.length > 0) {
        setData(result.data[0]._value);
      } else {
        setData(0);
      }
    }
  }, [result.data, result.isSuccess]);

  return <WidgetComp title={title} data={data} />;
}
