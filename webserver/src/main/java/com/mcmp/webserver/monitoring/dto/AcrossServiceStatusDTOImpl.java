package com.mcmp.webserver.monitoring.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@Data
@RequiredArgsConstructor
public class AcrossServiceStatusDTOImpl implements AcrossServiceStatusDTO {

	private final String status;
	private final Long cnt;

}
