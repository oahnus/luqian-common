package com.github.oahnus.luqiancommon.util;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by oahnus on 2019/4/26
 * 10:25.
 */
public class DateUtils {
    private static String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

    public static final String PATTERN_YMD = "yyyyMMdd";
    public static final String PATTERN_YMD2 = "yyyy-MM-dd";
    public static final String PATTERN_YMD3 = "yyyy年MM月dd日";
    public static final String PATTERN_YMD_HMS = "yyyyMMdd HH:mm:ss";
    public static final String PATTERN_YMD_HMS2 = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_HMS = "HH:mm:ss";
    public static final String PATTERN_YM = "yyyyMM";
    public static final String PATTERN_YM2 = "yyyy-MM";
    public static final String PATTERN_YM3 = "yyMM";

    private static final SimpleDateFormat FORMAT_YMD = new SimpleDateFormat(PATTERN_YMD);
    private static final SimpleDateFormat FORMAT_YMD2 = new SimpleDateFormat(PATTERN_YMD2);
    private static final SimpleDateFormat FORMAT_YMD3 = new SimpleDateFormat(PATTERN_YMD3);
    private static final SimpleDateFormat FORMAT_YMD_HMS = new SimpleDateFormat(PATTERN_YMD_HMS);
    private static final SimpleDateFormat FORMAT_YMD_HMS2 = new SimpleDateFormat(PATTERN_YMD_HMS2);
    private static final SimpleDateFormat FORMAT_HMS = new SimpleDateFormat(PATTERN_HMS);
    private static final SimpleDateFormat FORMAT_YM = new SimpleDateFormat(PATTERN_YM);
    private static final SimpleDateFormat FORMAT_YM2 = new SimpleDateFormat(PATTERN_YM2);
    private static final SimpleDateFormat FORMAT_YM3 = new SimpleDateFormat(PATTERN_YM3);

    private static final Map<String, SimpleDateFormat> formatMap;

    private static final Set<DayOfWeek> weekends = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static final ZoneId defaultZoneId = ZoneId.systemDefault();

    private final static Map<String, String> weekAndWeek;

    static {
        weekAndWeek = new HashMap<>();
        weekAndWeek.put("SUNDAY", "星期日");
        weekAndWeek.put("MONDAY", "星期一");
        weekAndWeek.put("TUESDAY", "星期二");
        weekAndWeek.put("WEDNESDAY", "星期三");
        weekAndWeek.put("THURSDAY", "星期四");
        weekAndWeek.put("FRIDAY", "星期五");
        weekAndWeek.put("SATURDAY", "星期六");

        formatMap = new HashMap<>();
        formatMap.put(PATTERN_YMD, FORMAT_YMD);
        formatMap.put(PATTERN_YMD2, FORMAT_YMD2);
        formatMap.put(PATTERN_YMD3, FORMAT_YMD3);
        formatMap.put(PATTERN_YMD_HMS, FORMAT_YMD_HMS);
        formatMap.put(PATTERN_YMD_HMS2, FORMAT_YMD_HMS2);
        formatMap.put(PATTERN_HMS, FORMAT_HMS);
        formatMap.put(PATTERN_YM, FORMAT_YM);
        formatMap.put(PATTERN_YM2, FORMAT_YM2);
        formatMap.put(PATTERN_YM3, FORMAT_YM3);
    }

    private static SimpleDateFormat getDateFormat(String pattern) {
        SimpleDateFormat dateFormat = formatMap.get(pattern);
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(pattern);
            formatMap.put(pattern, dateFormat);
        }

        return dateFormat;
    }

    public static Date nextDayStart() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static String date2String(Date date, SimpleDateFormat sdf) {
        try {
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static Date floatTimestamp2Date(float fStamp) {
        LocalDate localDate = LocalDate
                .of(1899, 12, 30)
                .plus((long) fStamp, ChronoUnit.DAYS);
        return localDate2date(localDate);
    }


    public static String date2String(Date date, String pattern) {
        SimpleDateFormat dateFormat = getDateFormat(pattern);
        synchronized (dateFormat) {
            return date2String(date, dateFormat);
        }
    }

    public static Date string2Date(String str, String pattern) {
        SimpleDateFormat dateFormat = getDateFormat(pattern);
        synchronized (dateFormat) {
            return string2Date(str, dateFormat);
        }
    }

    public static Date string2Date(String str, SimpleDateFormat sdf) {
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (Exception e) {
            return null;
        }
        return date;
    }

    public static Date localDate2date(LocalDate localDate) {
        return java.sql.Timestamp.valueOf(localDate.atStartOfDay());
    }

    public static Date getDate() {
        return new Date();
    }

    public static String getTime() {
        return getDateFormat(PATTERN_HMS).format(new Date());
    }

    public static String getDateTime() {
        Time sTime = new Time((new Date()).getTime());
        return getDateFormat(PATTERN_HMS).format(sTime);
    }

    public static String getDateTime(Date date) {
        Time sTime = new Time(date.getTime());
        return getDateFormat(PATTERN_HMS).format(sTime);
    }

    public static String getTime4(Time time) {
        return getDateFormat(PATTERN_HMS).format(time);
    }

    public static Integer curYear() {
        return YearMonth.now().getYear();
    }

    public static List<LocalDate> curMonthRange() {
        YearMonth now = YearMonth.now();
        return monthRange(now);
    }

    public static List<LocalDate> monthRange(LocalDate yearMonth) {
        YearMonth month = YearMonth.of(yearMonth.getYear(), yearMonth.getMonth());
        return monthRange(month);
    }

    /**
     * ex:
     * 2017-9-1
     *
     * @return
     */
    private static List<LocalDate> monthRange(YearMonth yearMonth) {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
        return Arrays.asList(firstDay, lastDay);
    }

    public static Integer curMonth() {
        return YearMonth.now().getMonthValue();
    }


    public static Boolean isWeekend(Date date) {
        return isWeekend(date2localDate(date));
    }

    public static Boolean isWorkDay(Date date) {
        return isWorkDay(date2localDate(date));
    }

    public static Boolean isWeekend(LocalDate date) {
        return weekends.contains(date.getDayOfWeek());
    }

    public static Boolean isWorkDay(LocalDate date) {
        return !isWeekend(date);
    }


    public static LocalDate date2localDate(Date date) {
        return date.toInstant().atZone(defaultZoneId).toLocalDate();
    }

    /**
     * 获取星期
     *
     * @param date
     * @return
     */
    public static String getWeek(Date date) {
        int week_index = date2localDate(date).getDayOfWeek().getValue();
        return weeks[week_index];
    }


    /**
     * java.time.LocalDate --> java.util.Date
     *
     * @param localDate
     * @return
     */
    public static Date LocalDateTimeToUpdate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 获取日历
     *
     * @param date
     * @return
     */
    public static Map<Date, String> getCalendar(Date date) {
        //date -> localDate
        LocalDate localDate = date2localDate(date);

        //获取当月
        int month = localDate.getMonthValue();
        //获取当前是当月的第几天
        int today = localDate.getDayOfMonth();

        //将日期设置为当月的第一天
        localDate = localDate.minusDays(today - 1);
        //本月的日期信息Map<"2017-09-01", "SATURDAY">
        Map<Date, String> map = new HashMap<>();
        while (localDate.getMonthValue() == month) {
            Date date2 = LocalDateTimeToUpdate(localDate);
            map.put(date2, localDate.getDayOfWeek().toString());
            localDate = localDate.plusDays(1);
        }
        return map;
    }

    /**
     * 返回中文星期
     *
     * @param week
     * @return
     */
    public static String getStringWeek(String week) {
        return weekAndWeek.get(week);
    }

    /**
     * 获取两个时间相差分钟
     *
     * @param date1
     * @param date2
     * @return
     */
    public static Integer minutesBetween(Date date1, Date date2) {
//        long between = ChronoUnit.MINUTES.between(date2localDate(date1), date2localDate(date2));
//        Integer.parseInt(String.valueOf(between));
        Time endTime = new Time(date1.getTime());
        Time nowTime = new Time(date2.getTime());
        // 获得两个时间的毫秒时间差异
        int diff = 0;
        diff = new Long(nowTime.getTime() - endTime.getTime()).intValue();
        // 计算差多少分钟
//        return Math.abs(diff % (1000 * 24 * 60 * 60) % (1000 * 60 * 60) / (1000 * 60));
        return Math.abs(diff / (1000 * 60));
    }

    public static String now() {
        return date2String(new Date(), PATTERN_YMD_HMS2);
    }

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(date2String(date, PATTERN_YM));
        System.out.println(date2String(date, PATTERN_YMD));
        System.out.println(date2String(date, PATTERN_YMD_HMS));
        System.out.println(date2String(date, PATTERN_HMS));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = "2016-12-30 15:35:34";
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (int i1 = 0; i1 < 5; i1++) {
                    // Error
//                        System.out.println(Thread.currentThread().getName() + "\t" + dateFormat.parse(dateTime));
                    System.out.println(Thread.currentThread().getName() + "\t" + string2Date(dateTime, PATTERN_YMD_HMS2));
                }
            }).start();
        }
    }

}
