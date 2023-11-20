import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

interface WidgetProps {
  color?: string;
  icon?: JSX.Element;
  sx?: object;
  title: string;
  data?: number | string;
  children?: JSX.Element | JSX.Element[];
}

export default function Widget({ title, data, icon, sx, children, ...other }: WidgetProps) {
  return (
    <Card
      component={Stack}
      spacing={1}
      direction="row"
      sx={{
        px: 2,
        py: 2,
        borderRadius: 2,
        ...sx,
      }}
      {...other}
    >
      {/* Simple Widget */}
      {icon && <Box sx={{ width: 35, height: '100%', alignSelf: 'center' }}>{icon}</Box>}

      {data !== undefined && data !== null ? (
        <Stack spacing={0.5}>
          <Typography variant="subtitle1">{data}</Typography>

          <Typography variant="subtitle1" sx={{ color: 'text.disabled', fontSize: 12 }}>
            {title}
          </Typography>
        </Stack>
      ) : (
        <Stack spacing={0.5}>
          <Typography variant="subtitle1">{title}</Typography>
          {children && children}
        </Stack>
      )}
    </Card>
  );
}
