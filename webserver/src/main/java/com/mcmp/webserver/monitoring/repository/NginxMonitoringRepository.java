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
public class NginxMonitoringRepository {

	@Autowired
	private InfluxDBClient influxDBClient;

	/**
	 * Nginx TPS 계산 (10초 단위) 모니터링 정보 조회
	 * 
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getNginxTpsByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 - 10초 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusSeconds(30);

		// 쿼리 생성
		Flux flux = Flux.from("test").range(now)
				.filter(Restrictions.measurement().equal("nginx"))
				.filter(Restrictions.field().equal("requests"))
				.filter(Restrictions.column("host").equal(hostName))
				.filter(Restrictions.column("port").equal("80"))
				.difference()
				.aggregateWindow().withEvery("10s") // 10초마다
				.withAggregateFunction("max").withCreateEmpty(true)
				.max();

		// 쿼리 API 객체 생성
		QueryApi queryApi = influxDBClient.getQueryApi();

		// 쿼리 확인
		log.debug(flux.toString());

		List<FluxTable> tables = queryApi.query(flux.toString());
		List<FluxRecord> records = tables.stream().flatMap(table -> table.getRecords().stream())
				.collect(Collectors.toList());

		return records;
	}

	/**
	 * Nginx TPS 계산 추이 모니터링 정보 조회
	 * 
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getNginxTpsTransitionByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 - 10초 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusMinutes(5);

		// 쿼리 생성
		Flux flux = Flux.from("test").range(now).filter(Restrictions.measurement().equal("nginx"))
				.filter(Restrictions.field().equal("requests"))
				.filter(Restrictions.column("host").equal(hostName))
				.filter(Restrictions.column("port").equal("80")).difference();

		// 쿼리 API 객체 생성
		QueryApi queryApi = influxDBClient.getQueryApi();

		// 쿼리 확인
		log.debug(flux.toString());

		List<FluxTable> tables = queryApi.query(flux.toString());
		List<FluxRecord> records = tables.stream().flatMap(table -> table.getRecords().stream())
				.collect(Collectors.toList());

		return records;
	}

	/**
	 * Nginx active 모니터링 정보 조회 (10s)
	 * 
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getNginxActiveByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 - 10초 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusSeconds(15);

		// 쿼리 생성
		Flux flux = Flux.from("test").range(now).filter(Restrictions.measurement().equal("nginx"))
				.filter(Restrictions.field().equal("active")).filter(Restrictions.column("host").equal(hostName))
				.filter(Restrictions.column("port").equal("80"))
				.aggregateWindow().withEvery("10s") // 10초마다
				.withAggregateFunction("max").withCreateEmpty(true)
				.max();

		// 쿼리 API 객체 생성
		QueryApi queryApi = influxDBClient.getQueryApi();

		// 쿼리 확인
		log.debug(flux.toString());

		List<FluxTable> tables = queryApi.query(flux.toString());
		List<FluxRecord> records = tables.stream().flatMap(table -> table.getRecords().stream())
				.collect(Collectors.toList());

		return records;
	}
	
	/**
	 * Nginx active 추이 조회 (1H)
	 * 
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getNginxActiveTransByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 - 1시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusMinutes(5);

		// 쿼리 생성
		Flux flux = Flux.from("test").range(now).filter(Restrictions.measurement().equal("nginx"))
				.filter(Restrictions.field().equal("active")).filter(Restrictions.column("host").equal(hostName))
				.filter(Restrictions.column("port").equal("80"))
				.aggregateWindow().withEvery("10s") // 30초마다
				.withAggregateFunction("max").withCreateEmpty(true);

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
