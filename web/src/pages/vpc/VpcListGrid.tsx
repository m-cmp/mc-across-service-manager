import { GridColDef, GridRowId, GridRowParams } from '@mui/x-data-grid';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import CommonDataGrid from '@/components/datagrid/CommonDataGrid';
import { VPCData, useGetVPC } from '@/network/apis/useVPC';

export default function VpcListGrid() {
  //체크박스 상태 관리
  const [selectionModel, setSelectionModel] = useState<GridRowId[]>([]); //체크된 체크박스 list : number[]
  //VPC 리스트
  const [vpcList, setVpcList] = useState<VPCData[]>([]);

  //VPC 목록 조회 API
  const vpcListQuery = useGetVPC();

  useEffect(() => {
    if (vpcListQuery.isSuccess) {
      setVpcList(vpcListQuery.data);
    }
  }, [vpcListQuery.data, vpcListQuery.isSuccess]);

  //#region DataGrid
  const gridColList: GridColDef[] = [
    {
      field: 'vpcId',
      headerName: 'vpc ID',
      type: 'number',
      width: 80,
    },
    {
      field: 'serviceInstanceId',
      headerName: '서비스 인스턴스 ID',
      sortable: false,
      width: 130,
    },
    {
      field: 'vpcCidr',
      headerName: 'vpc 대역',
      sortable: false,
      width: 130,
    },
    {
      field: 'subnetCidr',
      headerName: '서브넷 대역',
      sortable: false,
      width: 130,
    },
    {
      field: 'vpnTunnelIp',
      headerName: 'vpn tunnel ip',
      sortable: false,
      width: 130,
    },
    {
      // field: 'vpcCreateDate',
      field: 'vpcCreateDate',
      headerName: 'vpc 생성일시',
      sortable: false,
      width: 130,
      type: 'datetime',
      valueFormatter: params => (params.value ? dayjs(params.value).format('YYYY-MM-DD HH:ss:mm') : null),
      // renderCell: (params: GridRenderCellParams) => formatDateTime(params.row.vpcCreateDate),:
    },
    {
      field: 'vpnTunnelCreateDate',
      headerName: 'vpn tunnel 생성일시',
      width: 130,
      flex: 0.5,
      valueFormatter: params => (params.value ? dayjs(params.value).format('YYYY-MM-DD HH:ss:mm') : null),
      // renderCell: (params: GridRenderCellParams) => formatDateTime(params.row.vpnTunnelCreateDate),
    },
  ];

  const getRowId = (vpcList: { vpcId: string }) => {
    return vpcList.vpcId;
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
        <p>VPC 목록</p>
      </div>
      <div className="grid" style={{ height: '100%', width: '100%' }}>
        <CommonDataGrid
          autoHeight
          columns={gridColList}
          rows={vpcList}
          onRowClick={handleGridRowClick}
          getRowId={getRowId}
          rowSelectionModel={selectionModel}
          onRowSelectionModelChange={handleCheckboxChange}
        />
      </div>
    </>
  );
}
