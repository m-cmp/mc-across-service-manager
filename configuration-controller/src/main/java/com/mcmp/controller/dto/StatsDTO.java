package com.mcmp.controller.dto;

import lombok.Data;

import java.util.List;
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
public class StatsDTO {

    private List<Map<String, Object>> stats;
}
