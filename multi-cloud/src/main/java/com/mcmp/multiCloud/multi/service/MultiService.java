package com.mcmp.multiCloud.multi.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ObjectUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.mcmp.multiCloud.dto.InstanceDTO;
import com.mcmp.multiCloud.entity.AcrossServiceEntity;
import com.mcmp.multiCloud.entity.InstanceEntity;
import com.mcmp.multiCloud.entity.VpcEntity;
import com.mcmp.multiCloud.exception.CommandManagerException;
import com.mcmp.multiCloud.exception.DbNotFoundException;
import com.mcmp.multiCloud.exception.FileManagerException;
import com.mcmp.multiCloud.exception.InvalidInputException;
import com.mcmp.multiCloud.manager.FileManager;
import com.mcmp.multiCloud.manager.CommandManager;
import com.mcmp.multiCloud.repository.AcrossServiceRepository;
import com.mcmp.multiCloud.repository.InstanceRepository;
import com.mcmp.multiCloud.repository.VpcRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Multi-Cloud 멀티 서비스 클래스
 * 
 * @details Multi-Cloud 멀티 서비스 클래스
 * @author 박성준
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiService {

	private final FileManager fileManager;
	private final CommandManager commandManager;
	private final InstanceRepository instanceRepository;
	private final VpcRepository vpcRepository;
	private final AcrossServiceRepository acrossServiceRepository;

	public Map<String, String> createInstance(InstanceDTO dto) throws IOException, ParseException {
		log.info("[createInstance] InstanceDTO: {}", dto);

		Map<String, String> responseData = null;
		File directoryPath = null;

		// VPC 생성 확인
		boolean checkDbVpc = ObjectUtils
				.isNotEmpty(vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId()));
		log.info("[createInstance] VPC DB exist check: {}", checkDbVpc);

		if (!checkDbVpc) {
			log.error("[createInstance] VPC DB not Found");
			throw new DbNotFoundException("VPC DB not Found");
		}

		// 디렉토리 정보
		directoryPath = new File(
				vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId()).getTfstateFilePath());

		// 디렉토리 확인
		if (!directoryPath.exists()) {
			log.info("[createInstance] '{}' is Invalid path", directoryPath.toString());
			throw new FileManagerException("Invalid directory path");
		}

		log.info("[createInstance] Directory exist check: {}", directoryPath.exists());
		log.info("[createInstance] Directory name: {}", directoryPath.getName());
		log.info("[createInstance] service instance id: {}", dto.getServiceInstanceId());

		// 디렉토리명과 서비스명 확인
		if (!directoryPath.getName().equals(dto.getServiceInstanceId())) {
			log.info("[createInstance] '{}' is Invalid path or '{}' is Invalid service instance id",
					directoryPath.toString(), dto.getServiceInstanceId());
			throw new FileManagerException("Invalid directory path or service instance id");
		}
		// 파일 복사
		fileManager.copyFile(dto.getTfPath().get("vm").toString(), directoryPath.toString());

		File scriptPath = new File(dto.getTfPath().get("vm").toString().replace(".tf", ".sh"));

		// sh파일 존재 시 복사 후 실행
		if (scriptPath.exists()) {
			fileManager.copyFile(scriptPath.toString(), directoryPath.toString());

			File newSriptPath = new File(directoryPath.toString() + "/" + scriptPath.getName());

			// sed 명령어로 리소스 + s'service id'
			commandManager.command("source " + newSriptPath.toString() + " a" + dto.getAcrossServiceId() + " "
					+ newSriptPath.toString().replace(".sh", ".tf"));
		}

		// terraform apply
		commandManager.command("terraform -chdir=" + directoryPath.toString()
				+ " apply -auto-approve -json -var \"instance_name\"=\"" + dto.getServiceInstanceId() + "\"");

		// output json 형태로 추출
		JSONObject output = (JSONObject) commandManager
				.command("terraform -chdir=" + directoryPath.toString() + " output -json");

		// output에서 해당하는 값 변수 저장
		String instanceId = ((JSONObject) output.get("instance_id")).get("value").toString();
		String instanceName = ((JSONObject) output.get("instance_name")).get("value").toString();
		JSONObject instanceStatus = (JSONObject) output.get("instance_status");
		String instancepublicIp = ((JSONObject) output.get("public_ip")).get("value").toString();
		String instanceprivateIp = ((JSONObject) output.get("private_ip")).get("value").toString();
		String memoryType = ((JSONObject) output.get("memory_type")).get("value").toString();
		JSONObject gcpHealthcheckFlag = (JSONObject) output.get("gcp_healthcheck_flag");

		// insert Instance DB
		InstanceEntity newInstanceEntity = InstanceEntity.builder()
				.serviceInstanceId(dto.getServiceInstanceId())
				.acrossServiceId(dto.getAcrossServiceId())
				.vmInstanceId(instanceId)
				.csp(dto.getCsp())
				.vmInstanceName(instanceName)
				.vmInstanceStatus(
						(instanceStatus != null) ? instanceStatus.get("value").toString().toUpperCase() : "LOADING")
				.vmInstancePublicIp(instancepublicIp)
				.vmInstancePrivateIp(instanceprivateIp)
				.vmCreateDate(LocalDateTime.now())
				.vmMemoryType(memoryType)
				.agentActivateYn("N")
				.agentDeployYn("N")
				.gcpHealthcheckFlag(
						(gcpHealthcheckFlag != null) ? gcpHealthcheckFlag.get("value").toString() : "notGCP")
				.tfstateFilePath(directoryPath.toString())
				.vpcId(vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId()).getVpcId()).build();
		instanceRepository.save(newInstanceEntity);

		// insert 확인
		boolean checkDbInsert = instanceRepository.findById(dto.getServiceInstanceId()).isPresent();
		if (checkDbInsert) {
			log.info("[createInstance] Instance create successful");
			// 전달 데이터
			responseData = Map.of("service_instance_id", dto.getServiceInstanceId(), "public_ip", instancepublicIp,
					"vm_instance_id", instanceId);
		
		//실패 시 디렉토리 삭제, 생성된 인스턴스 삭제
		} else {
			log.error("[createInstance] DB insert failed");
			fileManager.deleteFile(directoryPath.toString(), "vm.tf");
			commandManager.command("terraform -chdir=" + directoryPath.toString() + " apply -auto-approve -json");
			throw new DbNotFoundException("Instance DB not Found(insert failed)");
		}

		log.info("[createInstance] response data: {}", responseData);

		return responseData;
	}

	public Map<String, String> createVpc(InstanceDTO dto) throws IOException, ParseException {

		log.info("[createVpc] InstanceDTO: {}", dto);

		Map<String, String> responseData = null;
		int count = 0;

		// 디렉토리 생성
		File directoryPath = fileManager.createDirectory(dto.getServiceInstanceId());

		// 디렉터리 생성 확인
		if (!directoryPath.exists()) {
			log.info("[createInstance] '{}' is Invalid create path", directoryPath.toString());
			throw new FileManagerException("Invalid create path");
		}

		// 파일 복사(azure의 경우 vgw 파일도 함께 복사)
		for (String key : dto.getTfPath().keySet()) {
//			if ("null".equals(dto.getTfPath().get(key)))
//				break;
			// 파일 복사
			fileManager.copyFile(dto.getTfPath().get(key).toString(), directoryPath.toString());

			File scriptPath = new File(dto.getTfPath().get(key).toString().replace(".tf", ".sh"));

			// sh파일 존재 시 복사 후 실행
			if (scriptPath.exists()) {
				fileManager.copyFile(scriptPath.toString(), directoryPath.toString());
				File newSriptPath = new File(directoryPath.toString() + "/" + scriptPath.getName());

				// sed 명령어로 vpc 리소스 + s'service id'
				commandManager.command("source " + newSriptPath.toString() + " a" + dto.getAcrossServiceId() + " "
						+ newSriptPath.toString().replace(".sh", ".tf"));
			}
		}

		// plug in 경로
		File pluginPath = new File(directoryPath + "/.terraform");

		// plug in 설치 진행
		while (!pluginPath.exists()) {
			log.info("[createVpc] plugin check: {}", pluginPath.exists());
			commandManager.command("terraform -chdir=" + directoryPath.toString() + " init -upgrade");

			// plug in 3번 이상 실패 시 예외 처리
			if (count > 3) {
				log.error("[createInstance] plug in install Failed");
				throw new CommandManagerException("plug in install Failed");
			}
			count += 1;
		}
		log.info("[createVpc] plugin check: {}", pluginPath.exists());

		// terraform apply
		commandManager.command("terraform -chdir=" + directoryPath.toString() + " apply -auto-approve -json");

		// output json 형태로 추출
		JSONObject output = (JSONObject) commandManager
				.command("terraform -chdir=" + directoryPath.toString() + " output -json");

		JSONObject vpnTunnelIp = (JSONObject) output.get("tunnel_ip");
		String tunnelIp = (vpnTunnelIp != null) ? vpnTunnelIp.get("value").toString() : "";
		String vpcCidr = ((JSONObject) output.get("vpc_cidr")).get("value").toString();
		String subnetCidr = ((JSONObject) output.get("subnet_cidr")).get("value").toString();

		// vpc 확인(기존 vpc 재사용 시 vpc db에 내용을 추가하지 않음)
		boolean checkDbVpc = ObjectUtils
				.isNotEmpty(vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId()));

		if (!checkDbVpc) {

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
			boolean checkDbInsert = ObjectUtils
					.isNotEmpty(vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId()));

			if (checkDbInsert) {
				log.info("[createVpc] vpc create successful");
				
			//실패 시 디렉토리 삭제, 생성된 인스턴스 삭제
			} else {
				log.error("[createInstance] DB insert failed");
				commandManager.command("terraform -chdir=" + directoryPath.toString() + " destroy -auto-approve -json");
				fileManager.deleteDirectory(directoryPath.toString());
				throw new DbNotFoundException("Instance DB not Found(insert failed)");
			}

		} else
			log.info("[createVpc] VPC DB already created");

		// 전달 데이터
		responseData = Map.of("service_instance_id", dto.getServiceInstanceId(), "tunnel_ip", tunnelIp,
				"vpc_cidr", vpcCidr);
		log.info("[createVpc] response data: {}", responseData);

		return responseData;
	}

	public Map<String, String> createVpn(InstanceDTO dto, String customerTunnelIp, String customerVpcCidr)
			throws IOException, ParseException {

		log.info("[createVpn] InstanceDTO: {}, customerTunnelIp: {}, customerVpcCidr: {}", dto, customerTunnelIp,
				customerVpcCidr);

		Map<String, String> resonseData = null;
		VpcEntity update = null;

		// VM 인스턴스 생성 확인
		boolean checkDbInstance = instanceRepository.findById(dto.getServiceInstanceId()).isPresent();
		log.info("[createVpn] Instance DB exist check: {}", checkDbInstance);

		if (!checkDbInstance) {
			log.error("[createVpn] Instance DB not Found");
			throw new DbNotFoundException("Instance DB not Found");
		}

		// 파일 경로 받기
		File directoryPath = new File(
				instanceRepository.findById(dto.getServiceInstanceId()).get().getTfstateFilePath());

		// 디렉터리 확인
		if (!directoryPath.exists()) {
			log.info("[createVpn] '{}' is Invalid path", directoryPath.toString());
			throw new FileManagerException("Invalid directory path");
		}
		
		log.info("[createVpn] Directory exist check: {}", directoryPath.exists());
		log.info("[createVpn] Directory name: {}", directoryPath.getName());
		log.info("[createVpn] service instance id: {}", dto.getServiceInstanceId());

		// 디렉토리명과 서비스명 확인
		if (!directoryPath.getName().equals(dto.getServiceInstanceId())) {
			log.info("[createVpn] '{}' is Invalid path or '{}' is Invalid service instance id",
					directoryPath.toString(), dto.getServiceInstanceId());
			throw new FileManagerException("Invalid directory path or service instance id");
		}

		// 파일 복사
		fileManager.copyFile(dto.getTfPath().get("vpn").toString(), directoryPath.toString());

		File scriptPath = new File(dto.getTfPath().get("vpn").toString().replace(".tf", ".sh"));

		// sh파일 존재 시 복사 후 실행
		if (scriptPath.exists()) {
			fileManager.copyFile(scriptPath.toString(), directoryPath.toString());
			File newSriptPath = new File(directoryPath.toString() + "/" + scriptPath.getName());

			// sed 명령어로 vpc 리소스 + s'service id'
			commandManager.command("source " + newSriptPath.toString() + " a" + dto.getAcrossServiceId() + " "
					+ newSriptPath.toString().replace(".sh", ".tf"));
		}

		// azure는 vpc_cidr list 형태로 전달
		customerVpcCidr = (dto.getCsp().equals("AZURE")) ? "[\\\"" + customerVpcCidr + "\\\"]" : customerVpcCidr;

		// terraform APPLY
		commandManager.command(
				"terraform -chdir=" + directoryPath.toString() + " apply -auto-approve -json -var \"instance_name\"=\""
						+ dto.getServiceInstanceId() + "\" -var \"customer_tunnel_ip\"=\"" + customerTunnelIp
						+ "\" -var \"customer_ip_address_space\"=\"" + customerVpcCidr + "\"");

		// output json 형태로 추출
		JSONObject output = (JSONObject) commandManager
				.command("terraform -chdir=" + directoryPath.toString() + " output -json");

		String vpnTunnelIp = ((JSONObject) output.get("tunnel_ip")).get("value").toString();
		String privateIp = ((JSONObject) output.get("private_ip")).get("value").toString();

		// vpn_tunnel_create_date 업데이트
		try {
			update = vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId());

		} catch (Exception e) {
			throw new NoSuchElementException("No such Element DB");
		}

		boolean checkVpcDb = ObjectUtils.isNotEmpty(update);

		if (checkVpcDb) {
			update.setVpnTunnelCreateDate(LocalDateTime.now());

			// aws는 생성된 tunnel_ip db 저장
			if (dto.getCsp().equals("AWS")) {
				log.info("[createVpn] AWS tunnel create");
				update.setVpnTunnelIp(vpnTunnelIp);
			}
			vpcRepository.save(update);
			
			log.info("[createVpn] vpn create successful");
			resonseData = Map.of("service_instance_id", dto.getServiceInstanceId(), "private_ip", privateIp,
					"tunnel_ip", vpnTunnelIp);
		// 실패 시 vpn 리소스 삭제
		} else {
			log.error("[createVpn] VPC DB not Found");
			fileManager.deleteFile(directoryPath.toString(), "vpn.tf");
			commandManager.command("terraform -chdir=" + directoryPath.toString()
					+ " apply -auto-approve -json -var \"instance_name\"=\"" + dto.getServiceInstanceId() + "\"");
			throw new DbNotFoundException("VPC DB not Found");
		}

		log.info("[createVpn] resonse data {}", resonseData);

		return resonseData;
	}

	public Map<String, String> createGslb(InstanceDTO dto, String publicIp, String customerPublicIp)
			throws IOException, ParseException {

		log.info("[createGslb] InstanceDTO: {}, publicIp: {}, customerPublicIp: {}", dto, publicIp, customerPublicIp);

		Map<String, String> resonseData = null;
		AcrossServiceEntity update = null;

		// VM 인스턴스 생성 확인
		boolean checkDbInstance = instanceRepository.findById(dto.getServiceInstanceId()).isPresent();
		log.info("[createGslb] Instance DB exist check: {}", checkDbInstance);

		if (!checkDbInstance) {
			log.error("[createGSLB] Instance DB not Found");
			throw new DbNotFoundException("Instance DB not Found");
		}

		// 파일 경로 받기
		File directoryPath = new File(
				instanceRepository.findById(dto.getServiceInstanceId()).get().getTfstateFilePath());

		// 디렉토리 확인
		if (!directoryPath.exists()) {
			log.info("[createGslb] '{}' is Invalid path", directoryPath.toString());
			throw new FileManagerException("Invalid directory path");
		}

		log.info("[createGslb] Directory exist check: {}", directoryPath.exists());
		log.info("[createGslb] Directory name: {}", directoryPath.getName());
		log.info("[createGslb] service instance id: {}", dto.getServiceInstanceId());

		// 디렉토리명과 서비스명 확인
		if (!directoryPath.getName().equals(dto.getServiceInstanceId())) {
			log.info("[createGslb] '{}' is Invalid path or '{}' is Invalid service instance id",
					directoryPath.toString(), dto.getServiceInstanceId());
			throw new FileManagerException("Invalid directory path or service instance id");
		}
		// 파일 복사
		fileManager.copyFile(dto.getTfPath().get("gslb").toString(), directoryPath.toString());

		File scriptPath = new File(dto.getTfPath().get("gslb").toString().replace(".tf", ".sh"));

		// sh파일 존재 시 복사 후 실행
		if (scriptPath.exists()) {
			fileManager.copyFile(scriptPath.toString(), directoryPath.toString());
			File newSriptPath = new File(directoryPath.toString() + "/" + scriptPath.getName());

			// sed 명령어로 vpc 리소스 + s'service id'
			commandManager.command("source " + newSriptPath.toString() + " a" + dto.getAcrossServiceId() + " "
					+ newSriptPath.toString().replace(".sh", ".tf"));
		}

		// terraform 명령어
		commandManager.command(
				"terraform -chdir=" + directoryPath.toString() + " apply -auto-approve -json -var \"instance_name\"=\""
						+ dto.getServiceInstanceId() + "\" -var \"customer_public_ip\"=\"" + customerPublicIp + "\"");

		// output json 형태로 추출
		JSONObject output = (JSONObject) commandManager
				.command("terraform -chdir=" + directoryPath.toString() + " output -json");

		String domain = ((JSONObject) output.get("domain")).get("value").toString();
		String gslbWeight = ((JSONObject) output.get("gslb_weight")).get("value").toString();
		String customerGslbWeight = ((JSONObject) output.get("customer_gslb_weight")).get("value").toString();

		Double weight = Double.parseDouble(gslbWeight);
		Double customerWeight = Double.parseDouble(customerGslbWeight);

		// update DB
		update = acrossServiceRepository.findByAcrossServiceId(dto.getAcrossServiceId());	

		boolean checkAcrossDb = ObjectUtils.isNotEmpty(update);

		if (checkAcrossDb) {
			update.setGslbDomain(domain);
			update.setGslbWeight((int) ((weight / (weight + customerWeight)) * 100));
			update.setCustomerGslbWeight((int) ((customerWeight / (weight + customerWeight)) * 100));
			update.setGslbCsp(dto.getCsp());
			acrossServiceRepository.save(update);

			log.info("[createGslb] gslb create successful");
			// 전달 데이터
			resonseData = Map.of("service_instance_id", dto.getServiceInstanceId(), "domain", domain, "public_ip",
					publicIp, "customer_public_ip", customerPublicIp);

		// 실패 시 gslb 리소스 삭제
		} else {
			log.error("[createGslb] Across DB not Found");
			fileManager.deleteFile(directoryPath.toString(), "gslb.tf");
			commandManager.command("terraform -chdir=" + directoryPath.toString()
					+ " apply -auto-approve -json -var \"instance_name\"=\"" + dto.getServiceInstanceId() + "\"");
			throw new DbNotFoundException("Across DB not Found");
		}

		log.info("[createGslb] resonse data {}", resonseData);

		return resonseData;
	}

	public String deleteInstance(InstanceDTO dto, String vmOnlyYn) throws IOException, ParseException {
		log.info("[deleteInstance] InstanceDTO: {}, vmOnlyYn: {}", dto, vmOnlyYn);

		String msg = "Delete Instance Error";
		File directoryPath = new File(dto.getTfstateFilePath());

		// 디렉토리 확인
		if (!directoryPath.exists()) {
			log.info("[deleteInstance] '{}' is Invalid delete path", directoryPath.toString());
			throw new FileManagerException("Invalid directory path");
		}

		log.info("[deleteInstance] Directory exist check: {}", directoryPath.exists());
		log.info("[deleteInstance] Directory name: {}", directoryPath.getName());
		log.info("[deleteInstance] service instance id: {}", dto.getServiceInstanceId());

		// 디렉토리명과 서비스명 확인
		if (!directoryPath.getName().equals(dto.getServiceInstanceId())) {
			log.error("[deleteInstance] '{}' is Invalid path or '{}' is Invalid service instance id",
					directoryPath.toString(), dto.getServiceInstanceId());
			throw new InvalidInputException("Invalid directory path or service instance id");
		}

		// vpc와 관련된 tf파일 제외하고 전부 삭제
		if ("Y".equals(vmOnlyYn.toUpperCase())) {
			fileManager.deleteFile(directoryPath.toString(), "vm.tf");
			fileManager.deleteFile(directoryPath.toString(), "vpn.tf");
			fileManager.deleteFile(directoryPath.toString(), "gslb.tf");

			commandManager.command("terraform -chdir=" + directoryPath.toString() + " apply -auto-approve -json");

			// DB data 확인
			boolean checkDbInstance = instanceRepository.existsById(dto.getServiceInstanceId());
			log.info("[deleteInstance] DB data exist check: {}", checkDbInstance);

			// DB data 삭제
			if (checkDbInstance) {
				instanceRepository.deleteById(dto.getServiceInstanceId());
				// DB data 삭제 확인
				boolean checkDbInstanceDelete = !instanceRepository.existsById(dto.getServiceInstanceId());

				if (checkDbInstanceDelete) {
					log.info("[deleteInstance] DB data delete successful");
					msg = "Delete Successful";

				} else {
					log.error("[deleteInstance] Instance DB delete failed");
					throw new DbNotFoundException("Instance DB delete failed");
				}

			} else {
				log.error("[deleteInstance] Instance DB not found");
				throw new DbNotFoundException("Instance DB not found");
			}

			// 전부 삭제
		} else if ("N".equals(vmOnlyYn.toUpperCase())) {

			// terraform destroy 
			commandManager.command("terraform -chdir=" + dto.getTfstateFilePath() + " destroy -auto-approve -json");

			// Instance DB, VPC DBa 확인
			boolean checkDbInstance = instanceRepository.existsById(dto.getServiceInstanceId());
			boolean checkDbInstanceDelete = false;
			log.info("[deleteInstance] Instance DB data exist check: {}", checkDbInstance);

			VpcEntity dbVpc = vpcRepository.findOneByServiceInstanceId(dto.getServiceInstanceId());
			boolean checkDbVpc = ObjectUtils.isNotEmpty(dbVpc);
			boolean checkDbVpcDelete = false;

			log.info("[deleteInstance] Vpc DB data exist check: {}", checkDbVpc);

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
				log.error("[deleteInstance] Instance or VPC DB delete faild");
				fileManager.deleteDirectory(dto.getTfstateFilePath());
				throw new DbNotFoundException("Instance or VPC DB not found(delete failed)");
			}

		} else {
			log.error("[deleteInstance] Invalid vmOnlyYn");
			throw new InvalidInputException("Invalid vmOnlyYn");
		}

		// 전달 데이터
		log.info("[deleteInstance] response message: {}", msg);
		return msg;
	}
}
