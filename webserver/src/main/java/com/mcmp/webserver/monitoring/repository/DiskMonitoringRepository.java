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
public class DiskMonitoringRepository {

	@Autowired
	private InfluxDBClient influxDBClient;
	
	/**
	 * N개 인스턴스의 디스크 사용량 모니터링 조회
	 * 
	 * @return
	 */
	public List<FluxRecord> getDiskUsed() {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusHours(1);

		// 쿼리 생성
		String[] groupBy = { "_time", "host" };
		Flux flux = Flux.from("test").range(now).filter(Restrictions.measurement().equal("disk"))
				.filter(Restrictions.field().equal("used_percent")).groupBy(groupBy).sum("_value").groupBy("host")
				.aggregateWindow().withEvery("1m").withAggregateFunction("max").withCreateEmpty(true);

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
	 * 1개 인스턴스의 Disk 사용량 평균 - 1분 기준
	 * 
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getDiskUsedByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusMinutes(1);

		// 쿼리 생성
		String query = """
				from(bucket: "test")
				  |> range(start: %s)
				  |> filter(fn: (r) => r["_measurement"] == "disk")
				  |> filter(fn: (r) => r["_field"] == "used_percent")
				  |> filter(fn: (r) => r["host"] == "%s")
				  |> group(columns: ["host", "_measurement"], mode:"by")
				  |> timeWeightedAvg(unit: 1s)
					""";
		query = String.format(query, now, hostName);

		// 쿼리 API 객체 생성
		QueryApi queryApi = influxDBClient.getQueryApi();

		// 쿼리 확인
		log.debug(query);

		List<FluxTable> tables = queryApi.query(query);
		List<FluxRecord> records = tables.stream().flatMap(table -> table.getRecords().stream())
				.collect(Collectors.toList());

		return records;
	}
}
