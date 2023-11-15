import { Button, ButtonGroup, CircularProgress } from '@mui/material';
import { GridColDef, GridRowId, GridRowParams } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import CommonDataGrid from '@/components/datagrid/CommonDataGrid';
import {
  AcrossServiceListData,
  useAcrossServiceList,
  useDeleteAcrossService,
} from '@/network/apis/useAcrossServiceList';

const ErrorCol = styled.p`
  color: red;
`;
const RunningCol = styled.p`
  color: green;
`;

//#region 쿼리 데이터 로그 영역
async function responseAcrossServiceLog(acrossServiceData: AcrossServiceListData[]) {
  console.log(acrossServiceData);
  return acrossServiceData;
}
//#endregion

export default function AcrossServiceListGrid() {
  const navigate = useNavigate();
  //연계 서비스 리스트
  const [acrossServices, setAcrossServices] = useState<AcrossServiceListData[]>([]);
  //체크박스 상태 관리
  const [selectionModel, setSelectionModel] = useState<GridRowId[]>([]); //체크된 체크박스 list : number[]
  //삭제 파라미터
  const [vmOnlyYn, setVmOnlyYn] = useState<string | null>(null);

  //연계 서비스 통합 조회 API
  const acrossServiceList = useAcrossServiceList();
  //연계 서비스 삭제 API
  const { refetch: deleteRefetch } = useDeleteAcrossService(vmOnlyYn, selectionModel);

  useEffect(() => {
    if (acrossServiceList.isSuccess) {
      responseAcrossServiceLog(acrossServiceList.data).then(response => {
        setAcrossServices(response as AcrossServiceListData[]);
      });
    }
  }, [acrossServiceList.data, acrossServiceList.isSuccess]);

  const gridColList: GridColDef[] = [
    {
      field: 'acrossServiceId',
      headerName: '연계 서비스 ID',
      type: 'number',
      flex: 1,
    },
    {
      field: 'acrossServiceName',
      headerName: '연계 서비스 명',
      sortable: false,
      flex: 2,
    },
    {
      field: 'acrossType',
      headerName: '연계 타입',
      flex: 2,
    },
    {
      field: 'acrossStatus',
      headerName: '연계 서비스 상태',
      flex: 2,
      renderCell: params => {
        if (params.row.acrossStatus === 'RUNNING') {
          return <RunningCol>{params.row.acrossStatus}</RunningCol>;
        } else if (params.row.acrossStatus === 'ERROR') {
          return <ErrorCol>ABNORMAL</ErrorCol>;
        } else if (params.row.acrossStatus.includes('FAIL')) {
          return <ErrorCol>{params.row.acrossStatus}</ErrorCol>;
        } else if (params.row.acrossStatus !== 'RUNNING' && !params.row.acrossStatus.includes('FAIL')) {
          return (
            <>
              <CircularProgress style={{ height: 12, width: 12 }} />
              <p>{params.row.acrossStatus}</p>
            </>
          );
        } else {
          params.row.acrossStatus;
        }
      },
    },
    {
      field: 'acrossCreateDate',
      headerName: '연계 서비스 생성일시',
      flex: 2,
    },
    {
      field: 'detailView',
      headerName: '상세',
      flex: 2,
      renderCell: params => (
        <Button
          onClick={event => {
            event.stopPropagation();
            console.log('Detail view button clicked:', params.row);
            navigate(`/across-service/${params.row.acrossServiceId}`);
          }}
          disabled={params.row.acrossStatus === 'INIT'}
          variant="outlined"
        >
          상세보기
        </Button>
      ),
    },
  ];

  const getRowId = (acrossServices: { acrossServiceId: number }) => {
    return acrossServices.acrossServiceId;
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

  /**
   * 삭제 버튼 클릭 이벤트
   * @Param vmOnlyYn  'Y': VM만 삭제 / 'N': VM, VPC 전체 삭제
   * @Param selectionModel  삭제할 across_service_id 리스트 (number[])
   */
  // const handleDeleteButtonClick = (vmOnlyYn: string, selectionModel: GridRowId[]) => async () => {
  //   setVmOnlyYn(vmOnlyYn);
  //   console.log('[서비스 삭제] vmOnlyYn: ' + vmOnlyYn);
  //   console.log(selectionModel.toString());
  //   if (selectionModel.length <= 0) {
  //     alert('연계 서비스를 선택해주세요.');
  //   }
  // };

  useEffect(() => {
    if (vmOnlyYn && selectionModel.length > 0) {
      deleteRefetch();
    }
    setVmOnlyYn(null);
  }, [deleteRefetch, selectionModel.length, vmOnlyYn]);

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
        <p>연계 서비스 목록</p>
        <div
          style={{
            display: 'flex',
            justifyContent: 'flex-end', // This will align the buttons to the right
          }}
        >
          <ButtonGroup variant="contained">
            <Button color={'error'} disabled={selectionModel.length <= 0} onClick={() => setVmOnlyYn('Y')}>
              서비스 삭제
            </Button>
            <Button color={'error'} disabled={selectionModel.length <= 0} onClick={() => setVmOnlyYn('N')}>
              전체 삭제
            </Button>
          </ButtonGroup>
        </div>
      </div>
      <div className="grid" style={{ height: '100%', width: '100%' }}>
        <CommonDataGrid
          checkboxSelection
          autoHeight
          columns={gridColList}
          rows={acrossServices}
          onRowClick={handleGridRowClick}
          getRowId={getRowId}
          rowSelectionModel={selectionModel}
          onRowSelectionModelChange={handleCheckboxChange}
        />
      </div>
    </>
  );
}
