import { Button, ButtonGroup, CircularProgress } from '@mui/material';
import { GridColDef, GridRowId, GridRowParams } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import CommonDataGrid from '@/components/datagrid/CommonDataGrid';
import {
  ServiceListData,
  useDeleteService,
  useServiceList,
  // useServiceListHealthCheck,
} from '@/network/apis/useServiceList';

const ErrorCol = styled.p`
  color: red;
`;
const RunningCol = styled.p`
  color: green;
`;

export default function ServiceListGrid() {
  const navigate = useNavigate();
  //서비스 리스트
  const [services, setServices] = useState<ServiceListData[]>([]);
  //체크박스 상태 관리
  const [selectionModel, setSelectionModel] = useState<GridRowId[]>([]); //체크된 체크박스 list : number[]

  //서비스 목록 조회 API
  const serviceList = useServiceList();
  //서비스 목록 Health Check API
  // const serviceListHealthCheck = useServiceListHealthCheck();
  //서비스 삭제 API  (selectionModel: 삭제할 service_id 리스트)
  const deleteServiceQuery = useDeleteService(selectionModel);

  useEffect(() => {
    if (serviceList.isSuccess) {
      setServices(serviceList.data);
    }
  }, [serviceList.data, serviceList.isSuccess]);

  // 서비스 삭제 버튼 클릭 이벤트
  const handleDeleteServiceButtonClick = () => {
    console.log(selectionModel);
    if (selectionModel.length === 0) {
      alert('서비스를 선택해주세요.');
    } else {
      deleteServiceQuery.refetch();
    }
  };

  //#region DataGrid
  const gridColList: GridColDef[] = [
    {
      field: 'rowId',
      headerName: 'NO',
      type: 'string',
      flex: 1,
      editable: false,
      headerAlign: 'center',
      align: 'center',
    },
    {
      field: 'serviceId',
      headerName: '서비스 ID',
      type: 'number',
      flex: 1,
      editable: false,
      headerAlign: 'center',
      align: 'center',
    },
    {
      field: 'serviceName',
      headerName: '서비스명',
      flex: 2,
      editable: false,
      headerAlign: 'center',
      align: 'center',
    },
    {
      field: 'csp',
      headerName: 'CSP',
      flex: 2,
      editable: false,
      headerAlign: 'center',
      align: 'center',
    },
    {
      field: 'serviceStatus',
      headerName: '서비스 상태',
      sortable: false,
      editable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 2,
      renderCell: params => {
        if (params.row.serviceStatus === 'RUNNING') {
          return <RunningCol>{params.row.serviceStatus}</RunningCol>;
        } else if (params.row.serviceStatus === 'ERROR') {
          return <ErrorCol>ABNORMAL</ErrorCol>;
        } else if (params.row.serviceStatus.includes('FAIL')) {
          return <ErrorCol>{params.row.serviceStatus}</ErrorCol>;
        } else if (params.row.serviceStatus !== 'RUNNING' && !params.row.serviceStatus.includes('FAIL')) {
          return (
            <>
              <CircularProgress style={{ height: 12, width: 12 }} />
              <p>{params.row.serviceStatus}</p>
            </>
          );
        } else {
          params.row.serviceStatus;
        }
      },
    },
    {
      field: 'serviceCreateDate',
      headerName: '서비스 생성일시',
      flex: 2,
      editable: false,
      headerAlign: 'center',
      align: 'center',
    },
    {
      field: 'detailView',
      headerName: '상세',
      flex: 1,
      renderCell: params => (
        <Button
          onClick={event => {
            event.stopPropagation();
            console.log('Detail view button clicked:', params.row);
            navigate(`/service/${params.row.serviceId}`);
          }}
          variant="outlined"
        >
          상세보기
        </Button>
      ),
    },
  ];

  // const getRowId = (services: { serviceId: number }) => {
  //   return services.serviceId;

  const getRowId = (services: { serviceId: string; csp: string; vmInstanceId: string }) => {
    return services.serviceId + '-' + services.csp + '-' + services.vmInstanceId;
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
    <>
      <div
        style={{
          height: 50,
          width: '100%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          fontSize: '20px',
        }}
      >
        <p>서비스 목록</p>
        <div
          style={{
            display: 'flex',
            justifyContent: 'flex-end', // This will align the buttons to the right
          }}
        >
          <ButtonGroup variant="contained">
            <Button disabled={selectionModel.length <= 0} color={'error'} onClick={handleDeleteServiceButtonClick}>
              삭제
            </Button>
          </ButtonGroup>
        </div>
      </div>
      <div className="grid" style={{ height: '100%', width: '100%' }}>
        <CommonDataGrid
          checkboxSelection
          autoHeight
          columns={gridColList}
          rows={services}
          onRowClick={handleGridRowClick}
          getRowId={getRowId}
          rowSelectionModel={selectionModel}
          onRowSelectionModelChange={handleCheckboxChange}
        />
      </div>
    </>
  );
}
