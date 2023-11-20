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
public class MemoryMonitoringRepository {

	@Autowired
	private InfluxDBClient influxDBClient;

	/**
	 * 인스턴스별 메모리 사용량 (%)
	 * 
	 * @return
	 */
	public List<FluxRecord> getMemoryUsed() {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusHours(1);

		String[] groupBy = { "_time", "host" };

		// 쿼리 생성
		Flux flux = Flux.from("test").range(now).filter(Restrictions.measurement().equal("mem"))
				.filter(Restrictions.field().equal("used_percent")).groupBy(groupBy).sum("_value").groupBy("host")
				.aggregateWindow().withEvery("1m").withAggregateFunction("mean").withCreateEmpty(true);

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
	 * 1개 인스턴스의 메모리 사용량 (%) - 1분
	 * 
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getMemoryUsedByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusMinutes(1);
		
		
		String query = """
						from(bucket: "test")
						  |> range(start: %s)
						  |> filter(fn: (r) => r["_measurement"] == "mem")
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

	/**
	 * 1개 인스턴스의 상세 메모리 사용 모니터링
	 * 
	 * @param hostName
	 * @return
	 */
	public List<FluxRecord> getMemoryResourceByHostName(String hostName) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusHours(1);

		// 쿼리 생성
		Restrictions filterOr = Restrictions.or(Restrictions.field().equal("active"),
				Restrictions.field().equal("available"), Restrictions.field().equal("buffered"),
				Restrictions.field().equal("cached"), Restrictions.field().equal("dirty"),
				Restrictions.field().equal("inactive"), Restrictions.field().equal("mapped"),
				Restrictions.field().equal("shared"));
		
		Flux flux = Flux.from("test").range(now)
				.filter(Restrictions.measurement().equal("mem")) // 측정 대상
				.filter(Restrictions.column("host").equal(hostName)) // 호스트명
				.filter(filterOr) // 필드
				.aggregateWindow().withEvery("1m")
				.withAggregateFunction("max")
				.withCreateEmpty(true);

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
