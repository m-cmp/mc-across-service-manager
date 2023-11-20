package com.mcmp.controller.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
/**
*
* @author : Jihyeong Lee
* @Project : mcmp-conf/controller
* @version : 1.0.0
* @date : 11/7/23
* @class-description : dto class for extracting ansible result
*
**/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResultDTO {
    private Map<String, Map<String, String>> stats;
    private int changed;
    private int failures;
    private int ok;
    private int unreachable;
    private int ignored;
}
