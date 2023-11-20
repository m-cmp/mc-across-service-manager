package com.mcmp.multiCloud.healthCheck;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.resourcemanager.compute.ComputeManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.NotFoundException;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.compute.v1.AggregatedListInstancesRequest;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.InstancesScopedList;
import com.google.cloud.compute.v1.InstancesSettings;
import com.mcmp.multiCloud.exception.InvalidInputException;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
/**
 * CSP HealthCheck  서비스 클래스
 * 
 * @details CSP HealthCheck  서비스 클래스
 * @author 박성준
 *
 */

@Slf4j
@Component
public class HealthCheck {
	//credentail data
	@Value("${aws.access_key}") private String accessKeyId;
	@Value("${aws.secret_access_key}") private String secretAccessKey;
	@Value("${azure.subscription_id}") private String subscriptionId;
	@Value("${azure.tenant_id}") private String tenantId;
	@Value("${azure.client_id}") private String clientId;
	@Value("${azure.client_secret}") private String clientSecret;
	@Value("${gcp.credential_path}") private String credentialPath;
	
	public String AwsHealthCheck(String vmInstanceId) {
		log.info("[AWS health check] vmInstanceId: {}", vmInstanceId);
		String status = "HEALTH_CHECK_FAILED";
		//추후 inventory db data 입력
		System.setProperty("aws.accessKeyId", accessKeyId);
		System.setProperty("aws.secretAccessKey", secretAccessKey);
		
		Region region = Region.AP_NORTHEAST_2;
		
		//Ec2 Client 생성
		Ec2Client ec2 = Ec2Client.builder().region(region)
				.credentialsProvider(SystemPropertyCredentialsProvider.create()).build();

		String nextToken = null;
		try {
			do {
				// vmInstanceId를 받아 해당하는 DescribeInstances 생성
				DescribeInstancesRequest request = DescribeInstancesRequest.builder().instanceIds(vmInstanceId)
						.nextToken(nextToken).build();
				DescribeInstancesResponse response = ec2.describeInstances(request);
				for (Reservation reservation : response.reservations()) {
					for (Instance instance : reservation.instances()) {
//						log.info("[AWS health check] Id: {}", instance.instanceId());
//						log.info("[AWS health check] Status: {}", instance.state().name());
						log.debug("[AWS health check] Id: {}", instance.instanceId());
						log.debug("[AWS health check] Status: {}", instance.state().name());
						status = instance.state().name().toString();
					}
				}
				nextToken = response.nextToken();
			} while (nextToken != null);

		} catch (Ec2Exception e) {
			System.err.println(e.awsErrorDetails().errorCode());
			//System.exit(1);
		}
		ec2.close();
		
		log.info("[AWS health check] response status: {}", status.toUpperCase());
		return status.toUpperCase();
	}

	public String AzureHealthCheck(String vmInstanceId) {
		log.info("[Azure health check] vmInstanceId: {}", vmInstanceId);
		String status = "HEALTH_CHECK_FAILED";
		
		//Azure 환경
		AzureEnvironment environment = AzureEnvironment.AZURE;
		
		//Azure 인증 
		TokenCredential clientSecretCredential = new ClientSecretCredentialBuilder()
			     .tenantId(tenantId)
			     .clientId(clientId)
			     .clientSecret(clientSecret)
			     .build();
		AzureProfile azureProfile = new AzureProfile(tenantId, subscriptionId, environment);
		
		//ComputeManager 생성
		ComputeManager computeManager = ComputeManager.authenticate(clientSecretCredential, azureProfile);
		
		
		// 모든 VM 정보
		 for (VirtualMachine vm : computeManager.virtualMachines().list()) {
			 if(vm.id().toString().toLowerCase().equals(vmInstanceId.toLowerCase())) {
				 String[] returndata = vm.powerState().toString().split("/");
				 status = returndata[1];
//				 log.info("[Azure health check] Id: {}", vmInstanceId);
//				 log.info("[Azure health check] Status: {}", status);
				 log.debug("[Azure health check] Id: {}", vmInstanceId);
				 log.debug("[Azure health check] Status: {}", status);
			 }
		 }
		 
		 log.info("[Azure health check] response status: {}", status.toUpperCase());
		 return status.toUpperCase();
	}

	public String GcpHealthCheck(String vmProject, String vmInstanceId) throws IOException {
		log.info("[GCP health check] vmProject: {}, vmInstanceId: {}", vmProject, vmInstanceId);
		String status = "HEALTH_CHECK_FAILED";
		ServiceAccountCredentials serviceAccountCredentials = null;
		
		//gcp 사용자 계정 키 파일 받아옴
		 try {
			 serviceAccountCredentials = ServiceAccountCredentials.fromStream(new FileInputStream(credentialPath));	
		} catch (FileNotFoundException e) {
			log.error("[GCP health check] Invalid gcp credentials");
			throw new InvalidInputException("Invalid gcp credentials");
		}
		
		InstancesSettings instancesSettings = InstancesSettings
				.newBuilder()
				.setCredentialsProvider(FixedCredentialsProvider.create(serviceAccountCredentials))
				.build();
		
		// Instance client 생성
		try (InstancesClient instancesClient = InstancesClient.create(instancesSettings)) {

			// vm 나열
			AggregatedListInstancesRequest aggregatedListInstancesRequest = AggregatedListInstancesRequest.newBuilder()
					.setProject(vmProject).setMaxResults(5).build();

			InstancesClient.AggregatedListPagedResponse response = instancesClient.aggregatedList(aggregatedListInstancesRequest);
			
			for (Map.Entry<String, InstancesScopedList> zoneInstances : response.iterateAll()) {
				//String zone = zoneInstances.getKey();
				if (!zoneInstances.getValue().getInstancesList().isEmpty()) {
					//System.out.printf("Instances at %s: ", zone.substring(zone.lastIndexOf('/') + 1));
					for (com.google.cloud.compute.v1.Instance instance : zoneInstances.getValue().getInstancesList()) {
						if(instance.getId() == Long.parseLong(vmInstanceId)) {
//							log.info("[GCP health check] Id: {}", vmInstanceId);
//							log.info("[GCP health check] Status: {}", instance.getStatus());
							log.debug("[GCP health check] Id: {}", vmInstanceId);
							log.debug("[GCP health check] Status: {}", instance.getStatus());
							status =  instance.getStatus();
						}
							
					}
				}
			}
			
			log.info("[GCP health check] response status: {}", status.toUpperCase());
			return status.toUpperCase();
		}catch (NotFoundException e) {
			log.error("[GCP health check] Invalid GCP project Id");
			throw new InvalidInputException("Invalid GCP project Id");
		}
	}

	
}
