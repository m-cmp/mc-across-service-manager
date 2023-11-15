import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider } from '@mui/material/styles';
import GlobalStyle from '@styles/globalStyles';
import { SnackbarProvider } from 'notistack';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import { theme } from './styles/theme/themeConfig';
import '@styles/fonts';

const container = document.getElementById('root');
const root = createRoot(container!); // createRoot(container!) if you use TypeScript

// dark모드 구현 필요
// https://mui.com/material-ui/customization/dark-mode/#dark-mode-with-a-custom-palette

root.render(
  <StrictMode>
    <ThemeProvider theme={theme('light')}>
      <SnackbarProvider maxSnack={5}>
        <CssBaseline />
        <GlobalStyle />
        <App />
      </SnackbarProvider>
    </ThemeProvider>
  </StrictMode>
);
