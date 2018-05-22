package com.danertu.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Viz on 2018/1/18.
 */

public class DateTimeUtils {
    public static String getDateToyyyyMMdd() {
        return parseDateToString("yyyy-MM-dd");
    }

    public static String getDateToyyyyMMddHHmm() {
        return parseDateToString("yyyy-MM-dd HH:mm");
    }

    public static String getDateToyyyyMMddHHmmss() {
        return parseDateToString("yyyy-MM-dd HH:mm:ss");
    }

    public static String parseDateToString(String pattern) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static boolean isToday(String day) {
        //当前时间
        Date now = new Date();
        SimpleDateFormat sdf;
        if (day.contains("-")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        } else if (day.contains("/")) {
            sdf = new SimpleDateFormat("yyyy/MM/dd");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        //获取今天的日期
        String nowDay = sdf.format(now);
        return nowDay.equals(day);
    }

    public static boolean isYesterday(String day, String pattern) {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            long create = formatter.parse(day).getTime();
            Calendar now = Calendar.getInstance();
            long ms = 1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND));//毫秒数
            long ms_now = now.getTimeInMillis();

            return ms_now - create < (ms + 24 * 3600 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String parseDate(String createTime) {
        try {
            String ret = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long create = sdf.parse(createTime).getTime();
            Calendar now = Calendar.getInstance();
            long ms = 1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND));//毫秒数
            long ms_now = now.getTimeInMillis();
            if (ms_now - create < ms) {
                ret = "今天";
            } else if (ms_now - create < (ms + 24 * 3600 * 1000)) {
                ret = "昨天";
            } else if (ms_now - create < (ms + 24 * 3600 * 1000 * 2)) {
                ret = "前天";
            } else {
                ret = "更早";
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String formatDateStr(String dateStr, String pattern) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            Date date = formatter.parse(dateStr);
            return formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getNowDate() {
        return parseDateToString("yyyy-MM-dd");
    }

    public static String getNowTime() {
        return parseDateToString("HH:mm:ss");
    }

    public static Date parseToDateyyyyMMdd(String str) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date parseToDateyyyyMMddHHmmss(String str) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String formatDateStr(String dateStr) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(dateStr);
        return formatter.format(date);
    }

    public static String getSpecifiedDayAfter(String specifiedDay) {

        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);
        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
    }

    /**
     * 比较两个日期,如果日期相等返回0；小于0，参数date1就是在date2之后,大于0，参数date1就是在date2之前
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int compareDate(String date1, String date2) {
        try {
            SimpleDateFormat sdf;
            if (date1.contains("-")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            } else if (date1.contains("/")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            } else {
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            }

            //将日期转成Date对象作比较
            Date fomatDate1 = sdf.parse(date1);
            Date fomatDate2 = sdf.parse(date2);
            return fomatDate2.compareTo(fomatDate1);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public static boolean compareDate2(String date1, String date2) {
        try {
            SimpleDateFormat sdf;
            if (date1.contains("-")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            } else if (date1.contains("/")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            } else {
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            }
            long time1 = sdf.parse(date1).getTime();
            long time2 = sdf.parse(date2).getTime();
            return time1 > time2;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }
}
