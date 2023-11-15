package com.mcmp.webserver.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtil {

	/**
	 * 현재시간 - minusHours (H)
	 * 
	 * @param minusHours - Integer 시간
	 * @return 현재시간 - minusHours 계산된 Long타입 시간
	 */
	public static Long getCurrentTimeByMinusHours(Integer minusHours) {
		ZoneId zoneId = ZoneId.of("Asia/Seoul");
		Instant instant = Instant.now().minus(minusHours, ChronoUnit.HOURS);
		return ZonedDateTime.ofInstant(instant, zoneId).toEpochSecond();
	}

	/**
	 * 현재시간 - minusMinutes (M)
	 * 
	 * @param minusHours - Integer 분
	 * @return 현재시간 - minusMinutes 계산된 Long타입 시간
	 */
	public static Long getCurrentTimeByMinusMinutes(Integer minusMinutes) {
		ZoneId zoneId = ZoneId.of("Asia/Seoul");
		Instant instant = Instant.now().minus(minusMinutes, ChronoUnit.MINUTES);
		return ZonedDateTime.ofInstant(instant, zoneId).toEpochSecond();
	}
	
	/**
	 * 현재시간 - minusSeconds (S)
	 * 
	 * @param minusHours - Integer 분
	 * @return 현재시간 - minusMinutes 계산된 Long타입 시간
	 */
	public static Long getCurrentTimeByMinusSeconds(Integer minusSeconds) {
		ZoneId zoneId = ZoneId.of("Asia/Seoul");
		Instant instant = Instant.now().minus(minusSeconds, ChronoUnit.SECONDS);
		return ZonedDateTime.ofInstant(instant, zoneId).toEpochSecond();
	}
}
