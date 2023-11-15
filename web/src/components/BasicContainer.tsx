import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import { Outlet } from 'react-router-dom';

const BasicContainer = () => {
  return (
    <Box
      component="main"
      sx={{
        backgroundColor: theme => (theme.palette.mode === 'light' ? theme.palette.grey[100] : theme.palette.grey[900]),
        height: '100vh',
        width: '100%',
      }}
    >
      <Toolbar />
      <Box
        component="main"
        sx={{ overflowY: 'scroll', width: '100%', height: 'calc(100vh - 64px)', flexGrow: 1, p: { xs: 2, sm: 3 } }}
      >
        {/* <Breadcrumbs navigation={navigation} title /> */}
        <Outlet />
      </Box>
    </Box>
  );
};

export default BasicContainer;
