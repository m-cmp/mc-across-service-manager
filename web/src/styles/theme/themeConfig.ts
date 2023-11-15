import { PaletteMode } from '@mui/material';
import { createTheme } from '@mui/material/styles';
import styled from 'styled-components';

const subtitle1 = styled.p`
  font-weight: 600 !important;
  font-size: 14px;
`;

export const theme = (mode: PaletteMode) =>
  createTheme({
    components: {
      MuiTypography: {
        defaultProps: {
          variantMapping: {
            h1: 'h1',
            h2: 'h2',
            h3: 'h3',
            h4: 'h4',
            h5: 'h5',
            h6: 'h6',
            subtitle1: subtitle1,
            subtitle2: 'h8',
            body1: 'p',
            body2: 'p',
            inherit: 'p',
          },
        },
      },
      MuiTablePagination: {
        styleOverrides: {
          root: {
            margin: '0 20px',
            borderRadius: 4,
            '&:last-child': {
              padding: '20px 0',
            },
          },
          spacer: {
            flex: '1 1 100%',
          },
        },
      },
    },
    palette: {
      mode,
      // light일 경우
      ...(mode === 'light'
        ? {
            primary: {
              main: '#346677',
            },
            secondary: {
              main: '#00b0ff',
            },
            error: {
              main: '#e53935',
            },
            warning: {
              main: '#ff7043',
            },
            info: {
              main: '#4fc3f7',
            },
            success: {
              main: '#47bd4b',
            },
          }
        : {
            primary: {
              main: '#42c5f5',
            },
            secondary: {
              main: '#5c6bc0',
            },
            error: {
              main: '#e53935',
            },
            warning: {
              main: '#ff7043',
            },
            info: {
              main: '#4fc3f7',
            },
            success: {
              main: '#47bd4b',
            },
          }),
    },
  });
