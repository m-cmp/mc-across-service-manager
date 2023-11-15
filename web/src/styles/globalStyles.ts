import { createGlobalStyle } from 'styled-components';
import { reset } from 'styled-reset';

const GlobalStyle = createGlobalStyle`
  ${reset}

  * {
    /* box-sizing: border-box; */
  }

  html, body { 
    width: 100%;
    height: 100vh;
    background-color: #ffffff;
    font-family: Roboto, sans-serif;
    font-size : 14px;
  }

  ul, ol {
    list-style: none;
  }
`;

export default GlobalStyle;
