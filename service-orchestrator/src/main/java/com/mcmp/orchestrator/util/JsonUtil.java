package com.mcmp.orchestrator.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json 관련 Util
 * @author 오승재
 *
 */
public class JsonUtil {
	
	private JsonUtil() {
		
	}
    private static final ObjectMapper objectMapper = new ObjectMapper();
 
    /**
     * json 파일을 읽어서 class로 변환
     * @param <T>
     * @param jsonFile json 파일 위치
     * @param type  json 변환 class
     * @return json 변환 class 타입
     * @throws IOException
     */
    public static <T> T readJsonFiletoObject(String templatePath, final Class<T> type) {

    	T obj = null;
    	File jsonTemplate = new File(templatePath);

        try {
        	if(jsonTemplate.isFile()) {
        		obj =  objectMapper.readValue(jsonTemplate, type);
        	} 
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return obj;
    }
    
    /**
     * 받은 값을 conf 키값을 가진 json으로 변환 
     * @param obj - 변환 대상 
     * @return json 문자열
     * @throws JacksonException
     */
    public static String toJson(Object obj) {
    	
    	String jsonString = null;
    	
        try {
        	jsonString = objectMapper.writeValueAsString(Map.of("conf", obj));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        
		return jsonString;
    }
}
