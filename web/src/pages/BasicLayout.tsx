import Box from '@mui/material/Box';
import { useState } from 'react';
import BasicContainer from '@/components/BasicContainer';
import Header from '@/components/Header';
import LeftMenu from '@/components/LeftMenu';

function BasicLayout() {
  const [open, setOpen] = useState(true);

  const onToggleDrawer = () => {
    setOpen(!open);
  };

  return (
    <>
      <Box
        sx={{
          display: 'flex',
          height: '100vh',
        }}
      >
        <Header open={open} onToggleDrawer={onToggleDrawer} />
        <LeftMenu open={open} onToggleDrawer={onToggleDrawer} />
        <BasicContainer />
      </Box>
    </>
  );
}

export default BasicLayout;
