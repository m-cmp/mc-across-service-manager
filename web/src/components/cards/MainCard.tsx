import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardHeader from '@mui/material/CardHeader';
import Divider from '@mui/material/Divider';
import styled from 'styled-components';

interface IMainCard {
  // border: boolean;
  // boxShadow: boolean;
  children?: JSX.Element | JSX.Element[];
  content: boolean;
  // contentClass: string;
  contentSX?: object;
  // darkTitle: boolean;
  // secondary: Element | string | object;
  // shadow: string;
  // sx: object;
  // title: Element | string | object;
  title: string;
}
const Content = styled(CardContent)`
  width: 100%;
  height: 100%;
  /* min-height: 300px; */
`;

const MainCard = (props: IMainCard) => {
  return (
    <Card
      sx={{
        ':hover': {
          boxShadow: '0 2px 14px 0 rgb(32 40 45 / 8%)',
        },
        overflow: 'visible',
      }}
    >
      {/* card header and action */}
      {props.title && <CardHeader titleTypographyProps={{ fontWeight: 600, fontSize: 14 }} title={props.title} />}

      {/* content & header divider */}
      {props.title && <Divider />}

      {/* card content */}
      {props.content && <Content>{props.children}</Content>}
      {!props.content && props.children}
    </Card>
  );
};

export default MainCard;
