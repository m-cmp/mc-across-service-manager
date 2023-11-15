import { Box, Button, Divider, Modal, Toolbar, Typography } from '@mui/material';
import { GridColDef, GridRenderCellParams, GridRowId, GridRowParams } from '@mui/x-data-grid';
import dayjs from 'dayjs';
import { closeSnackbar } from 'notistack';
import { useState, useEffect } from 'react';
import CommonDataGrid from '@/components/datagrid/CommonDataGrid';
import { Data, useDeleteTemplate, useDeployService, useGetTamplate } from '@/network/apis/useTamplate';
import { VPCGrid } from '@/pages/template/VPCGrid';

const formatDateTime = (dateTime: string | null) => {
  if (dateTime) {
    const formattedDate = dayjs(dateTime).format('YYYY-MM-DD');
    const formattedTime = dayjs(dateTime).format('HH:mm:ss');
    return (
      <div>
        <time>{formattedDate}</time>
        <time>{formattedTime}</time>
      </div>
    );
  } else {
    return null;
  }
};

const gridColList: GridColDef[] = [
  { field: 'serviceTemplateName', headerName: '서비스 템플릿명', flex: 1, sortable: true },
  { field: 'acrossType', headerName: '서비스 타입', flex: 0.5, sortable: false },
  { field: 'targetCsp1', headerName: '대상 CSP1', flex: 0.3, sortable: false },
  { field: 'targetCsp2', headerName: '대상 CSP2', flex: 0.3, sortable: false },
  { field: 'serviceTemplatePath', headerName: '서비스 템플릿 파일 경로', flex: 1, sortable: false },
  {
    field: 'serviceTemplateCreateDate',
    headerName: '서비스 템플릿 생성일시',
    flex: 0.75,
    sortable: true,
    renderCell: (params: GridRenderCellParams) => formatDateTime(params.row.serviceTemplateCreateDate),
  },
];

export default function TemplateListGird() {
  const { data: initialData = [] } = useGetTamplate();
  const [templateRowData, setTemplateRowData] = useState<Data | null>(null);
  const [templateData, setTemplateData] = useState<Data[]>(initialData);
  useEffect(() => {
    if (initialData.length > 0) {
      setTemplateData(initialData);
    }
  }, [initialData]);
  const [serviceTemplateId, setServiceTemplateId] = useState<number>(0);
  const deleteTemplateQuery = useDeleteTemplate(serviceTemplateId);

  const [isDeleteButtonDisabled, setDeleteButtonDisabled] = useState(false);

  // Agent 활성화 API
  const createServiceQuery = useDeployService(templateRowData);
  // 단일 서비스 버튼 관리
  const [createButtonDisabled, setCreateButtonDisabled] = useState<Record<string, boolean>>({});

  //체크박스 상태 관리
  const [selectionModel, setSelectionModel] = useState<GridRowId[]>([]);
  const getRowId = (templateData: { serviceTemplateName: string }) => {
    return templateData.serviceTemplateName;
  };

  const handleGridRowClick = (params: GridRowParams) => {
    const row = params.row as Data;
    console.log(params.id);
    console.log(params.row);
    setServiceTemplateId(row.serviceTemplateId);
  };

  //체크박스 클릭 이벤트
  const handleCheckboxChange = (selectionModel: GridRowId[]) => {
    console.log('Selected Rows:', selectionModel);
    setSelectionModel(selectionModel);
  };

  //#region VPC 테이블 관련

  const closeModal = () => {
    setTemplateRowData(null);
    setModalOpen(false);
  };

  const vpcTableStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: '90%',
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
  };

  const [isModalOpen, setModalOpen] = useState(false);
  const handleModalOpen = async (templateRowData: Data) => {
    const templateID = templateRowData.serviceTemplateId;

    if (templateRowData == null) {
      console.log('선택된 템플릿이 없습니다.');
      return;
    }
    await setTemplateRowData(templateRowData);
    if (templateRowData.acrossType !== 'NONE') {
      setModalOpen(true);
    } else {
      try {
        setCreateButtonDisabled(prev => ({ ...prev, [templateID]: true }));

        const result = createServiceQuery.refetch().then();
        await result;
      } finally {
        setCreateButtonDisabled(prev => ({ ...prev, [templateID]: false }));
      }
    }
  };

  const handleModalClose = () => {
    setTemplateRowData(null);
    setModalOpen(false);
  };
  //#endregion
  //#region 삭제 모달 관련
  const DeleteModalStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 500,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
  };

  const [isDelteModalOpen, setDelteModalOpen] = useState(false);
  const deleteModalOpen = (target: Data) => {
    setTemplateRowData(target);
    setDelteModalOpen(true);
  };

  const deleteModalClose = () => {
    setTemplateRowData(null);
    setDelteModalOpen(false);
    setDeleteButtonDisabled(false);
  };

  const handleDelete = async (templateRowData: Data | null) => {
    if (templateRowData) {
      setDeleteButtonDisabled(true);

      const result = deleteTemplateQuery.refetch();
      result
        .then(queryResult => {
          closeSnackbar(templateRowData.serviceTemplateName);

          if (queryResult.error) {
            console.error('오류:', queryResult.error);
          } else {
            console.log('쿼리 결과:', queryResult.data);
            const updatedData = templateData.filter(row => row.serviceTemplateId !== templateRowData.serviceTemplateId);
            setTemplateData(updatedData);
          }
        })
        .catch(error => {
          console.error('프로미스 오류:', error);
        })
        .finally(() => {
          deleteModalClose();
        });
    }
  };

  //#endregion

  const renderActionsCell = (params: GridRenderCellParams) => {
    const rowData = params.row as Data;
    const templateID = rowData.serviceTemplateId;

    return (
      <div>
        <Button
          variant="outlined"
          color={'primary'}
          disabled={createButtonDisabled[templateID]}
          onClick={() => handleModalOpen(rowData)}
          sx={{ marginRight: 1 }}
        >
          서비스 생성
        </Button>
        <Button variant="outlined" color={'error'} onClick={() => deleteModalOpen(rowData)}>
          템플릿 삭제
        </Button>
      </div>
    );
  };

  return (
    <>
      <header>
        <h1>템플릿 목록</h1>
      </header>
      <div className="grid" style={{ height: '100%', width: '100%' }}>
        <CommonDataGrid
          autoHeight
          columns={[
            ...gridColList,
            {
              headerName: '',
              field: 'actions',
              width: 220,
              renderCell: renderActionsCell,
            },
          ]}
          rows={templateData}
          onRowClick={handleGridRowClick}
          getRowId={getRowId}
          rowSelectionModel={selectionModel}
          onRowSelectionModelChange={handleCheckboxChange}
        />
      </div>

      <Modal id="DeleteModal" open={isDelteModalOpen} onClose={deleteModalClose}>
        <Box sx={DeleteModalStyle}>
          <h1 id="modal-title">삭제 확인</h1>
          <Divider style={{ margin: '15px 0' }} />
          <Box>
            {templateRowData && (
              <>
                <div>
                  <strong>Service Template Name:</strong> {templateRowData.serviceTemplateName}
                </div>
                <div>
                  <strong>Service Template acrossType:</strong> {templateRowData.acrossType}
                </div>
                <div>
                  <strong>Service Template targetCsp:</strong>{' '}
                  {templateRowData.targetCsp2 ? (
                    <div>
                      {templateRowData.targetCsp1}
                      {'   |   '}
                      {templateRowData.targetCsp2}
                    </div>
                  ) : (
                    templateRowData.targetCsp1
                  )}
                </div>
                <div>
                  <strong>Service Template Path:</strong> {templateRowData.serviceTemplatePath}
                </div>
                <div>
                  <strong>Service Template Create Date:</strong>
                  {formatDateTime(templateRowData.serviceTemplateCreateDate)}
                </div>
              </>
            )}
          </Box>

          <Divider style={{ margin: '15px 0' }} />
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'flex-end',
              marginTop: '20px',
            }}
          >
            <Button variant="outlined" style={{ marginLeft: '10px' }} onClick={deleteModalClose}>
              취소
            </Button>
            <Button
              variant="contained"
              style={{ marginLeft: '10px' }}
              onClick={() => handleDelete(templateRowData)}
              disabled={isDeleteButtonDisabled}
            >
              삭제
            </Button>
          </Box>
        </Box>
      </Modal>

      <Modal open={isModalOpen} onClose={handleModalClose}>
        <Box sx={vpcTableStyle}>
          <Toolbar
            sx={{
              pl: { sm: 2 },
              pr: { xs: 1, sm: 1 },
            }}
          >
            <Typography sx={{ flex: '1 1 100%', color: 'grey' }} variant="h6" id="tableTitle" component="div">
              {templateRowData
                ? templateRowData.targetCsp2
                  ? `선택된 템플릿 - ${templateRowData.serviceTemplateName}(${templateRowData.targetCsp1} , ${templateRowData.targetCsp2})`
                  : `선택된 템플릿 - ${templateRowData.serviceTemplateName}(${templateRowData.targetCsp1})`
                : '선택된 템플릿 없음'}
            </Typography>
            <Typography sx={{ flex: '1 1 100%' }} variant="h5" id="tableTitle" component="div">
              VPC 목록
            </Typography>
          </Toolbar>
          <VPCGrid templateRowData={templateRowData} closeModal={closeModal}></VPCGrid>
        </Box>
      </Modal>
    </>
  );
}
