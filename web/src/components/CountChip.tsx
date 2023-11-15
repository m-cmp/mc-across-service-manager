import Avatar from '@mui/material/Avatar';
import Paper from '@mui/material/Paper';
import styled from 'styled-components';

const CntChip = styled(Paper)`
  min-height: 25px;
  align-self: center;
  display: inline-flex;
  min-width: 90%;
  justify-content: space-between;
  padding: 3px;
  padding-left: 15px;
  padding-right: 15px;
  margin-bottom: 3px;
  border-radius: 20px !important;
  font-size: 12px;
`;

const StyledP = styled.p`
  margin-right: 1px;
  align-self: center;
`;

interface Props {
  label: string;
  count: number;
}
export default function CountChip({ label, count }: Props) {
  return (
    <CntChip elevation={2}>
      <StyledP>{label}</StyledP>
      <Avatar sx={{ width: 24, height: 23, background: '#346677' }}>{count}</Avatar>
    </CntChip>
  );
}
