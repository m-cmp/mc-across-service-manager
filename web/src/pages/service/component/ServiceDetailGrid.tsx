import { Button } from '@mui/material';
import { GridColDef, GridRowId, GridRowParams } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import CommonChip from '@/components/datagrid/CommonChip';
import CommonDataGrid from '@/components/datagrid/CommonDataGrid';
import { displaySnackbar } from '@/components/snackBar/snackbarController';
import {
  ActivateAgentData,
  ActivateServiceData,
  ServiceDetailData,
  useActivateAgent,
  useActivateService,
  useDeployAgent,
} from '@/network/apis/useServiceDetail';

interface ServiceDetailProps {
  // serviceId: string;
  serviceId: number;
  data: ServiceDetailData[];
}

export default function ServiceDetailGrid(props: ServiceDetailProps) {
  const [selectionModel, setSelectionModel] = useState<GridRowId[]>([]); //체크된 체크박스 list : number[]

  const [serviceId, setServiceId] = useState(props.serviceId); // 서비스 id
  const [data, setData] = useState(props.data); // 서비스 상세 조회 데이터

  //Agent 배포 진행 상태
  const [isRefetchingDeployAgent, setIsRefetchingAgentDeploy] = useState(false);
  const [isRefetchingActivateAgent, setIsRefetchingActivateAgent] = useState(false);
  const [isRefetchingActivateService, setIsRefetchingActivateService] = useState(false);

  // Agent 활성화 Request body
  const [activateAgentParams, setActivateAgentParams] = useState<ActivateAgentData>({
    service_instance_id: props.data[0]?.serviceInstanceId,
    agent_activate_yn: props.data[0]?.agentActivateYn === 'Y' ? 'N' : 'Y',
  });
  // 서비스 활성화 Request body
  const [activateServiceParams, setActivateServiceParams] = useState<ActivateServiceData>({
    application_id: props.data[0]?.applicationId,
    application_activate_yn: props.data[0]?.applicationActivateYn === 'Y' ? 'N' : 'Y',
    vm_instance_public_ip: props.data[0]?.vmInstancePublicIp,
  });

  useEffect(() => {
    setServiceId(props.serviceId);
    setData(props.data);
    setActivateAgentParams({
      service_instance_id: props.data[0]?.serviceInstanceId,
      agent_activate_yn: props.data[0]?.agentActivateYn === 'Y' ? 'N' : 'Y',
    });
    setActivateServiceParams({
      application_id: props.data[0]?.applicationId,
      application_activate_yn: props.data[0]?.applicationActivateYn === 'Y' ? 'N' : 'Y',
      vm_instance_public_ip: props.data[0]?.vmInstancePublicIp,
    });
  }, [props.data, props.serviceId]);

  // Agent 배포 API
  const deployAgentQuery = useDeployAgent(serviceId);

  // Agent 활성화 API
  const activateAgentQuery = useActivateAgent(serviceId, activateAgentParams);

  // 서비스(APP) 활성화 API
  const activateServiceQuery = useActivateService(serviceId, activateServiceParams);

  // Agent 배포 버튼 클릭 이벤트
  const handleAgentDeployButtonClick = () => {
    // deployAgentQuery.refetch();
    if (!isRefetchingDeployAgent) {
      setIsRefetchingAgentDeploy(true);
      deployAgentQuery.refetch().finally(() => setIsRefetchingAgentDeploy(false));
    } else {
      displaySnackbar('이미 처리 중입니다.', 'info', null);
    }
  };

  // Agent 활성화/비활성화 버튼 클릭 이벤트
  const handleAgentActivateButtonClick = () => {
    // activateAgentQuery.refetch();
    if (!isRefetchingActivateAgent) {
      setIsRefetchingActivateAgent(true);
      activateAgentQuery.refetch().finally(() => setIsRefetchingActivateAgent(false));
    } else {
      displaySnackbar('이미 처리 중입니다.', 'info', null);
    }
  };

  // 서비스(APP) 활성화/비활성화 버튼 클릭 이벤트
  const handleServiceActivateButtonClick = () => {
    // activateServiceQuery.refetch();
    if (!isRefetchingActivateService) {
      setIsRefetchingActivateService(true);
      activateServiceQuery.refetch().finally(() => setIsRefetchingActivateService(false));
    } else {
      displaySnackbar('이미 처리 중입니다.', 'info', null);
    }
  };

  //#region DataGrid
  const gridColList: GridColDef[] = [
    {
      field: 'vmInstanceId',
      headerName: 'VM ID',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 1.3,
    },
    {
      field: 'vmInstanceStatus',
      headerName: 'VM 상태',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 1,
    },
    {
      field: 'agentDeployYn',
      headerName: 'Agent 배포 여부',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 1.3,
      renderCell: params => CommonChip(params.row.agentDeployYn),
    },
    {
      field: 'agentActivateYn',
      headerName: 'Agent 상태',
      sortable: false,
      width: 100,
      flex: 1.2,
      headerAlign: 'center',
      align: 'center',
      renderCell: params => CommonChip(params.row.agentActivateYn),
    },
    {
      field: 'agentDeploy',
      headerName: 'Agent 배포',
      width: 100,
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      renderCell: params => {
        if (params.row.agentDeployYn === 'N') {
          return (
            <Button
              onClick={event => {
                event.stopPropagation();
                //Agent 배포 API
                handleAgentDeployButtonClick();
              }}
              variant="contained"
              fullWidth
            >
              DEPLOY
            </Button>
          );
        } else if (params.row.agentDeployYn === 'Y') {
          return (
            <Button disabled variant="contained" fullWidth>
              DEPLOY
            </Button>
          );
        }
      },
    },
    {
      field: 'agentActivate',
      headerName: 'Agent 활성화',
      headerAlign: 'center',
      align: 'center',
      flex: 1.3,
      renderCell: params => {
        if (params.row.agentDeployYn === 'Y') {
          if (params.row.agentActivateYn === 'Y') {
            return (
              <Button
                onClick={event => {
                  event.stopPropagation();
                  console.log('Agent 비활성화 button clicked:', params.row);
                  //Agent 비활성화 API
                  handleAgentActivateButtonClick();
                }}
                variant="contained"
                color="error"
                fullWidth
              >
                DEACTIVATE
              </Button>
            );
          } else {
            return (
              <Button
                onClick={event => {
                  event.stopPropagation();
                  console.log('Agent 활성화 button clicked:', params.row);
                  //Agent 활성화 API
                  handleAgentActivateButtonClick();
                }}
                variant="contained"
                fullWidth
              >
                ACTIVATE
              </Button>
            );
          }
        } else {
          <Button disabled variant="contained" fullWidth>
            ACTIVATE
          </Button>;
        }
      },
    },
    {
      field: 'applicationName',
      headerName: 'APP',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 1.5,
    },
    {
      field: 'applicationType',
      headerName: 'APP 타입',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 1.5,
    },
    {
      field: 'applicationActivateYn',
      headerName: '서비스 활성화 여부',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 1.5,
      renderCell: params => (params.row.applicationActivateYn ? CommonChip(params.row.applicationActivateYn) : null),
    },
    {
      field: 'appActivate',
      headerName: '서비스 활성화',
      width: 100,
      hideable: true,
      flex: 1.2,
      renderCell: params => {
        if (params.row.applicationId) {
          if (params.row.applicationActivateYn === 'Y') {
            return (
              <Button
                onClick={event => {
                  event.stopPropagation();
                  //서비스(APP) 비활성화 API Request
                  handleServiceActivateButtonClick();
                }}
                color="error"
                variant="contained"
                fullWidth
              >
                DEACTIVATE
              </Button>
            );
          } else {
            return (
              <Button
                onClick={event => {
                  event.stopPropagation();
                  //서비스(APP) 활성화 API Request
                  handleServiceActivateButtonClick();
                }}
                variant="contained"
                fullWidth
              >
                ACTIVATE
              </Button>
            );
          }
        }
      },
    },
  ];

  const getRowId = (row: { vmInstanceId: string }) => {
    return row.vmInstanceId;
  };

  //Row 클릭 이벤트
  const handleGridRowClick = (params: GridRowParams) => {
    console.log(params.id);
    console.log(params.row);
  };

  //체크박스 클릭 이벤트
  const handleCheckboxChange = (selectionModel: GridRowId[]) => {
    console.log('Selected Rows:', selectionModel);
    setSelectionModel(selectionModel);
  };
  //#endregion

  return (
    <CommonDataGrid
      autoHeight
      columns={gridColList}
      rows={data}
      onRowClick={handleGridRowClick}
      getRowId={getRowId}
      rowSelectionModel={selectionModel}
      onRowSelectionModelChange={handleCheckboxChange}
      isHidePagination={true}
    />
  );
}
