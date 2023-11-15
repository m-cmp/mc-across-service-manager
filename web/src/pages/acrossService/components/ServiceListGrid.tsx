import Button from '@mui/material/Button';
import { GridColDef, GridRowId, GridRowParams } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import styled from 'styled-components';
import CommonChip from '@/components/datagrid/CommonChip';
import CommonDataGrid from '@/components/datagrid/CommonDataGrid';
import {
  AcrossServiceData,
  useActiveAcrossAgent,
  useActiveAcrossService,
  useDeployAcrossSVCAgents,
  ActivationService,
  ActivationAgent,
} from '@/network/apis/useAcrossServiceDetail';

const gridColList: GridColDef[] = [
  // {
  //   field: 'vmInstanceId',
  //   headerName: 'VM ID',
  //   headerAlign: 'center',
  //   align: 'center',
  //   flex: 2,
  //   sortable: false,
  // },
  {
    field: 'csp',
    headerName: 'CSP',
    headerAlign: 'center',
    align: 'center',
    flex: 1.8,
    sortable: false,
  },
  {
    field: 'vmInstanceStatus',
    headerName: 'VM 상태',
    headerAlign: 'center',
    align: 'center',
    flex: 2,
    sortable: false,
  },
  {
    field: 'mainGslb',
    headerName: 'GSLB 생성',
    headerAlign: 'center',
    align: 'center',
    flex: 2.1,
    sortable: false,
    renderCell: params => (params.row.mainGslb ? CommonChip(params.row.mainGslb) : null),
  },
  {
    field: 'agentDeployYn',
    headerName: 'Agent 배포',
    headerAlign: 'center',
    align: 'center',
    flex: 2.1,
    sortable: false,
    renderCell: params => CommonChip(params.row.agentDeployYn),
  },
  {
    field: 'agentActiveYn',
    headerName: 'Agent 활성화',
    headerAlign: 'center',
    align: 'center',
    flex: 2.2,
    sortable: false,
    renderCell: params => CommonChip(params.row.agentActivateYn),
  },
  {
    field: 'vmInstancePublicIp',
    headerName: 'Service Expose',
    headerAlign: 'center',
    align: 'center',
    flex: 3.2,
    sortable: false,
    renderCell: params => (params.row.acrossType === 'GSLB' ? params.row.gslbDomain : params.row.vmInstancePublicIp),
  },
  // {
  //   field: 'gslbDomain',
  //   headerName: 'domain',
  //   headerAlign: 'center',
  //   align: 'center',
  //   flex: 2.5,
  //   sortable: false,
  // },
  // {
  //   field: 'applicationType',
  //   headerName: 'APP 타입',
  //   headerAlign: 'center',
  //   align: 'center',
  //   flex: 2,
  //   sortable: false,
  // },
  {
    field: 'applicationType',
    headerName: 'APP 타입',
    headerAlign: 'center',
    align: 'center',
    flex: 2,
    sortable: false,
  },
  {
    field: 'applicationActivateYn',
    headerName: '서비스 활성화',
    headerAlign: 'center',
    align: 'center',
    flex: 2.5,
    sortable: false,
    renderCell: params => (params.row.applicationActivateYn ? CommonChip(params.row.applicationActivateYn) : null),
  },
  {
    field: 'applicationCreateDate',
    headerName: 'APP 생성일시',
    headerAlign: 'center',
    align: 'center',
    flex: 3,
    sortable: false,
  },
];
interface Props {
  data: AcrossServiceData[];
}

const ButtonSection = styled.section`
  direction: rtl;
  margin-bottom: 10px;
`;

export default function ServiceListGrid({ data }: Props) {
  const [acrossServiceId, setAcrossServiceId] = useState(0);
  const [activeSvcParam, setActiveSvcParam] = useState<ActivationService[]>([]);
  const [activeAgentsParam, setActiveAgentsParam] = useState<ActivationAgent[]>([]);
  const { refetch: deployAgentRefetch } = useDeployAcrossSVCAgents(acrossServiceId);
  const { refetch: activeSvcRefetch } = useActiveAcrossService(acrossServiceId, activeSvcParam);
  const { refetch: activeAgentsRefetch } = useActiveAcrossAgent(acrossServiceId, activeAgentsParam);

  //체크박스 상태 관리
  const [selectionModel, setSelectionModel] = useState<GridRowId[]>([]); //체크된 체크박스 list : number[]

  const getRowId = (list: { serviceInstanceId: string }) => {
    return list.serviceInstanceId;
  };

  //Row 클릭 이벤트
  const handleGridRowClick = (params: GridRowParams) => {
    console.log(params.id);
  };

  //체크박스 클릭 이벤트
  const handleCheckboxChange = (selectionModel: GridRowId[]) => {
    console.log('Selected Rows:', selectionModel);
    setSelectionModel(selectionModel);
  };

  useEffect(() => {
    if (data.length > 0) {
      setAcrossServiceId(data[0].acrossServiceId);
    }
  }, [data]);

  // 연계서비스 Agent 배포 버튼 클릭시 동작
  const handleClickDeployAgent = () => {
    if (acrossServiceId && acrossServiceId !== 0) {
      deployAgentRefetch();
    }
  };
  function sortActiveParam(a: string, b: string) {
    if (a === 'DB' && b !== 'DB') {
      return -1;
    } else if (a !== 'DB' && b === 'DB') {
      return 1;
    }
    return 0;
  }

  // useEffect(() => {
  //   activeAgentsParam.length > 0 ? deployAgentRefetch() : null;
  // }, [activeAgentsParam, deployAgentRefetch]);

  useEffect(() => {
    activeAgentsParam.length > 0 ? activeAgentsRefetch() : null;
  }, [activeAgentsParam, activeAgentsRefetch]);

  useEffect(() => {
    activeSvcParam.length > 0 ? activeSvcRefetch() : null;
  }, [activeSvcRefetch, activeSvcParam]);

  return (
    <>
      <ButtonSection aria-label="buttons-service-list">
        <Button
          variant="contained"
          size="medium"
          color={data?.length > 0 && data[0].applicationActivateYn === 'Y' ? 'error' : 'primary'}
          disabled={data?.length <= 0}
          onClick={() =>
            setActiveSvcParam(
              data
                .map(acrossSvc => {
                  return acrossSvc.acrossType.toUpperCase() === 'VPN_TUNNEL'
                    ? {
                        application_id: acrossSvc.applicationId,
                        application_activate_yn: acrossSvc.applicationActivateYn === 'N' ? 'Y' : 'N',
                        vm_instance_private_ip: acrossSvc.vmInstancePrivateIp,
                        vm_instance_public_ip: acrossSvc.vmInstancePublicIp,
                        application_type: acrossSvc.applicationType,
                        across_type: acrossSvc.acrossType.toUpperCase(),
                      }
                    : {
                        application_id: acrossSvc.applicationId,
                        application_activate_yn: acrossSvc.applicationActivateYn === 'N' ? 'Y' : 'N',
                        vm_instance_public_ip: acrossSvc.vmInstancePublicIp,
                        application_type: acrossSvc.applicationType,
                        across_type: acrossSvc.acrossType.toUpperCase(),
                      };
                })
                .sort((a, b) => {
                  return sortActiveParam(a.application_type, b.application_type);
                })
            )
          }
        >
          {data?.length > 0 && data[0].applicationActivateYn === 'Y' ? '서비스 비활성화' : '서비스 활성화'}
        </Button>
        <Button
          style={{ marginRight: '10px' }}
          variant="contained"
          size="medium"
          color={
            data?.length > 0 && data[0].agentDeployYn === 'Y' && data[0].agentActivateYn === 'Y' ? 'error' : 'primary'
          }
          disabled={data?.length <= 0 || data[0].agentDeployYn === 'N'}
          onClick={() =>
            setActiveAgentsParam(
              data
                .map((acrossSvc, index) => {
                  return {
                    serviceInstanceId: acrossSvc.serviceInstanceId,
                    vmInstancePublicIp: acrossSvc.vmInstancePublicIp,
                    agentActivateYN: acrossSvc.agentActivateYn === 'N' ? 'Y' : 'N',
                    pingTargetUrl: data[index === 1 ? 0 : 1]?.vmInstancePrivateIp,
                    applicationType: acrossSvc.applicationType,
                    acrossType: acrossSvc.acrossType,
                  };
                })
                .sort((a, b) => {
                  return sortActiveParam(a.applicationType, b.applicationType);
                })
            )
          }
        >
          {data?.length > 0 && data[0].agentDeployYn === 'Y' && data[0].agentActivateYn === 'Y'
            ? 'agent 비활성화'
            : 'agent 활성화'}
        </Button>
        <Button
          style={{ marginRight: '10px' }}
          variant="contained"
          size="medium"
          color={'primary'}
          disabled={data?.length <= 0 || data[0].agentDeployYn === 'Y'}
          onClick={handleClickDeployAgent}
        >
          agent 배포
        </Button>
      </ButtonSection>

      <CommonDataGrid
        autoHeight
        columns={gridColList}
        rows={data}
        onRowClick={handleGridRowClick}
        getRowId={getRowId}
        rowSelectionModel={selectionModel}
        onRowSelectionModelChange={handleCheckboxChange}
        isHidePagination={true}
        overlayHeight={150}
      />
    </>
  );
}
