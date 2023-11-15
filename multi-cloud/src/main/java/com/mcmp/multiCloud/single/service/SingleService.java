package com.mcmp.multiCloud.single.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.mcmp.multiCloud.dto.InstanceDTO;
import com.mcmp.multiCloud.entity.InstanceEntity;
import com.mcmp.multiCloud.entity.VpcEntity;
import com.mcmp.multiCloud.exception.DbNotFoundException;
import com.mcmp.multiCloud.exception.FileManagerException;
import com.mcmp.multiCloud.exception.InvalidInputException;
import com.mcmp.multiCloud.healthCheck.HealthCheck;
import com.mcmp.multiCloud.manager.FileManager;
import com.mcmp.multiCloud.manager.CommandManager;
import com.mcmp.multiCloud.repository.InstanceRepository;
import com.mcmp.multiCloud.repository.VpcRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Multi-Cloud 싱글 서비스 클래스
 * 
 * @details Multi-Cloud 싱글 서비스 클래스
 * @author 박성준
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SingleService {

	private final FileManager fileManager;
	private final CommandManager commandManager;
	private final InstanceRepository instanceRepository;
	private final VpcRepository vpcRepository;
	private final HealthCheck healthCheck;

	public Map<String, String> createInstance(InstanceDTO dto) throws IOException, ParseException {

		log.info("[createInstance] InstanceDTO: {}", dto);

		Map<String, String> responseData = null;
		int count = 0;

		// 디렉토리 생성
		File directoryPath = fileManager.createDirectory(dto.getServiceInstanceId());

		// 디렉토리 생성 확인
		if (!directoryPath.exists()) {
			log.info("[createInstance] '{}' is Invalid create path", directoryPath.toString());
			throw new FileManagerException("Invalid create path");
		}
		// 파일 복사
		for (String key : dto.getTfPath().keySet()) {
			// tf 파일 복사
			fileManager.copyFile(dto.getTfPath().get(key).toString(), directoryPath.toString());

			File scriptPath = new File(dto.getTfPath().get(key).toString().replace(".tf", ".sh"));

			// sh파일 존재 시 복사 후 실행
			if (scriptPath.exists()) {
				fileManager.copyFile(scriptPath.toString(), directoryPath.toString());
				File newSriptPath = new File(directoryPath.toString() + "/" + scriptPath.getName());

				// sed 명령어로 리소스 + s'service id'
				commandManager.command("source " + newSriptPath.toString() + " s" + dto.getServiceId() + " "
						+ newSriptPath.toString().replace(".sh", ".tf"));
			}
		}

		// plug in 경로
		File pluginPath = new File(directoryPath + "/.terraform");

		// plug in 설치 진행
		while (!pluginPath.exists()) {
			log.info("[createInstance] plugin check: {}", pluginPath.exists());
			commandManager.command("terraform -chdir=" + directoryPath.toString() + " init -upgrade");

			// plug in 3번 이상 실패 시 예외 처리
			if (count > 3) {
				log.error("[createInstance] plug in install Failed");
				throw new FileManagerException("plug in install Failed");
			}
			count += 1;
		}

		log.info("[createInstance] plugin check: {}", pluginPath.exists());

		// terraform apply
		commandManager.command("terraform -chdir=" + directoryPath.toString()
				+ " apply -auto-approve -json -var \"instance_name\"=\"" + dto.getServiceInstanceId() + "\"");

		// output json 형태로 추출
		JSONObject output = (JSONObject) commandManager
				.command("terraform -chdir=" + directoryPath.toString() + " output -json");

		JSONObject vpnTunnelIp = (JSONObject) output.get("tunnel_ip");
		String tunnelIp = (vpnTunnelIp != null) ? vpnTunnelIp.get("value").toString() : "";
		String vpcCidr = ((JSONObject) output.get("vpc_cidr")).get("value").toString();
		String subnetCidr = ((JSONObject) output.get("subnet_cidr")).get("value").toString();

		// insert VPC DB
		VpcEntity newVpcEntity = VpcEntity.builder()
				.serviceInstanceId(dto.getServiceInstanceId())
				.vpcCidr(vpcCidr)
				.subnetCidr(subnetCidr)
				.vpcCreateDate(LocalDateTime.now())
				.vpnTunnelIp(tunnelIp)
				.tfstateFilePath(directoryPath.toString())
				.csp(dto.getCsp()).build();
		vpcRepository.save(newVpcEntity);

		//VPC DB insert 확인
		boolean checkVpcDbInsert = ObjectUtils
				.isNotEmpty(vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId()));
		//실패 시 디렉토리 삭제, 생성된 인스턴스 삭제
		if (!checkVpcDbInsert) {
			log.error("[createInstance] VPC DB insert failed");
			commandManager.command("terraform -chdir=" + directoryPath.toString() + " destroy -auto-approve -json");
			fileManager.deleteDirectory(directoryPath.toString());
			throw new DbNotFoundException("VPC DB not Found(insert failed)");
		}

		String instanceId = ((JSONObject) output.get("instance_id")).get("value").toString();
		String instanceName = ((JSONObject) output.get("instance_name")).get("value").toString();
		JSONObject instanceStatus = ((JSONObject) output.get("instance_status"));
		String instancepublicIp = ((JSONObject) output.get("public_ip")).get("value").toString();
		String instanceprivateIp = ((JSONObject) output.get("private_ip")).get("value").toString();
		String memoryType = ((JSONObject) output.get("memory_type")).get("value").toString();
		JSONObject gcpHealthcheckFlag = (JSONObject) output.get("gcp_healthcheck_flag");

		// insert Instance DB
		InstanceEntity newInstanceEntity = InstanceEntity.builder()
				.serviceInstanceId(dto.getServiceInstanceId())
				.serviceId(dto.getServiceId())
				.csp(dto.getCsp())
				.vmInstanceId(instanceId)
				.vmInstanceName(instanceName)
				.vmInstanceStatus(
						(instanceStatus != null) ? instanceStatus.get("value").toString().toUpperCase() : "LOADING")
				.vmInstancePublicIp(instancepublicIp)
				.vmInstancePrivateIp(instanceprivateIp)
				.vmCreateDate(LocalDateTime.now())
				.vmMemoryType(memoryType).agentActivateYn("N")
				.agentDeployYn("N")
				.gcpHealthcheckFlag(
						(gcpHealthcheckFlag != null) ? gcpHealthcheckFlag.get("value").toString() : "notGCP")
				.tfstateFilePath(directoryPath.toString())
				.vpcId(vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId()).getVpcId()).build();
		instanceRepository.save(newInstanceEntity);

		// Instance DB insert 확인
		boolean checkInstanceDbInsert = instanceRepository.existsById(dto.getServiceInstanceId());
		if (checkInstanceDbInsert) {
			log.info("[createInstance] DB insert successful");

			// 전달 데이터
			responseData = Map.of("service_instance_id", dto.getServiceInstanceId(), "public_ip", instancepublicIp,
					"vm_instance_id", instanceId);

		} else {
			// DB 저장 실패 시 디렉토리 삭제, 생성된 인스턴스 삭제
			log.error("[createInstance] DB insert failed");
			commandManager.command("terraform -chdir=" + directoryPath.toString() + " destroy -auto-approve -json");
			fileManager.deleteDirectory(directoryPath.toString());
			throw new DbNotFoundException("Instance DB not Found(insert failed)");
		}

		log.info("[createInstance] response data: {}", responseData);

		return responseData;
	}

	public String deleteInstance(InstanceDTO dto) throws ParseException {

		log.info("[deleteInstance] InstanceDTO: {}", dto);

		String msg = "Delete Instance Error";
		// 삭제 디렉토리 경로
		File directoryPath = new File(dto.getTfstateFilePath());

		// 디렉터리 확인
		if (!directoryPath.exists()) {
			log.info("[deleteInstance] '{}' is Invalid delete path", directoryPath.toString());
			throw new FileManagerException("Ivalid directory path");
		}

		log.info("[deleteInstance] Directory exist check: {}", directoryPath.exists());
		log.info("[deleteInstance] Directory name: {}", directoryPath.getName());
		log.info("[deleteInstance] service instance id: {}", dto.getServiceInstanceId());

		// 디렉토리명과 서비스명 확인
		if (!directoryPath.getName().equals(dto.getServiceInstanceId())) {
			log.error("[deleteInstance] '{}' is Invalid path or '{}' is Invalid service instance id",
					directoryPath.toString(), dto.getServiceInstanceId());
			throw new InvalidInputException("Ivalid directory path or service instance id");
		}
		// 인스턴스 전부 삭제
		commandManager.command("terraform -chdir=" + dto.getTfstateFilePath() + " destroy -auto-approve -json");

		// DB data 확인
		boolean checkDbInstance = instanceRepository.existsById(dto.getServiceInstanceId());
		boolean checkDbInstanceDelete = false;
		log.info("[deleteInstance] DB data exist check: {}", checkDbInstance);

		VpcEntity dbVpc = vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId());
		boolean checkDbVpc = ObjectUtils.isNotEmpty(dbVpc);
		boolean checkDbVpcDelete = false;

		// Instance DB, VPC DB data 삭제
		if (checkDbInstance) {
			instanceRepository.deleteById(dto.getServiceInstanceId());
			checkDbInstanceDelete = !instanceRepository.existsById(dto.getServiceInstanceId());
		} else {
			log.error("[deleteInstance] Instance DB not found");
			fileManager.deleteDirectory(dto.getTfstateFilePath());

			throw new DbNotFoundException("Instance DB not found");
		}

		if (checkDbVpc) {
			vpcRepository.deleteById(dbVpc.getVpcId());

			checkDbVpcDelete = !vpcRepository.existsById(dbVpc.getVpcId());
		} else {
			log.error("[deleteInstance] VPC DB not found");
			fileManager.deleteDirectory(dto.getTfstateFilePath());

			throw new DbNotFoundException("VPC DB not found");
		}

		// Instance DB, VPC DB data 삭제 후 디렉토리 삭제
		if (checkDbInstanceDelete && checkDbVpcDelete) {
			log.info("[deleteInstance] Instance DB data delete successful");
			log.info("[deleteInstance] Vpc DB data delete successful");
			boolean checkDirDelete = fileManager.deleteDirectory(dto.getTfstateFilePath());

			if (checkDirDelete) {
				log.info("[deleteInstance] Directory delete successful");
				// 전달 데이터
				msg = "Deleted Successful";
			} else {
				log.error("[deleteInstance] Directory delete failed");
				throw new FileManagerException("Directory delete failed");
			}
		} else {
			log.error("[deleteInstance] Instance or VPC DB delete failed");
			fileManager.deleteDirectory(dto.getTfstateFilePath());
			throw new DbNotFoundException("Instance or VPC DB(delete failed)");
		}

		// 전달 데이터
		log.info("[deleteInstance] response message: {}", msg);
		return msg;
	}

	public InstanceDTO healthCheck(String serviceInstanceId, String csp, String vmInstanceId) throws IOException {

		log.info("[health check] serviceInstanceId: {}, csp : {}, vmInstacneId : {}", serviceInstanceId, csp,
				vmInstanceId);

		String status = "HEALTH_CHECK_FAILED";
		InstanceEntity update = null;

		// Instance DB 확인
		update = instanceRepository.findByServiceInstanceId(serviceInstanceId);
	

		boolean checkDbInstance = ObjectUtils.isNotEmpty(update);
		boolean checkVmInstanceId = update.getVmInstanceId().equals(vmInstanceId);
		log.info("[health check] DB exist check: {}", checkDbInstance);
		log.info("[health check] vm Instance Id check: {}", checkVmInstanceId);

		// DB 확인
		if (!checkDbInstance) {
			log.error("[health check] DB not found");
			throw new DbNotFoundException("DB not found(health check)");
		}

		// vmInstanceId와 일치 판단
		if (!checkVmInstanceId) {
			log.error("[health check] Invalid Vm Instance Id");
			throw new InvalidInputException("Invalid Vm Instance Id");
		}

		// aws health check
		if ("AWS".equals(csp.toUpperCase())) {
			log.info("[health check] csp: {}", csp);
			status = healthCheck.AwsHealthCheck(vmInstanceId);

			if (status.indexOf("FAILED") > -1) {
				log.error("[health check] '{}' {}", csp.toUpperCase(), status);
				// DB 업데이트
				update.setVmInstanceStatus(status);
				instanceRepository.save(update);
				throw new InvalidInputException(csp.toUpperCase() + " " + status);
			}
		}

		// azure health check
		else if ("AZURE".equals(csp.toUpperCase())) {
			log.info("[health check] csp: {}", csp);
			status = healthCheck.AzureHealthCheck(vmInstanceId);

			if (status.indexOf("FAILED") > -1) {
				log.error("[health check] '{}' {}", csp.toUpperCase(), status);
				// DB 업데이트
				update.setVmInstanceStatus(status);
				instanceRepository.save(update);
				throw new InvalidInputException(csp.toUpperCase() + " " + status);
			}
		}

		// gcp health check(GcpHealthcheckFlag 사용)
		else if ("GCP".equals(csp.toUpperCase())) {
			log.info("[health check] GcpHealthcheckFlag: {}, csp: {}", update.getGcpHealthcheckFlag(), csp);
			status = healthCheck.GcpHealthCheck(update.getGcpHealthcheckFlag(), vmInstanceId);

			if (status.indexOf("FAILED") > -1) {
				log.error("[health check] '{}' {}", csp.toUpperCase(), status);
				// DB 업데이트
				update.setVmInstanceStatus(status);
				instanceRepository.save(update);
				throw new InvalidInputException(csp.toUpperCase() + " " + status);
			}
		}

		else {
			log.error("[health check] '{}' {}", csp.toUpperCase(), status);
			// DB 업데이트
			update.setVmInstanceStatus(status);
			instanceRepository.save(update);
			throw new InvalidInputException("Invaild csp");
		}

		// DB 업데이트
		update.setVmInstanceStatus(status);
		instanceRepository.save(update);

		log.info("[health check] resonse data {}", status);
		return InstanceDTO.of(update);
	}
}
