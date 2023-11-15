import WidgetComp from '@components/WidgetComp';
// import FaceIcon from '@mui/icons-material/Face';
import { useEffect, useState } from 'react';
import { useGetFieldCntByIns, useGetSystemUptimeByIns } from '@/network/apis/useMonitoringAcrossService';

interface Props {
  title: string;
  instanceName: string;
  type?: 'total' | 'total_threads';
}

export default function ProcessCnt({ title, instanceName, type }: Props) {
  const fieldCnt = useGetFieldCntByIns(instanceName, 1, type);
  const uptime = useGetSystemUptimeByIns(instanceName);

  const [data, setData] = useState<number | string>(0);

  useEffect(() => {
    if (type && fieldCnt.isSuccess) {
      if (fieldCnt.data.length > 0 && typeof fieldCnt.data[0]._value === 'number') {
        setData(Math.round(fieldCnt.data[0]._value));
      } else {
        setData(0);
      }
    } else if (uptime.isSuccess && uptime.data.length > 0) {
      if (uptime.data.length <= 0) {
        setData(0);
      } else {
        setData(uptime.data[0]._value);
      }
    }
  }, [fieldCnt.data, fieldCnt.isSuccess, type, uptime.data, uptime.isSuccess]);

  return <WidgetComp title={title} data={data} />;
}
