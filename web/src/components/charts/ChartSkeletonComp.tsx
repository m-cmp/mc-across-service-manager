import { Avatar, Skeleton } from '@mui/material';

const ChartSkeleton = () => {
  return (
    <Skeleton variant="circular">
      <Avatar />
    </Skeleton>
  );
};

export default ChartSkeleton;
