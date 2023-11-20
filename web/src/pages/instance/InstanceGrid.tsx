import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip/Chip';
import { GridColDef, GridRenderCellParams, GridRowId, GridRowParams } from '@mui/x-data-grid';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import CommonDataGrid from '@/components/datagrid/CommonDataGrid';
import { Data, useActiveAgent, useDeployAgent, useGetInstanceList } from '@/network/apis/useInstance';

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
  { field: 'vmInstanceName', headerName: '인스턴스 명', flex: 1.2 },
  { field: 'csp', headerName: 'csp', flex: 0.4, sortable: false },
  {
    field: 'vmInstanceStatus',
    headerName: '인스턴스 상태',
    flex: 0.6,
    sortable: false,
    renderCell: (params: GridRenderCellParams) => {
      const status = params.row.vmInstanceStatus;
      if (status.toLowerCase() === 'running') {
        return <span style={{ color: 'green' }}>RUNNING</span>;
      } else {
        return <span>{status}</span>;
      }
    },
  },
  { field: 'vmInstancePublicIp', headerName: '인스턴스 public ip', flex: 0.8, sortable: false },
  { field: 'vmInstancePrivateIp', headerName: '인스턴스 private ip', flex: 0.8, sortable: false },
  {
    field: 'vmInstanceCreateDate',
    headerName: '인스턴스 생성일시',
    flex: 0.9,
    renderCell: (params: GridRenderCellParams) => formatDateTime(params.row.vmInstanceCreateDate),
  },
  { field: 'vmMemoryType', headerName: '플레이버 타입', flex: 0.6, sortable: false },
  {
    field: 'agentDeployYn',
    headerName: 'agent 배포 여부',
    flex: 0.7,
    sortable: false,
    align: 'center',
    renderCell: params => {
      const agentDeployYn = params.row.agentDeployYn;
      const chipColor = agentDeployYn === 'N' ? 'default' : 'primary';
      return <Chip label={agentDeployYn} color={chipColor} />;
    },
  },
  {
    field: 'agentActivateYn',
    headerName: 'agent 상태',
    flex: 0.5,
    sortable: false,
    align: 'center',
    renderCell: params => {
      const agentActivateYn = params.row.agentActivateYn;
      const chipColor = agentActivateYn === 'N' ? 'default' : 'primary';
      return <Chip label={agentActivateYn} color={chipColor} />;
    },
  },
  {
    field: 'agentDeployDate',
    headerName: 'agent 배포 일시',
    flex: 0.8,
    renderCell: (params: GridRenderCellParams) => formatDateTime(params.row.agentDeployDate),
  },
  { field: 'tfstateFilePath', headerName: '서비스 템플릿 파일 경로', flex: 1, sortable: false },
];

export default function InstanceListGrid() {
  //인스턴스 리스트
  const { data: initialData = [] } = useGetInstanceList();
  const [instanceData, setInstanceData] = useState<Data[]>(initialData);
  const [rowData, setRowData] = useState<Data>(initialData[0]);
  useEffect(() => {
    if (initialData.length > 0) {
      setInstanceData(initialData);
    }
  }, [initialData]);
  // Agent 배포 API
  const deployAgentQuery = useDeployAgent(rowData);
  // Agent 활성화 API
  const activeAgentQuery = useActiveAgent(rowData);

  //버튼 제어
  const [deployButtonDisabled, setDeployButtonDisabled] = useState<Record<string, boolean>>({});
  const [activateButtonDisabled, setActivateButtonDisabled] = useState<Record<string, boolean>>({});

  //체크박스 상태 관리
  const [selectionModel, setSelectionModel] = useState<GridRowId[]>([]); //체크된 체크박스 list : number[]
  const getRowId = (instanceData: { serviceInstanceId: string }) => {
    return instanceData.serviceInstanceId;
  };

  const handleGridRowClick = (params: GridRowParams) => {
    console.log(params.id);
    console.log(params.row);
  };

  //체크박스 클릭 이벤트
  const handleCheckboxChange = (selectionModel: GridRowId[]) => {
    console.log('Selected Rows:', selectionModel);
    setSelectionModel(selectionModel);
  };

  const handleAgentActivateClick = async (rowData: Data) => {
    const instanceName = rowData.vmInstanceName;
    setActivateButtonDisabled(prev => ({ ...prev, [instanceName]: true }));
    try {
      await setRowData(rowData);
      activeAgentQuery.error = null;
      const result = activeAgentQuery.refetch();
      await result;
    } finally {
      setActivateButtonDisabled(prev => ({ ...prev, [instanceName]: false }));
    }
  };

  const handleAgentDeployClick = async (rowData: Data) => {
    const instanceName = rowData.vmInstanceName;

    setDeployButtonDisabled(prev => ({ ...prev, [instanceName]: true }));

    try {
      await setRowData(rowData);
      deployAgentQuery.error = null;
      const result = deployAgentQuery.refetch();
      await result;
    } finally {
      setDeployButtonDisabled(prev => ({ ...prev, [instanceName]: false }));
    }
  };

  const renderActionsCell = (params: GridRenderCellParams) => {
    const rowData = params.row as Data;
    const instanceName = rowData.vmInstanceName;

    const isAgentDeployDisable = rowData.agentDeployYn === 'Y' || deployButtonDisabled[instanceName];
    const buttonText = rowData.agentActivateYn === 'N' ? 'Agent 활성화' : 'Agent 비활성화';
    const buttonStyle = {
      background: rowData.agentActivateYn === 'Y' ? 'pink' : '',
    };
    const isAgentDeploy = rowData.agentDeployYn === 'Y';

    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Button variant="contained" onClick={() => handleAgentDeployClick(rowData)} disabled={isAgentDeployDisable}>
          Agent 배포
        </Button>
        <Button
          variant="contained"
          onClick={() => handleAgentActivateClick(rowData)}
          disabled={activateButtonDisabled[instanceName] || !isAgentDeploy}
          style={buttonStyle}
        >
          {buttonText}
        </Button>
      </div>
    );
  };

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
        <p>인스턴스 목록</p>
      </div>
      <div className="grid" style={{ height: '100%', width: '100%' }}>
        <CommonDataGrid
          autoHeight
          autoPageSize
          columns={[
            ...gridColList,

            {
              headerName: '',
              field: 'actions',
              width: 240,
              renderCell: renderActionsCell,
              align: 'center',
            },
          ]}
          rows={instanceData}
          onRowClick={handleGridRowClick}
          getRowId={getRowId}
          rowSelectionModel={selectionModel}
          onRowSelectionModelChange={handleCheckboxChange}
        />
      </div>
    </>
  );
}
