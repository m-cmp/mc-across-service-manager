package com.mcmp.webserver.monitoring.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import com.mcmp.webserver.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class NetResponseMonitoringRepository {

	@Autowired
	private InfluxDBClient influxDBClient;
	
	/**
	 * 1개 인스턴스의 Network Response Time 추이 (1H)
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getNetResponseTimeTransByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기 (1시간 전)
		Long now = TimeUtil.getCurrentTimeByMinusHours(1);
		
		// 쿼리 생성
		Flux flux = Flux.from("test").range(now)
				.filter(Restrictions.measurement().equal("net_response"))
				.filter(Restrictions.field().equal("response_time"))
				.filter(Restrictions.column("host").equal(hostName))
				.filter(Restrictions.column("protocol").equal("tcp"))
				.aggregateWindow()
				.withEvery("10s") // 10초마다
				.withAggregateFunction("max")
				.withCreateEmpty(false);

		// 쿼리 API 객체 생성
		QueryApi queryApi = influxDBClient.getQueryApi();

		// 쿼리 확인
		log.debug(flux.toString());

		List<FluxTable> tables = queryApi.query(flux.toString());
		List<FluxRecord> records = tables.stream().flatMap(table -> table.getRecords().stream())
				.collect(Collectors.toList());

		return records;
	}
}
