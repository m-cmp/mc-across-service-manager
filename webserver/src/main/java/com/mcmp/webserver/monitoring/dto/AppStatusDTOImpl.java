package com.mcmp.webserver.monitoring.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@Data
@RequiredArgsConstructor
public class AppStatusDTOImpl implements AgentStatusDTO {

	private final String status;
	private final Long cnt;
}
