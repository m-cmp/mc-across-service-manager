package com.mcmp.controller.util;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
@Service
public class CommonUtil {

    /**
     * UUID 생성
     *
     * @return
     */
    public static String makeUUID() {
        UUID uid = UUID.randomUUID();
        String convertUUID = uid.toString().replace("-", "");
        return convertUUID;
    }

    /**
     * request 날짜 파라미터 yyyyMMddHHmmss 형식 validation
     *
     * @return
     */
    public static String dateValidationChk(String reqDate) throws Exception {
        try {
            SimpleDateFormat sdf01 = new SimpleDateFormat("yyyyMMddHHmmss");
            Date reqDtmDate = null;
            String date = "";

            if (StringUtils.isEmpty(reqDate)) {
                reqDtmDate = new Date();
            } else {
                if (reqDate.length() != 14) {
                    throw new Exception();
                }
                reqDtmDate = sdf01.parse(reqDate);
            }

            date = sdf01.format(reqDtmDate);
            return date;
        } catch (Exception e) {
            throw new Exception("ERR:-6,date 파라미터 형식을 확인하신후 다시 시도해주십시오. (날짜 형식 : yyyyMMddHHmmss)");
        }
    }

    /**
     * request 날짜 파라미터 yyyy-MM-dd HH:mm:ss 형식 validation
     *
     * @return
     */
    public static String dateValidationChk2(String reqDate) throws Exception {
        try {
            SimpleDateFormat sdf01 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date reqDtmDate = null;
            String date = "";

            if (StringUtils.isEmpty(reqDate)) {
                reqDtmDate = new Date();
            } else {
                reqDtmDate = sdf01.parse(reqDate);
            }

            date = sdf01.format(reqDtmDate);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("ERR:-6,date 파라미터 형식을 확인하신후 다시 시도해주십시오. (날짜 형식 : yyyy-MM-dd HH:mm:ss)");
        }
    }

    //현재 년+월 (yymm)
    public static String getDate(String reqDate) throws Exception {
        String syear = "";
        String smonth = "";

        if (StringUtils.isEmpty(reqDate)) {
            Calendar cal = Calendar.getInstance();
            syear = String.valueOf(cal.get(1));
            smonth = String.format("%02d", new Object[] { Integer.valueOf(cal.get(2) + 1) });
        } else {
            try {
                SimpleDateFormat sdf01 = new SimpleDateFormat("yyyyMMddHHmmss");
                Date reqDtmDate = sdf01.parse(reqDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(reqDtmDate);
                syear = String.valueOf(cal.get(1));
                smonth = String.format("%02d", new Object[] { Integer.valueOf(cal.get(2) + 1) });
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("ERR:-6, reqDate을 확인하신후 다시 시도해주십시오.");
            }
        }
        return syear+smonth;
    }

    //현재 날짜 yyyymmddHHmmss
    public static String getCurrentDate() {
        SimpleDateFormat sdf01 = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = "";

        Date reqDtmDate = new Date();

        date = sdf01.format(reqDtmDate);
        return date;
    }
}
