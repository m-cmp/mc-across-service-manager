import { Chip } from '@mui/material';

/**
 * Common Chip Y/N rendering
 * @param MUI DataGrid의 rendercell의 param.row.필드 데이터
 * @returns <Chip>
 */
const CommonChip = (param: string | number) => {
  const chipColor = param === 'N' ? 'default' : 'primary';
  return <Chip label={param} color={chipColor} />;
};

export default CommonChip;
