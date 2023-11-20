import { Button, Grid } from '@mui/material';
import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import MainCard from '@/components/cards/MainCard';
import { useServiceDetail, ServiceDetailData, ServiceTemplateData } from '@/network/apis/useServiceDetail';
import ServiceMonitoring from '../monitoring/ServiceMonitoring';
import MigrationModal from './MigrationModal';
import ServiceDetailGrid from './ServiceDetailGrid';

/**
 * 서비스 상세 페이지
 */
export default function ServiceDetail() {
  const navigate = useNavigate();
  const serviceId = parseInt(useLocation().pathname.split('/')[2]);

  // 서비스 상세 정보
  const [service, setService] = useState<ServiceDetailData[]>([]);
  // 마이그레이션 템플릿 리스트 정보
  const [templates, setTemplates] = useState<ServiceTemplateData[]>([]);
  //모니터링
  const [hostNames, setHostNames] = useState<string[]>([]);
  const [vmNames, setVmNames] = useState<string[]>([]);
  //마이그레이션 모달 open 상태[true: Modal open / false: Modal close]
  const [isModalOpen, setModalOpen] = useState(false);

  // 서비스 상세 조회 API
  const detailData = useServiceDetail(serviceId);

  //모니터링 hostname 업데이트
  useEffect(() => {
    setHostNames(
      service.map(service =>
        service.csp === 'AWS' ? 'ip-' + service.vmInstancePrivateIp.replace(/\./g, '-') : service.vmInstanceName
      )
    );
  }, [service]);

  useEffect(() => {
    setVmNames(service.map(service => service.vmInstanceName));
  }, [service]);

  //서비스 상세, 마이그레이션 템플릿 조회 데이터 업데이트
  useEffect(() => {
    if (detailData.isSuccess) {
      setService([detailData.data]);
      setTemplates(detailData.data.templateDTOs);
    }
  }, [detailData.data, detailData.isSuccess]);

  // 서비스 마이그레이션 버튼 클릭 이벤트
  const handleMigrationServiceButtonClick = () => {
    console.log(service);
    // migrationServiceQuery.refetch();
    openModal();
  };

  //Modal Open으로 상태 변경
  const openModal = () => {
    setModalOpen(true);
  };
  //Modal Close로 상태 변경
  const closeModal = () => {
    setModalOpen(false);
  };

  return (
    <>
      {/* 버튼 */}
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '10px' }}>
        <Button
          style={{ marginRight: '30px' }}
          variant="contained"
          color="secondary"
          disabled={!detailData.data || detailData.data.applicationActivateYn !== 'Y'}
          onClick={handleMigrationServiceButtonClick}
        >
          마이그레이션
        </Button>
        <Button
          variant="contained"
          color="primary"
          onClick={() => {
            navigate(-1);
          }}
        >
          목록으로
        </Button>
      </div>
      <Grid container rowSpacing={4.5} columnSpacing={2}>
        <Grid item xs={12} sm={12} md={12} lg={12}>
          {/* 인스턴스 및 애플리케이션 정보 */}
          <MainCard title="인스턴스 및 애플리케이션 정보" content={true} contentSX={{ width: '100%' }}>
            <ServiceDetailGrid serviceId={serviceId} data={service} />
          </MainCard>
        </Grid>
        <Grid item xs={12} sm={12} md={12} lg={12}>
          {/* 서비스 모니터링 */}
          <ServiceMonitoring vmNames={vmNames} hostNames={hostNames} />
        </Grid>
      </Grid>
      {/* 마이그레이션 모달 */}
      {isModalOpen && (
        <MigrationModal
          serviceId="42"
          data={service}
          templates={templates}
          isModalOpen={isModalOpen}
          closeModal={closeModal}
        />
      )}
    </>
  );
}
