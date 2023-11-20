package com.mcmp.controller.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;


public class JsonUtil {
    private ObjectMapper objectMapper;

    public JsonUtil() {
        this.objectMapper = new ObjectMapper();
    }

    public static <T> T readResult(String output, final Class<T> type) {

        T obj = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            obj =  objectMapper.readValue(output, type);
        } catch (IOException e) {
            throw new RuntimeException(output, e);
        }

        return obj;
    }

    public <T> T fromJson(String json, Class<T> type){
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



}
