import { setEnqueueSnackbar } from '@components/snackBar/snackbarController';

import { useSnackbar } from 'notistack';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';
import { RouterProvider } from 'react-router-dom';
import { RecoilRoot } from 'recoil';
import router from './router';

const queryClient = new QueryClient();

const InnerComponent: React.FC = () => {
  const { enqueueSnackbar } = useSnackbar();
  setEnqueueSnackbar(enqueueSnackbar);

  return <></>;
};

function App() {
  return (
    <>
      {/* recoil 설정 */}
      <RecoilRoot>
        {/* react-query 설정 */}
        <QueryClientProvider client={queryClient}>
          {/* react-query devtools */}
          <ReactQueryDevtools initialIsOpen={false} />
          <RouterProvider router={router} />
          <InnerComponent />
        </QueryClientProvider>
      </RecoilRoot>
    </>
  );
}

export default App;
