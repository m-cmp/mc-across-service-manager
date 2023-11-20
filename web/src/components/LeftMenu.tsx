import AssignmentTurnedIn from '@mui/icons-material/AssignmentTurnedIn';
import CallSplitIcon from '@mui/icons-material/CallSplit';
import Category from '@mui/icons-material/Category';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import DnsIcon from '@mui/icons-material/Dns';
import Sensors from '@mui/icons-material/Sensors';
import Divider from '@mui/material/Divider';
import MuiDrawer from '@mui/material/Drawer';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
// import ListSubheader from '@mui/material/ListSubheader';
import { styled } from '@mui/material/styles';
import Toolbar from '@mui/material/Toolbar';
import { ReactJSXElement } from 'node_modules/@emotion/react/types/jsx-namespace';
import { useNavigate } from 'react-router-dom';

const drawerWidth = 240;

const Drawer = styled(MuiDrawer, { shouldForwardProp: prop => prop !== 'open' })(({ theme, open }) => ({
  '& .MuiDrawer-paper': {
    position: 'relative',
    whiteSpace: 'nowrap',
    width: drawerWidth,
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
    boxSizing: 'border-box',
    ...(!open && {
      overflowX: 'hidden',
      transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
      }),
      width: theme.spacing(7),
      [theme.breakpoints.up('sm')]: {
        width: theme.spacing(9),
      },
    }),
  },
}));

interface Props {
  open: boolean;
  onToggleDrawer: (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
}

interface Menu {
  icon: ReactJSXElement;
  text: string;
  route: string;
}

const menuItems: Menu[] = [
  {
    icon: <DnsIcon />,
    text: '인스턴스',
    route: '/instance',
  },
  {
    icon: <AssignmentTurnedIn />,
    text: '서비스',
    route: '/service',
  },
  {
    icon: <CallSplitIcon />,
    text: '연계 서비스',
    route: '/across-service',
  },
  {
    icon: <Category />,
    text: '카탈로그',
    route: '/template',
  },
  {
    icon: <Sensors />,
    text: 'VPC',
    route: '/vpc',
  },
];

const LeftMenu = ({ open, onToggleDrawer }: Props) => {
  const navigate = useNavigate();

  const renderListItemButton = (menuItem: Menu) => {
    return (
      <ListItemButton key={menuItem.text} onClick={() => navigate(menuItem.route)}>
        {menuItem.icon && <ListItemIcon>{menuItem.icon}</ListItemIcon>}
        <ListItemText primary={menuItem.text} sx={{ fontWeight: 900 }} />
      </ListItemButton>
    );
  };

  return (
    <Drawer variant="permanent" open={open}>
      {/* Tool Bar */}
      <Toolbar
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'flex-end',
          px: [1],
        }}
      >
        <IconButton onClick={onToggleDrawer}>
          <ChevronLeftIcon />
        </IconButton>
      </Toolbar>
      <Divider />

      {/* Menu */}
      <List component="nav">
        {/* Main Menu */}
        {menuItems.map(renderListItemButton)}

        <Divider sx={{ my: 1 }} />

        {/* Sub Menu - 필요 없으면 삭제 예정*/}
        {/* <ListSubheader component="div" inset>
          서브 메뉴
        </ListSubheader>
        <ListItemButton>
          <ListItemIcon>
            <DashboardIcon />
          </ListItemIcon>
          <ListItemText primary="VPC" />
        </ListItemButton> */}
      </List>
    </Drawer>
  );
};

export default LeftMenu;
