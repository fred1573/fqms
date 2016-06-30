/**
 * <h3>Class description</h3>
 * <h4>日期处理类</h4>
 * <h4>Special Notes</h4>
 *
 * @ver 0.1
 * @author mowei
 */
package com.project.utils.time;

import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
    /**
     * 一天中一共的毫秒数
     */
    public static long millionSecondsOfDay = 86400000;
    /**
     * 一小时中一共得毫秒数
     */
    public static long millionSecondsOfHour = 3600000;

    private static final String FORMAT_DATE_STR = "yyyy-MM-dd";

    /**
     * 时间格式化格式：显示秒
     */
    public static final String FORMAT_DATE_STR_SECOND = "yyyy-MM-dd HH:mm:ss";
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 得到两个日期之间相差的年数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDifferYear(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        return c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
    }

    /**
     * 得到两个日期之间相差的年数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDifferYear(String date1, String date2) {
        Date dateTime1_tmp = DateUtil.parse(date1, "yyyy-MM-dd");
        Date dateTime2_tmp = DateUtil.parse(date2, "yyyy-MM-dd");
        return getDifferYear(dateTime1_tmp, dateTime2_tmp);
    }

    /**
     * 得到两个日期之间相差的月数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDifferMonth(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        int year = getDifferYear(date1, date2);
        int months = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH) + year * 12;
        if (c2.get(Calendar.DATE) < c1.get(Calendar.DATE)) {
            months = months - 1;
        }
        return months;
    }

    /**
     * 得到两个日期之间相差的月数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDifferMonth(String date1, String date2) {
        Date dateTime1_tmp = DateUtil.parse(date1, "yyyy-MM-dd");
        Date dateTime2_tmp = DateUtil.parse(date2, "yyyy-MM-dd");
        return getDifferMonth(dateTime1_tmp, dateTime2_tmp);
    }

    /**
     * 得到两个日期之间相差的天数,两头不算,取出数据后，可以根据需要再加
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDifferDay(Date date1, Date date2) {
        Long d1 = date1.getTime();
        Long d2 = date2.getTime();
        return (int) ((d2 - d1) / millionSecondsOfDay);
    }

    /**
     * 得到两个日期之间相差的天数,两头不算,取出数据后，可以根据需要再加
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDifferDay(String date1, String date2) {
        Date dateTime1_tmp = DateUtil.parse(date1, "yyyy-MM-dd");
        Date dateTime2_tmp = DateUtil.parse(date2, "yyyy-MM-dd");
        return getDifferDay(dateTime1_tmp, dateTime2_tmp);
    }

    /**
     * 计算2个时间之间的相差的小时和分钟数，返回XX小时XX分
     * 注意date1格式为yyyy-MM-dd
     * 注意date2格式为yyyy-MM-dd
     * 注意time1格式为HH:mm
     * 注意time2格式为HH:mm
     * date1<date2
     *
     * @param date1
     * @param time1
     * @param date2
     * @param time2
     * @return resultHM[hours, mins]
     */
    public static int[] getDifferHourAndMinute(String date1, String time1, String date2, String time2) {
        Date dateTime1_tmp = DateUtil.parse(date1 + " " + time1, "yyyy-MM-dd HH:mm");
        Date dateTime2_tmp = DateUtil.parse(date2 + " " + time2, "yyyy-MM-dd HH:mm");
        Long d2 = dateTime2_tmp.getTime();
        Long d1 = dateTime1_tmp.getTime();
        int hours = (int) ((d2 - d1) / millionSecondsOfHour);
        int mins = (int) ((d2 - d1) % millionSecondsOfHour);
        mins = mins / 60000;
        int[] resultHM = new int[2];
        resultHM[0] = hours;
        resultHM[1] = mins;
        return resultHM;
    }

    /**
     * 计算2个时间之间的相差的小时数(Date,Date)
     * date1<date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDifferHour(Date date1, Date date2) {
        Long d1 = date1.getTime();
        Long d2 = date2.getTime();
        int hours = (int) ((d2 - d1) / millionSecondsOfHour);
        return hours;
    }

    /**
     * 计算2个时间之间的相差的小时(String,String)
     * date1<date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDifferHour(String date1, String date2) {
        Date dateTime1_tmp = DateUtil.parse(date1, "yyyy-MM-dd");
        Date dateTime2_tmp = DateUtil.parse(date2, "yyyy-MM-dd");
        Long d2 = dateTime2_tmp.getTime();
        Long d1 = dateTime1_tmp.getTime();
        int hours = (int) ((d2 - d1) / millionSecondsOfHour);
        return hours;
    }

    /**
     * 计算日期加年
     *
     * @param date
     * @param years
     * @return
     */
    public static Date addYear(Date date, int years) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.YEAR, years);
        return c.getTime();
    }

    /**
     * 计算日期加月数
     *
     * @param date
     * @param months
     * @return
     */
    public static Date addMonth(Date date, int months) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }

    /**
     * 计算日期加天数
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDay(Date date, int days) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    /**
     * 计算日期加分钟数
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date addMinutes(Date date, int minutes) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    /**
     * 格式化日期为String型(yyyy-MM-dd)
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, FORMAT_DATE_STR);
    }

    /**
     * 根据指定日期格式格式化日期为String型
     *
     * @param date
     * @param formater
     * @return
     */
    public static String format(Date date, String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        return sdf.format(date);
    }

    /**
     * 格式化日期为Date型(yyyy-MM-dd)
     *
     * @param date
     * @return
     */
    public static Date parse(String date) {
        return parse(date, FORMAT_DATE_STR);
    }

    /**
     * 根据指定日期格式格式化日期为Date型
     *
     * @param date
     * @param formater
     * @return
     */
    public static Date parse(String date, String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        Date result = null;
        try {
            result = sdf.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据日期取出是星期几,数字
     *
     * @param date
     * @return int 返回1-7
     */
    public static int getWeekOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return (calendar.get(Calendar.DAY_OF_WEEK) - 1) == 0 ? 7 : calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 根据日期取出是星期几,中文
     *
     * @param date
     * @return int 返回1-7
     */
    public static String getWeekTextOfDate(Date date) {
        String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int t = getWeekOfDate(date);
        if (t == 7) t = 0;
        return dayNames[t];
    }

    /**
     * 得到当前的日期，格式为：yyyy-MM-dd
     *
     * @return 为一个字符串
     */
    public static String getCurrenDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        return sdf.format(d).toString();
    }

    /**
     * 得到当前的时间，精确到毫秒，格式为：yyyy-MM-dd hh:mm:ss
     *
     * @return 为一个字符串
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date();
        return sdf.format(d).toString();
    }

    /**
     * 将java时间转为sql时间
     *
     * @param date
     * @return
     */
    public static java.sql.Date convertUtilDateToSQLDate(java.util.Date date) {
        if (date == null)
            return null;
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        java.sql.Date jd = new java.sql.Date(cl.getTimeInMillis());
        return jd;
    }

    /**
     * 将sql时间转为java时间
     *
     * @param date
     * @return
     */
    public static java.util.Date convertSQLDateToUtilDate(java.sql.Date date) {
        if (date == null)
            return null;
        Calendar cl = Calendar.getInstance();

        cl.setTime(date);
        java.util.Date jd = new java.util.Date(cl.getTimeInMillis());
        return jd;
    }

    /**
     * 是否为闰年
     *
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year) {
        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            if ((year % 100) == 0)
                return false;
            else return true;
        } else return false;
    }

    /**
     * 是否为当天
     *
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        if (today.get(Calendar.YEAR) == day.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == day.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH))
            return true;
        else
            return false;
    }

    /**
     * 取Java虚拟机系统时间, 返回当前时间戳
     *
     * @return Timestamp类型的时间
     */
    public static Timestamp getSysTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 取Java虚拟机系统时间, 返回当前Date
     *
     * @return Date类型的时间
     */
    public static Date getSysDate() {
        Calendar cl = Calendar.getInstance();
        return cl.getTime();
    }

    /**
     * 当前日期是否在指定区间日期范围内-- 闭区间
     *
     * @param date1 Date类型
     * @param date2 Date类型
     * @return
     */
    public static boolean isBetweenDateByClosedInterval(Date date1, Date date2) {
        String nowDate = DateUtil.format(new Date(), "yyyy-MM-dd");
        String sDate1 = DateUtil.format(date1, "yyyy-MM-dd");
        String sDate2 = DateUtil.format(date2, "yyyy-MM-dd");
        if (java.sql.Date.valueOf(sDate1).before(java.sql.Date.valueOf(sDate2)) || java.sql.Date.valueOf(sDate1).equals(java.sql.Date.valueOf(sDate2))) {
            if ((java.sql.Date.valueOf(nowDate).after(java.sql.Date.valueOf(sDate1)) || java.sql.Date.valueOf(nowDate).equals(java.sql.Date.valueOf(sDate1)))
                    && (java.sql.Date.valueOf(nowDate).before(java.sql.Date.valueOf(sDate2)) || java.sql.Date.valueOf(nowDate).equals(java.sql.Date.valueOf(sDate2)))) {
                return true;
            } else {
                return false;
            }
        } else {
            if ((java.sql.Date.valueOf(nowDate).after(java.sql.Date.valueOf(sDate2)) || java.sql.Date.valueOf(nowDate).equals(java.sql.Date.valueOf(sDate2)))
                    && (java.sql.Date.valueOf(nowDate).before(java.sql.Date.valueOf(sDate1))) || java.sql.Date.valueOf(nowDate).equals(java.sql.Date.valueOf(sDate1))) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 当前日期是否在指定区间日期范围内-- 开区间
     *
     * @param date1 Date类型
     * @param date2 Date类型
     * @return
     */
    public static boolean isBetweenDateByOpenInterval(Date date1, Date date2) {
        String nowDate = DateUtil.format(new Date(), "yyyy-MM-dd");
        String sDate1 = DateUtil.format(date1, "yyyy-MM-dd");
        String sDate2 = DateUtil.format(date2, "yyyy-MM-dd");
        if (java.sql.Date.valueOf(sDate1).before(java.sql.Date.valueOf(sDate2))) {
            if (java.sql.Date.valueOf(nowDate).after(java.sql.Date.valueOf(sDate1)) && java.sql.Date.valueOf(nowDate).before(java.sql.Date.valueOf(sDate2))) {
                return true;
            } else {
                return false;
            }
        } else if (java.sql.Date.valueOf(sDate1).after(java.sql.Date.valueOf(sDate2))) {
            if (java.sql.Date.valueOf(nowDate).after(java.sql.Date.valueOf(sDate2)) && java.sql.Date.valueOf(nowDate).before(java.sql.Date.valueOf(sDate1))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 当前日期是否在指定区间日期范围内-- 闭区间
     *
     * @param sDate1 String类型（格式为：yyyy-MM-dd）
     * @param date2  String类型（格式为：yyyy-MM-dd）
     * @return
     */
    public static boolean isBetweenDateByClosedInterval(String sDate1, String date2) {
        String nowDate = DateUtil.format(new Date(), "yyyy-MM-dd");
        if (java.sql.Date.valueOf(sDate1).before(java.sql.Date.valueOf(date2)) || java.sql.Date.valueOf(sDate1).equals(java.sql.Date.valueOf(date2))) {
            if ((java.sql.Date.valueOf(nowDate).after(java.sql.Date.valueOf(sDate1)) || java.sql.Date.valueOf(nowDate).equals(java.sql.Date.valueOf(sDate1)))
                    && (java.sql.Date.valueOf(nowDate).before(java.sql.Date.valueOf(date2)) || java.sql.Date.valueOf(nowDate).equals(java.sql.Date.valueOf(date2)))) {
                return true;
            } else {
                return false;
            }
        } else {
            if ((java.sql.Date.valueOf(nowDate).after(java.sql.Date.valueOf(date2)) || java.sql.Date.valueOf(nowDate).equals(java.sql.Date.valueOf(date2)))
                    && (java.sql.Date.valueOf(nowDate).before(java.sql.Date.valueOf(sDate1))) || java.sql.Date.valueOf(nowDate).equals(java.sql.Date.valueOf(sDate1))) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 当前日期是否在指定区间日期范围内-- 开区间
     *
     * @param sDate1 String类型（格式为：yyyy-MM-dd）
     * @param date2  String类型（格式为：yyyy-MM-dd）
     * @return
     */
    public static boolean isBetweenDateByOpenInterval(String sDate1, String date2) {
        String nowDate = DateUtil.format(new Date(), "yyyy-MM-dd");
        if (java.sql.Date.valueOf(sDate1).before(java.sql.Date.valueOf(date2))) {
            if (java.sql.Date.valueOf(nowDate).after(java.sql.Date.valueOf(sDate1)) && java.sql.Date.valueOf(nowDate).before(java.sql.Date.valueOf(date2))) {
                return true;
            } else {
                return false;
            }
        } else if (java.sql.Date.valueOf(sDate1).after(java.sql.Date.valueOf(date2))) {
            if (java.sql.Date.valueOf(nowDate).after(java.sql.Date.valueOf(date2)) && java.sql.Date.valueOf(nowDate).before(java.sql.Date.valueOf(sDate1))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取两个日期间的日期，包括边界值
     *
     * @param beginDate 开始日期字符串
     * @param endDate   结束日期字符串
     * @return
     */
    public static List<String> getDays(String beginDate, String endDate) {
        Date begin = parse(beginDate);
        Date end = parse(endDate);
        List<String> list = new ArrayList<>();
        if (begin.after(end)) {
            Date temp = begin;
            begin = end;
            end = temp;
        }
        while (DateUtils.isSameDay(begin, end) || begin.before(end)) {
            list.add(format(begin));
            begin = addDay(begin, 1);
        }
        return list;
    }

    /**
     * 获取最小日期
     *
     * @param dateList
     * @return
     */
    public static Date getBeginDate(List<Date> dateList) {
        if (CollectionsUtil.isNotEmpty(dateList)) {
            Date beginDate = dateList.get(0);
            for (int i = 1; i < dateList.size(); i++) {
                if (dateList.get(i).before(beginDate)) {
                    beginDate = dateList.get(i);
                }
            }
            return beginDate;
        }
        return null;
    }


    /**
     * 获取最大日期
     *
     * @param dateList
     * @return
     */
    public static Date getEndDate(List<Date> dateList) {

        if (CollectionsUtil.isNotEmpty(dateList)) {
            Date endDate = dateList.get(0);
            for (int i = 1; i < dateList.size(); i++) {

                if (dateList.get(i).after(endDate)) {
                    endDate = dateList.get(i);
                }
            }

            return endDate;
        }
        return null;
    }
}

