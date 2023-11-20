import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import { GridColDef, GridRenderCellParams, GridRowId, GridRowParams } from '@mui/x-data-grid';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import CommonDataGrid from '@/components/datagrid/CommonDataGrid';
import { useGetInstanceList } from '@/network/apis/useInstance';
import { Data as TemplateData } from '@/network/apis/useTamplate';
import { VPCData, useDeployAcrossService, useGetVPC } from '@/network/apis/useVPC';

const formatDateTime = (dateTime: string | null) => {
  if (dateTime) {
    const formattedDate = dayjs(dateTime).format('YYYY-MM-DD');
    const formattedTime = dayjs(dateTime).format('HH:mm:ss');
    return (
      <div>
        <div>{formattedDate}</div>
        <div>{formattedTime}</div>
      </div>
    );
  } else {
    return null;
  }
};

const gridColList: GridColDef[] = [
  {
    field: 'vpcId',
    headerName: 'vpcid',
    flex: 0.3,
  },
  {
    field: 'serviceInstanceId',
    headerName: '서비스 인스턴스 id',
    flex: 1.5,
  },
  {
    field: 'vpcCidr',
    headerName: 'vpc ip 대역',
    sortable: false,
    flex: 1,
  },
  {
    field: 'subnetCidr',
    headerName: 'subnet ip 대역',
    sortable: false,
    flex: 1,
  },
  {
    field: 'vpcCreateDate',
    headerName: 'vpc 생성일시',
    sortable: false,
    renderCell: (params: GridRenderCellParams) => formatDateTime(params.row.vpcCreateDate),
    flex: 1,
  },
  {
    field: 'vpnTunnelIp',
    headerName: 'vpn tunnel(vgw) ip',
    sortable: false,
    flex: 1,
  },
  {
    field: 'vpnTunnelCreateDate',
    headerName: 'vpn tunnel(vgw) 생성일시',
    sortable: false,
    renderCell: (params: GridRenderCellParams) => formatDateTime(params.row.vpnTunnelCreateDate),
    flex: 1,
  },
  {
    field: 'csp',
    headerName: '클라우드 서비스 프로바이더 타입',
    sortable: false,
    flex: 1,
  },
];

export function VPCGrid({
  templateRowData,
  closeModal,
}: {
  templateRowData: TemplateData | null;
  closeModal: () => void;
}) {
  // 선택된 row의 데이터를 저장할 state
  const { isLoading, isError, data: rows = [] } = useGetVPC();

  const [filteredData, setFilteredData] = useState<VPCData[]>([]);
  const [deployLoading, setDeployLoading] = useState(false);
  const instanceListQuery = useGetInstanceList();
  const { data: instanceData = [] } = instanceListQuery;

  const [selectedGroupData, setSelectedGroupData] = useState<{ [key: string]: VPCData | null }>({});
  const selectedRowDataArray = Object.values(selectedGroupData).filter(data => data !== null) as VPCData[];

  // Agent 활성화 API
  const deployAccrossServiceQuery = useDeployAcrossService(templateRowData, selectedRowDataArray);

  // 사용자가 템플릿을 선택할 때 해당 템플릿과 관련된 VPC 데이터를 필터링하여 업데이트
  useEffect(() => {
    if (templateRowData && rows && instanceData) {
      const filteredRows = rows.filter(row => {
        return (
          (row.csp === templateRowData.targetCsp1 || row.csp === templateRowData.targetCsp2) &&
          !instanceData.some(instance => instance.vpcId === row.vpcId)
        );
      });

      // 배열 비교를 수행
      if (JSON.stringify(filteredRows) !== JSON.stringify(filteredData)) {
        setFilteredData(filteredRows);
      }
    } else {
      setFilteredData([]);
    }
  }, [templateRowData, rows, instanceData, filteredData]);
  // 그룹화 및 데이터 초기화
  useEffect(() => {
    if (templateRowData) {
      const groups: { [key: string]: VPCData[] } = {};

      filteredData.forEach(row => {
        if (!groups[row.csp]) {
          groups[row.csp] = [];
        }
        groups[row.csp].push(row);
      });
    }
  }, [templateRowData, filteredData]);

  //#endregion

  const handleTemplateDeployment = () => {
    if (templateRowData) {
      deployAccrossServiceQuery.refetch().then(response => {
        console.log('console.log', response);
        closeModal();
      });
    }
  };

  // 클릭 핸들러
  const seletedRowData = (params: GridRowParams) => {
    const rowData = params.row as VPCData;
    const csp = rowData.csp;

    if (selectedGroupData[csp] === rowData) {
      setSelectedGroupData(prev => ({ ...prev, [csp]: null }));
    } else {
      setSelectedGroupData(prev => ({ ...prev, [csp]: rowData }));
    }
  };

  //체크박스 상태 관리
  const [selectionModel, setSelectionModel] = useState<GridRowId[]>([]); //체크된 체크박스 list : number[]
  const getRowId = (filteredData: { vpcId: number }) => {
    return filteredData.vpcId;
  };

  const handleGridRowClick = (params: GridRowParams) => {
    console.log(params.id);
    console.log(params.row);
    seletedRowData(params);
  };

  //체크박스 클릭 이벤트
  const handleCheckboxChange = (selectionModel: GridRowId[]) => {
    setSelectionModel(selectionModel);
  };

  const checkboxColumn: GridColDef = {
    field: 'checkbox',
    headerName: '',
    sortable: false,
    renderCell: (params: GridRenderCellParams) => {
      const rowData = params.row as VPCData;
      const csp = rowData.csp;

      return <Checkbox checked={selectedGroupData[csp] === rowData} />;
    },
    flex: 0.2,
  };
  const columns = [checkboxColumn, ...gridColList];

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
        <Box flex="1" display="flex" justifyContent="flex-end" alignItems="center">
          <Button
            id="TemplateDeployCommender"
            variant="contained"
            onClick={() => {
              setDeployLoading(true);
              handleTemplateDeployment();
            }}
            disabled={isLoading || isError || deployLoading}
          >
            {deployLoading ? '배포 중...' : '템플릿 배포'}
          </Button>
        </Box>
      </div>
      <div className="grid" style={{ height: '100%', width: '100%' }}>
        <CommonDataGrid
          autoHeight
          autoPageSize
          columns={columns}
          rows={filteredData}
          onRowClick={handleGridRowClick}
          getRowId={getRowId}
          rowSelectionModel={selectionModel}
          onRowSelectionModelChange={handleCheckboxChange}
        />
      </div>
    </>
  );
}
