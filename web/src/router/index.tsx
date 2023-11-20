import { createBrowserRouter } from 'react-router-dom';
import Main from '../pages/main/MainView';
import AcrossServiceDetailView from '@/pages/acrossService/AcrossServiceDetailView';
import AcrossServiceList from '@/pages/acrossService/AcrossServiceListView';
import BasicLayout from '@/pages/BasicLayout';
import Guide from '@/pages/guide/Guide';
import InstanceList from '@/pages/instance/InstanceListView';
import ServiceDetailView from '@/pages/service/ServiceDetailView';
import ServiceListView from '@/pages/service/ServiceListView';
import TemplateList from '@/pages/template/TemplateListView';
import VPCList from '@/pages/vpc/VPCListView';

const router = createBrowserRouter([
  // 메인
  {
    path: '/',
    element: <BasicLayout />,
    children: [
      {
        path: '/',
        element: <Main />,
      },
      {
        path: 'template',
        element: <TemplateList />,
      },
      //연계 서비스 목록 페이지
      {
        path: 'across-service',
        element: <AcrossServiceList />,
      },
      //연계 서비스 상세 페이지
      {
        path: 'across-service/:id',
        element: <AcrossServiceDetailView />,
      },
      //(단일) 서비스 목록 페이지
      {
        path: 'service',
        element: <ServiceListView />,
      },
      //(단일) 서비스 상세 페이지
      {
        path: 'service/:id',
        element: <ServiceDetailView />,
      },
      {
        path: 'instance',
        element: <InstanceList />,
      },
      {
        path: 'vpc',
        element: <VPCList />,
      },

      {
        path: 'guide',
        element: <Guide />,
      },
    ],
  },
]);

export default router;
