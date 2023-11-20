import { Button, Divider, Modal, Typography } from '@mui/material';
import { GridColDef, GridRowId, GridRowParams } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import CommonDataGrid from '@/components/datagrid/CommonDataGrid';
import { ServiceDetailData, ServiceTemplateData, useMigrationService } from '@/network/apis/useServiceDetail';

interface ModalProps {
  serviceId: string;
  data: ServiceDetailData[];
  templates: ServiceTemplateData[];
  isModalOpen: boolean;
  closeModal: () => void;
}

/**
 * 마이그레이션 Modal
 * @param props: ModalProps
 */
export default function MigrationModal(props: ModalProps) {
  const [selectionModel, setSelectionModel] = useState<GridRowId[]>([]); //마이그레이션 타겟 템플릿 리스트 체크된 체크박스 list : number[]
  const [data, setData] = useState(props.data); // 서비스 상세 조회 데이터
  const [templates, setTemplates] = useState<ServiceTemplateData[]>(props.templates); // 서비스 상세 조회 데이터(마이그레이션 타겟 템플릿 리스트)
  const [isModalOpen, setIsModalOpen] = useState(props.isModalOpen); // 모달 isModalOpen

  // 마이그레이션 요청 API
  const migrationServiceQuery = useMigrationService({
    serviceId: String(data[0].serviceId),
    serviceTemplateId: selectionModel,
  });

  useEffect(() => {
    // setServiceId(props.serviceId);
    setData(props.data);
    setTemplates(props.templates);
    setIsModalOpen(props.isModalOpen);
  }, [props.data, props.isModalOpen, props.serviceId, props.templates]);

  // 마이그레이션 버튼 클릭 이벤트
  const handleMigrationButtonClick = () => {
    console.log(selectionModel.length === 0);
    if (selectionModel.length === 0) {
      alert('타겟 서비스 템플릿을 선택해주세요.');
    } else {
      migrationServiceQuery.refetch();
    }
  };

  //현재 서비스 Grid Column
  const curGridColList: GridColDef[] = [
    {
      field: 'serviceName',
      headerName: '서비스명',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 3,
    },
    {
      field: 'csp',
      headerName: 'csp',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 1,
    },
    {
      field: 'applicationName',
      headerName: 'Application',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 2,
    },
    {
      field: 'applicationType',
      headerName: 'Application 타입',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 1.5,
    },
  ];

  // #region Modal
  //마이그레이션 타겟 템플릿 목록 Grid Column
  const gridColList: GridColDef[] = [
    {
      field: 'serviceTemplateId',
      headerName: '템플릿 ID',
      type: 'number',
      headerAlign: 'center',
      align: 'center',
      flex: 1.5,
    },
    {
      field: 'serviceTemplateName',
      headerName: '템플릿명',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 2,
    },
    {
      field: 'targetCsp1',
      headerName: '타겟 CSP',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 1,
    },
    {
      field: 'serviceTemplatePath',
      headerName: '템플릿 파일 경로',
      sortable: false,
      headerAlign: 'center',
      align: 'center',
      flex: 3,
    },
  ];

  //Row Id
  const getRowId = (row: { serviceTemplateId: number }) => {
    return row.serviceTemplateId;
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
    <Modal open={isModalOpen} onClose={props.closeModal}>
      <div
        style={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          backgroundColor: '#fff',
          padding: '3rem 2rem 1rem 2rem',
          width: '70%',
        }}
      >
        <Typography variant="h5">서비스 마이그레이션</Typography>
        <Divider style={{ margin: '10px 0', border: 'none' }} />
        <Typography variant="subtitle1" gutterBottom>
          현재 서비스
        </Typography>
        <CommonDataGrid
          className="currentGrid"
          autoHeight
          disableRowSelectionOnClick={true}
          columns={curGridColList}
          rows={data}
          getRowId={getRowId}
          isHidePagination={true}
        />
        <Divider style={{ margin: '20px 0', border: 'none' }} />
        <Typography variant="subtitle1" gutterBottom>
          타겟 서비스 템플릿 목록
        </Typography>
        <CommonDataGrid
          autoHeight
          columns={gridColList}
          rows={templates}
          onRowClick={handleGridRowClick}
          getRowId={getRowId}
          rowSelectionModel={selectionModel}
          onRowSelectionModelChange={handleCheckboxChange}
          isHidePagination={true}
          overlayHeight={150}
        />
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '10px', marginTop: '25px' }}>
          <Button
            style={{ marginRight: '30px' }}
            variant="contained"
            color="secondary"
            onClick={handleMigrationButtonClick}
          >
            마이그레이션
          </Button>
          <Button variant="contained" color="primary" onClick={props.closeModal}>
            취소
          </Button>
        </div>
      </div>
    </Modal>
  );
}
