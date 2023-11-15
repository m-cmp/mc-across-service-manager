package com.mcmp.webserver.monitoring.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@Getter
@RequiredArgsConstructor
public class PingHostParamDTO {
	public String hostName;
	public String ip;
}
