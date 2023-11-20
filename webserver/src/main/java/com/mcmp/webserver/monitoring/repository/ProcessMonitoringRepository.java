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
public class ProcessMonitoringRepository {

	@Autowired
	private InfluxDBClient influxDBClient;

	/**
	 * 인스턴스 별 프로세스 수 추이 변화 (1시간 전~)
	 * 
	 * @return List<FluxRecord> records
	 */
	public List<FluxRecord> getProcessTransition() {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusHours(1);

		// 쿼리 생성
		String[] group = { "host" };
		Flux flux = Flux.from("test")
				.range(now)
				.filter(Restrictions.measurement().equal("processes"))
				.filter(Restrictions.field().equal("total"))
				.groupBy(group).aggregateWindow().withEvery("1m")
				.withAggregateFunction("max").withCreateEmpty(false);

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
	 * 평균 프로세스/스레드 수 By 분, host명
	 * @param field "total_threads" / "process"
	 * @param hostName
	 * @param minutes
	 * @return List<FluxRecord> records
	 */
	public List<FluxRecord> getFieldCntByHostAndMunutes(String field, String hostName, Integer minutes) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusMinutes(minutes);

		// 쿼리 생성
		String query = """
				from(bucket:"test")
				    |> range(start: %s)
				    |> filter(fn: (r) => r["_measurement"] == "processes")
				    |> filter(fn: (r) => r["_field"] == "%s")
				    |> filter(fn: (r) => r["host"] == "%s")
				    |> highestAverage(n:1, groupColumns: ["host"])
				""";
		query = String.format(query, now, field, hostName);

		log.debug(query);
		QueryApi queryApi = influxDBClient.getQueryApi();
		List<FluxTable> tables = queryApi.query(query);
		List<FluxRecord> records = tables.stream().flatMap(table -> table.getRecords().stream())
				.collect(Collectors.toList());

		return records;
	}

	public List<FluxRecord> getThreadTransition() {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusHours(1);

		// 쿼리 생성
		String[] group = { "host" };
		Flux flux = Flux.from("test").range(now).filter(Restrictions.measurement().equal("processes"))
				.filter(Restrictions.field().equal("total_threads")).groupBy(group).aggregateWindow().withEvery("1m")
				.withAggregateFunction("max").withCreateEmpty(false);

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
	 * 인스턴스의 프로세스 상태별 수 추이
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getProcessStatusCntTransition(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기 (10분 전)
		Long now = TimeUtil.getCurrentTimeByMinusMinutes(60);
		
		Restrictions filterOr = 
				Restrictions.or(
						Restrictions.field().equal("running"),
						Restrictions.field().equal("sleeping"),
						Restrictions.field().equal("stopped"),
						Restrictions.field().equal("paging"),
						Restrictions.field().equal("blocked"),
						Restrictions.field().equal("idle"),
						Restrictions.field().equal("unknown"),
						Restrictions.field().equal("idle"),
						Restrictions.field().equal("zombies")
				);
		
		// 쿼리 생성
		Flux flux = Flux.from("test").range(now)
				.filter(Restrictions.measurement().equal("processes"))
				.filter(filterOr)
				.filter(Restrictions.column("host").equal(hostName))
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
