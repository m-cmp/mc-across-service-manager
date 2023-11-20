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
public class CPUMonitoringRepository {

	@Autowired
	private InfluxDBClient influxDBClient;

	public List<FluxRecord> getTotalCPU() {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusHours(1);

		String[] groupBy = { "_time", "host" };
		// 쿼리 생성
		Restrictions filterOr = Restrictions.or(Restrictions.field().equal("usage_user"),
				Restrictions.field().equal("usage_system"));
		Flux flux = Flux.from("test").range(now).filter(Restrictions.measurement().equal("cpu")).filter(filterOr)
				.filter(Restrictions.column("cpu").equal("cpu-total")).groupBy(groupBy).sum("_value").groupBy("host")
				.aggregateWindow().withEvery("1m").withAggregateFunction("max").withCreateEmpty(true);

		// 쿼리 API 객체 생성
		QueryApi queryApi = influxDBClient.getQueryApi();

		// 쿼리 확인
		log.debug(flux.toString());

		// 쿼리 결과 조회
		List<FluxTable> tables = queryApi.query(flux.toString());
		List<FluxRecord> records = tables.stream().flatMap(table -> table.getRecords().stream())
				.collect(Collectors.toList());

		return records;
	}
	
	/**
	 * 1개 인스턴스의 CPU Usage 상세 
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getCPUUsageByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusHours(1);

		Restrictions filterOr = Restrictions.or(Restrictions.field().equal("usage_iowait"),
				Restrictions.field().equal("usage_idle"), Restrictions.field().equal("usage_guest"),
				Restrictions.field().equal("usage_steal"), Restrictions.field().equal("usage_user"),
				Restrictions.field().equal("usage_system"));

		Flux flux = Flux.from("test").range(now).filter(Restrictions.measurement().equal("cpu"))
				.filter(Restrictions.column("host").equal(hostName))
				.filter(Restrictions.column("cpu").equal("cpu-total")).filter(filterOr).aggregateWindow()
				.withEvery("1m").withAggregateFunction("max").withCreateEmpty(true);

		// 쿼리 API 객체 생성
		QueryApi queryApi = influxDBClient.getQueryApi();

		// 쿼리 확인
		log.debug(flux.toString());// 쿼리 결과 조회
		List<FluxTable> tables = queryApi.query(flux.toString());
		List<FluxRecord> records = tables.stream().flatMap(table -> table.getRecords().stream())
				.collect(Collectors.toList());

		return records;
	}
	
	/**
	 * 1개 인스턴스의 CPU Used 평균 (1분 기준)
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getCPUUsedByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusMinutes(1);

		String query = """
				from(bucket: "test")
				  |> range(start: %s)
				  |> filter(fn: (r) => r["_measurement"] == "cpu")
				  |> filter(fn: (r) => r["_field"] == "usage_system" or r["_field"] == "usage_user")
				  |> filter(fn: (r) => r["cpu"] == "cpu-total")
				  |> filter(fn: (r) => r["host"] == "%s")
				  |> group(columns: ["host", "_measurement"], mode:"by")  
				  |> timeWeightedAvg(unit: 1s)
				""";
		query = String.format(query, now, hostName);

		// 쿼리 API 객체 생성
		QueryApi queryApi = influxDBClient.getQueryApi();

		// 쿼리 확인
		log.debug(query);
		
		// 쿼리 결과 조회
		List<FluxTable> tables = queryApi.query(query);
		List<FluxRecord> records = tables.stream().flatMap(table -> table.getRecords().stream())
				.collect(Collectors.toList());

		return records;
	}

}
