package com.mcmp.multiCloud.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import com.mcmp.multiCloud.exception.CommandManagerException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommandManager {
	public JSONObject command(String cmd) throws ParseException {
		Process process = null;
		Runtime runtime = Runtime.getRuntime();
		StringBuffer successOutput = new StringBuffer(); // 성공 스트링 버퍼
		StringBuffer errorOutput = new StringBuffer(); // 오류 스트링 버퍼
		BufferedReader successBufferReader = null; // 성공 버퍼
		BufferedReader errorBufferReader = null; // 오류 버퍼
		FileManager fileManager = new FileManager();
		String msg = null; // 메시지
		List<String> cmdList = new ArrayList<String>();
		String cmdSubstring = null;

		// windows 또는 linux 판단
		if (System.getProperty("os.name").indexOf("Windows") > -1) {
			cmdList.add("cmd");
			cmdList.add("/c");
		} else {
			cmdList.add("/bin/bash");
			cmdList.add("-c");
		}
		// 명령어 셋팅
		cmdList.add(cmd);
		String[] array = cmdList.toArray(new String[cmdList.size()]);
		log.info("[command] command: {}", cmdList.toString());
		
		try {

			// 명령어 실행
			process = runtime.exec(array);

			// 정상 동작
			successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));

			while ((msg = successBufferReader.readLine()) != null) {
				if (msg.indexOf("error") > -1 && cmd.indexOf("apply") > -1) { // apply 명렁어 실행 중 에러
					log.error("[command] Apply Command running Error: '{}'", msg);
					command(cmd.replace("apply", "destroy"));
					cmdSubstring = cmd.substring(cmd.indexOf("=")); 
					fileManager.deleteDirectory(cmdSubstring.substring(1, cmdSubstring.indexOf(" ")));
					throw new CommandManagerException("Command running Error");
					
				} else if(msg.indexOf("error") > -1 ) { // destroy 명령어 실핼 중 에러
					log.error("[command] Destroy Command running Error: '{}'", msg);
					throw new CommandManagerException("Command running Error");
					
				}
				successOutput.append(msg + System.getProperty("line.separator"));
			}

			// 실행시 에러
			errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
			while ((msg = errorBufferReader.readLine()) != null) {
				errorOutput.append(msg + System.getProperty("line.separator"));
			}

			// 명령어 끝날때까지 대기
			process.waitFor();

			// 정상 종료
			if (process.exitValue() == 0) {
				log.info("[command] Successful");
				System.out.println(successOutput.toString());
			} else {
				// 비정상 종료
				log.error("[command] Command Termination Error");
				System.out.println(successOutput.toString());

				if (!(cmd.indexOf("init") > -1)) {
					cmdSubstring = cmd.substring(cmd.indexOf("=")); 
					fileManager.deleteDirectory(cmdSubstring.substring(1, cmdSubstring.indexOf(" ")));
					throw new CommandManagerException("Command Termination Error");
				}
			}

			// 실행 에러
			if (!errorOutput.isEmpty()) {
				// 종료 에러 발생
				log.error("[command] Command start Error");
				System.out.println(errorOutput.toString());

				// terraform init를 제외한 명령어 에러 발생 시 종료 - 윈도우 테스트 주석
				if (!(cmd.indexOf("init") > -1)) {
					cmdSubstring = cmd.substring(cmd.indexOf("=")); 
					fileManager.deleteDirectory(cmdSubstring.substring(1, cmdSubstring.indexOf(" ")));
					throw new CommandManagerException("Command result error");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}  finally {
			try {
				process.destroy();
				if (successBufferReader != null)
					successBufferReader.close();
				if (errorBufferReader != null)
					errorBufferReader.close();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		// 결과값
		if (cmd.indexOf("output") > -1) {
			log.info("[command] output result");
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(successOutput.toString());

			return jsonObject;
		}

		return null;
	}
}
