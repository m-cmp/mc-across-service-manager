import WidgetComp from '@components/WidgetComp';
import { useEffect, useState } from 'react';
import { useGetTpsByInstance } from '@/network/apis/useMonitoringAcrossService';

interface Props {
  title: string;
  instanceName: string;
}

export default function TPSCnt({ title, instanceName }: Props) {
  const result = useGetTpsByInstance(instanceName);

  const [data, setData] = useState<number | string>(0);

  useEffect(() => {
    if (result.isSuccess) {
      const datas = result.data.flatMap(f => {
        if (f._value) {
          return f._value;
        }
      });
      if (datas.length > 0) {
        setData(Number(result.data[0]._value) / 10);
      } else {
        setData(0);
      }
    }
  }, [result.data, result.isSuccess]);

  return <WidgetComp title={title} data={data} />;
}
