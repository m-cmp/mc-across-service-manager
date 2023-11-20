package com.mcmp.webserver.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private JsonUtil() {}

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 
     * @param <T>
     * @param json
     * @param type
     * @return
     * @throws JacksonException
     */
    public static <T> T toObject(final String json, final Class<T> type) throws JacksonException {
        return objectMapper.readValue(json, type);
    }

    /**
     * json 파일을 읽어서 class로 변환
     * @param <T>
     * @param jsonFile json 파일 위치
     * @param type  json 변환 class
     * @return json 변환 class 타입
     * @throws IOException
     */
    public static <T> T readJsonFiletoObject(File jsonFile, final Class<T> type) throws IOException {
        return objectMapper.readValue(jsonFile, type);
    }

    /**
     * class 값을 json으로 변환 
     * @param obj - 변환 대상 
     * @return json 문자열
     * @throws JacksonException
     */
    public static String toJson(Object obj) throws JacksonException {
        return objectMapper.writeValueAsString(obj);
    }
}
