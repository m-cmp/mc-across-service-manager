import MenuIcon from '@mui/icons-material/Menu';
import MuiAppBar, { AppBarProps as MuiAppBarProps } from '@mui/material/AppBar';
import IconButton from '@mui/material/IconButton';
import { styled } from '@mui/material/styles';
import Toolbar from '@mui/material/Toolbar';
import { useNavigate } from 'react-router-dom';

const drawerWidth = 240;

const AppBar = styled(MuiAppBar, {
  shouldForwardProp: prop => prop !== 'open',
})<AppBarProps>(({ theme, open }) => ({
  zIndex: theme.zIndex.drawer + 1,
  transition: theme.transitions.create(['width', 'margin'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  ...(open && {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  }),
}));

interface AppBarProps extends MuiAppBarProps {
  open?: boolean;
}

interface Props {
  open: boolean;
  onToggleDrawer: (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
}

const Header = ({ open, onToggleDrawer }: Props) => {
  const navigate = useNavigate();
  return (
    <AppBar position="absolute" open={open}>
      <Toolbar
        sx={{
          pr: '24px', // keep right padding when drawer closed
        }}
      >
        <IconButton
          edge="start"
          color="inherit"
          aria-label="open drawer"
          onClick={onToggleDrawer}
          sx={{
            marginRight: '36px',
            ...(open && { display: 'none' }),
          }}
        >
          <MenuIcon />
        </IconButton>
        {/* <Typography component="h1" variant="h6" color="inherit" noWrap sx={{ flexGrow: 1 }}>
          M-CMP
        </Typography> */}
        <IconButton
          color="inherit"
          aria-label="button-main"
          sx={{ minWidth: 40, minHeight: 40 }}
          onClick={() => navigate('/')}
        >
          {/* <Icon sx={{ maxHeight: 20, minHeight: 30, minWidth: 40 }}>
          </Icon> */}
          <img src="m-cmp-icon-nolabel.png" alt="logo" style={{ width: 38, height: '100%', padding: 0, margin: 0 }} />
          {/* <image href="../../../public/m-cmp-icon.png" /> */}
        </IconButton>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
