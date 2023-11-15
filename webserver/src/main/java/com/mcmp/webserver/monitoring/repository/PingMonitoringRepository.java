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
public class PingMonitoringRepository {

	@Autowired
	private InfluxDBClient influxDBClient;

	/**
	 * 연계서비스의 ping 모니터링 정보 조회
	 * 
	 * @param hostArray
	 * @return
	 */
	public List<FluxRecord> getPingAvgTimeByHostNames(List<String> hosts) {
		// DB 연결 상태 확인
		log.debug("influxDBClient=====>" + influxDBClient.ready());

		// 현재 시간 구하기
		Long now = TimeUtil.getCurrentTimeByMinusMinutes(1);

		// 쿼리 생성 (1분동안
		String[] groupBy = { "host", "_measurement","url" };
		

		
//		from(bucket: "test")
//		  |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
//			|> filter(fn: (r) => r["_measurement"] == "ping")
//			|> filter(fn: (r) => r["_field"] == "average_response_ms")
//			|> filter(fn: (r) => (r["host"] == "instance-influxdb" or r["host"] == "instance-telegraf-test"))
//		  |> group(columns: ["host", "_measurement", "url"], mode:"by")
//		  |> mean(column: "_value")  
		Flux flux = Flux.from("test").range(now)
	            .filter(Restrictions.measurement().equal("ping"))
	            .filter(Restrictions.field().equal("average_response_ms"))
	            .filter(Restrictions.or(hosts.stream().map(host -> Restrictions.column("host").equal(host)).toArray(Restrictions[]::new)))
				.groupBy(groupBy).mean();

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