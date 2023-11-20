package com.mcmp.multiCloud.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mcmp.multiCloud.exception.FileManagerException;

import lombok.extern.slf4j.Slf4j;

/**
 * File Manager 서비스 클래스
 * 
 * @details CSP HealthCheck 서비스 클래스
 * @author 박성준
 *
 */

@Slf4j
@Component
public class FileManager {
	@Value("${terraform.path}")
	private String terraformPath;

	public File createDirectory(String serviceInstanceId) {

		if (!(new File(terraformPath)).exists()) {
			log.error("[createDirectory] 'terraform path' is Ivalid path");
			throw new FileManagerException("Ivalid terraform path");
		}
		// 폴더 경로
		File directory = new File(terraformPath + serviceInstanceId);
		log.info("[createDirectory] Service Instance Id: {}", serviceInstanceId);

		// 폴더가 없는 경우 생성
		if (!directory.exists()) {
			try {
				directory.mkdirs();
				// System.out.println("Directory has been created");
				log.info("[createDirectory] new directory path: {}", directory.toPath().toString());
			} catch (Exception e) {
				log.error("[createDirectory] create directory failed");
				e.getStackTrace();
			}
		} else {
			log.info("[createDirectory] existing directory path: {}", directory.toPath().toString());
		}
		return directory;
	}

	public File copyFile(String originalPath, String newPath) throws IOException {

		// 원본 파일 경로
		File originalFilePath = new File(originalPath);
		File newPathCheck = new File(newPath);

		File newFilePath = null;

		if (!originalFilePath.exists()) {
			log.info("[copyFile] '{}' is Invalid source path", originalPath);
			throw new FileManagerException("Ivalid source path");
		}

		if (!newPathCheck.exists()) {
			log.info("[copyFile] '{}' is Invalid copy path", newPath);
			throw new FileManagerException("Ivalid copy path");
		}

		// 원본 파일 경로 확인
		String filename = originalFilePath.getName();
		log.info("[copyFile] file name: {}", originalFilePath.getName());

		// 복사할 파일 경로
		newFilePath = new File(newPath + "/" + filename);
		log.info("[copyFile] copy file path: {}", newFilePath.toString());


		// .tf 파일 복사
		if (filename.indexOf(".tf") > -1 || filename.indexOf(".sh") > -1) {
			try {
				Files.copy(originalFilePath.toPath(), newFilePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
				log.info("[copyFile] copy successful");
			} catch (Exception e) {
				e.getStackTrace();
			}
		}

		return newFilePath;
	}

	public boolean deleteDirectory(String path) {
		File deleteDirectory = new File(path);
		log.info("[deleteDirectory] delete directory path: {}", deleteDirectory.toString());

		if (!deleteDirectory.exists()) {
			log.error("[deleteDirectory] '{}' is Ivalid path", path);
			throw new FileManagerException("Ivalid directory path");
		}
		try {
			// 삭제 폴더의 파일 리스트
			File[] deleteDirectoryList = deleteDirectory.listFiles();

			for (int i = 0; i < deleteDirectoryList.length; i++) {
				if (deleteDirectoryList[i].isFile()) {
					deleteDirectoryList[i].delete();
					log.info("[deleteDirectory] '{}' File deleted", deleteDirectoryList[i].getName());
				} else {
					// 재귀함수
					deleteDirectory(deleteDirectoryList[i].getPath());
					log.info("[deleteDirectory] '{}' Directory deleted", deleteDirectoryList[i]);
				}
				deleteDirectoryList[i].delete();
			}
			// 폴더 삭제
			deleteDirectory.delete();

			if (!deleteDirectory.exists()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void deleteFile(String path, String fileName) {
		File deleteDirectory = new File(path);
		log.info("[deleteFile] file name: {}", fileName);

		if (!deleteDirectory.exists()) {
			log.error("[deleteDirectory] '{}' is Ivalid path", path);
			throw new FileManagerException("Ivalid file path");
		}

		try {
			File[] deleteDirectoryList = deleteDirectory.listFiles(); // 파일리스트 얻어오기

			for (int i = 0; i < deleteDirectoryList.length; i++) {
				if (deleteDirectoryList[i].getName().equals(fileName)) {
					deleteDirectoryList[i].delete();
					log.info("[deleteFile] '{}' file deleted", deleteDirectoryList[i].getName());			

				}
			}

		} catch (Exception e) {
			e.getStackTrace();
		}

	}
}
