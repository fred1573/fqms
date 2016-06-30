
package com.project.utils.time;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public final class VDateTimeUtil implements Serializable {

	// 日历
	private final Calendar greCal;
	
	 // 毫秒数
	private final int milsecond;

	
	 // 构造函数<br>
	public VDateTimeUtil() {
		greCal = new GregorianCalendar();
		SimpleDateFormat sdf1 = new SimpleDateFormat("SSS");
		milsecond = Integer.parseInt(sdf1.format(new Date()));
	}

	/**
	 * 构造函数<br>
	 * @param zone 时区
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public VDateTimeUtil(TimeZone zone) {
		greCal = new GregorianCalendar(zone);
		SimpleDateFormat sdf1 = new SimpleDateFormat("SSS");
		milsecond = Integer.parseInt(sdf1.format(new Date()));
	}

	/**
	 * 构造函数<br>
	 * @param aLocale 地区
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public VDateTimeUtil(Locale aLocale) {
		greCal = new GregorianCalendar(aLocale);
		SimpleDateFormat sdf1 = new SimpleDateFormat("SSS");
		milsecond = Integer.parseInt(sdf1.format(new Date()));
	}

	/**
	 * 构造函数<br>
	 * @param zone     时区
	 * @param aLocale  地区
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public VDateTimeUtil(TimeZone zone, Locale aLocale) {
		greCal = new GregorianCalendar(zone, aLocale);
		SimpleDateFormat sdf1 = new SimpleDateFormat("SSS");
		milsecond = Integer.parseInt(sdf1.format(new Date()));
	}

	/**
	 * 构造函数<br>
	 * @param year  年
	 * @param month 月
	 * @param date  日
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public VDateTimeUtil(int year, int month, int date) {
		this(year, month, date, 0, 0, 0, 0);
	}

	/**
	 * 构造函数<br>
	 * @param year  年
	 * @param month 月
	 * @param date  日
	 * @param hour  小时
	 * @param minute 分
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public VDateTimeUtil(int year, int month, int date, int hour, int minute) {
		this(year, month, date, hour, minute, 0, 0);
	}
	/**
	 * 构造函数<br>
	 * @param year  年
	 * @param month 月
	 * @param date  日
	 * @param hour  小时
	 * @param minute 分
	 * @param second 秒
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public VDateTimeUtil(int year, int month, int date, int hour, int minute ,int second) {
		this(year, month, date, hour, minute, second, 0);
	}
	/**
	 * 构造函数<br>
	 * @param year   年
	 * @param month  月
	 * @param date   日
	 * @param hour   小时
	 * @param minute 分
	 * @param second 秒
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public VDateTimeUtil(
		int year,
		int month,
		int date,
		int hour,
		int minute,
		int second,
		int milsubsecond) {
		boolean flag = true;
		if ((year > 2050) || (year < 1850)) {
			flag = false;
		} else if ((month > 12) || (month < 1)) {
			flag = false;
		} else if (month == 2) {
			if (VDateTimeUtil.isLeapYear(year)) {
				if ((date < 1) || (date > 29))
					flag = false;
			} else {
				if ((date < 1) || (date > 28))
					flag = false;
			}
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			if ((date < 1) || (date > 30))
				flag = false;
		}
		if ((date < 1) || (date > 31))
			flag = false;

		if (hour == 24) {
			if ((minute != 0) || (second != 0)) {
				flag = false;
			}
		}
		if ((hour > 24)
			|| (hour < 0)
			|| (minute > 59)
			|| (minute < 0)
			|| (second > 59)
			|| (second < 0)) {
			flag = false;
		}
		if (flag) {
			greCal =
				new GregorianCalendar(
					year,
					month - 1,
					date,
					hour,
					minute,
					second);
		milsecond = milsubsecond;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 *  构造函数<br>
	 * @param millis 毫秒
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public VDateTimeUtil(long millis) {
		greCal = new GregorianCalendar();
		java.util.Date date = new java.util.Date(millis);
		greCal.setTime(date);
		milsecond = (int) (millis % 1000);
	}

	/**
	 *  构造函数<br>
	 * @param sqlDate 日期对象
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public VDateTimeUtil(java.sql.Date sqlDate) {
		greCal = new GregorianCalendar();
		greCal.setTime(sqlDate);
		SimpleDateFormat sdf1 = new SimpleDateFormat("SSS");
		milsecond = Integer.parseInt(sdf1.format(new Date()));
	}
	
	/**
	 *  构造函数<br>
	 * @param date  日期对象
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */

	public VDateTimeUtil(java.util.Date date) {
		greCal = new GregorianCalendar();
		greCal.setTime(date);
		SimpleDateFormat sdf1 = new SimpleDateFormat("SSS");
		milsecond = Integer.parseInt(sdf1.format(new Date()));
	}

	/**
	 * 获取两个VDateTime,VDateTime之间天数,计算算法：datetime2-datetime1 
	 * @param first  日期时间对象
	 * @param second  日期时间对象
	 * @return  两个VDateTime,VDateTime之间天数
	 * @exception 
	 * @see 
	 */
	public final static int getDaysBetween(VDateTimeUtil first, VDateTimeUtil second) {
		if (!first.greCal.getTimeZone().equals(second.greCal.getTimeZone())) {
			throw new IllegalArgumentException();
		}
		return VDateUtil.getDaysBetween(first.getVDate(), second.getVDate());
	}
	/**
	 * 获取所输入月份的天数
	 * @param year  年
	 * @param month 月
	 * @return   所输入月份的天数
	 */
	public final static int getDaysOfMonth(int year, int month) {
		return VDateUtil.getDaysMonth(year, month);
	}
	
	/**
	 *得到当前的时间，精确到毫秒，格式为：hh:mm:ss:SSS
	 *
	 * @return 为一个字符串
	 */
	public static String getCurrentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
		Date d = new Date();
		return sdf.format(d).toString();
	}
	
	/**
	 * 得到当前的时间，精确到毫秒，格式为：yyyy-MM-dd hh:mm:ss:SSS
	 * 
	 * @return 为一个字符串
	 */
	public static String getCurrentVTS(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		Date d = new Date();
		return sdf.format(d).toString();
	}
	
	/**
	 * 得到当前的时间，精确到毫秒，格式为：yyyyMMddHHmmssSSS
	 * 
	 * @return 为一个字符串
	 */
	public static String getCurrentTimeNum(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date d = new Date();
		return sdf.format(d).toString();
	}

	/**
	 * 得到当前的时间
	 * 
	 * @return 当前的时间
	 */
	public final long getTime() {
		Date date = greCal.getTime();
		return date.getTime();
	}
	
	/**
	 * 判断是否为闰年
	 * @param year  年
	 * @return 如果是闰年，则返回true;如果是平年，则返回false
	 * @exception 
	 * @see 
	 */
	
	public final static boolean isLeapYear(int year) {
		GregorianCalendar gc = new GregorianCalendar();
		return gc.isLeapYear(year);
	}
	/**
	 * 判断当前对象是否在when之后
	 * @param when  日期时间对象
	 * @return 如果在when之后，则返回true;否则返回false
	 * @exception 
	 * @see 
	 */
	
	public final boolean after(VDateTimeUtil when) {
		if (!greCal.getTimeZone().equals(when.greCal.getTimeZone())) {
			throw new IllegalArgumentException();
		}
		return getTime() > when.getTime();
	}
	/**
	 * 判断当前对象是否在when之前
	 * @param when  日期时间对象
	 * @return 如果在when之前，返回true;否则返回false
	 * @exception 
	 * @see 
	 */
	
	public final boolean before(VDateTimeUtil when) {
		if (!greCal.getTimeZone().equals(when.greCal.getTimeZone())) {
			throw new IllegalArgumentException();
		}
		return getTime() < when.getTime();
	}
	/**
	 * 比较当前对象与date时间顺序的前后
	 * @param datetime    日期时间对象
	 * @return 如果在date之后，返回1;如果在之前，返回－1;同一时间返回0
	 * 
	 */
	public final int compareTo(VDateTimeUtil datetime) {
		if (datetime == null) {
			return Integer.MIN_VALUE;
		} else if (after(datetime)) {
			return 1;
		} else if (before(datetime)) {
			return -1;
		} else
			return 0;
	}
	/**
	 *  判断两个对象是否为相等
	 * @param o  对象
	 * @return true:相等;false:不相等
	 * @exception 
	 * @see 
	 */

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o != null && getClass() == o.getClass()) {
			VDateTimeUtil datetime = (VDateTimeUtil) o;
			if (getTime() == datetime.getTime()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 获取散列代码
	 * @param
	 * @return int 哈希码
	 * @exception 
	 * @see 
	 */

	public int hashCode() {
		return greCal.hashCode();
	}
	/**
	 * 得到当前对象的VDate
	 * @param datetime 日期时间对象
	 * @return 当前对象的日期对象
	 * @exception 
	 * @see 
	 */
	public final static VDateUtil getVDate(VDateTimeUtil datetime) {
		return datetime.getVDate();
	}
	
	public final VDateUtil getVDate() {
		return new VDateUtil(
			greCal.get(Calendar.YEAR),
			greCal.get(Calendar.MONTH) + 1,
			greCal.get(Calendar.DATE));
	}
	
	/**
	 * 把一般的日期时间格式，改为定义的标准的日期时间格式
	 * 如：1982-3-4 12:12:12  改为 1982-03-04 12:12:12

	 * @param datetime 日期时间对象
	 * @return 符合规范的日期时间形式的字符串
	 * @exception 
	 * @see 
	 */
	public final String toLocaleString() {
		StringBuffer sb = new StringBuffer();
		int month = greCal.get(Calendar.MONTH) + 1;
		int date = greCal.get(Calendar.DATE);
		String strMonth = Integer.toString(month);
		if (strMonth.length() == 1) {
			strMonth = "0" + strMonth;
		}
		String strDate = Integer.toString(date);
		if (strDate.length() == 1) {
			strDate = "0" + strDate;
		}
		int hour = greCal.get(Calendar.HOUR);
		if (greCal.get(Calendar.AM_PM) == Calendar.PM) {
			hour = hour + 12;
		}
		int minute = greCal.get(Calendar.MINUTE);
		int second = greCal.get(Calendar.SECOND);
		int milsubsecond = milsecond;
	
		String strHour = Integer.toString(hour);
		if (strHour.length() == 1) {
			strHour = "0" + strHour;
		}
		String strMinute = Integer.toString(minute);
		if (strMinute.length() == 1) {
			strMinute = "0" + strMinute;
		}
		String strSecond = Integer.toString(second);
		if (strSecond.length() == 1) {
			strSecond = "0" + strSecond;
		}
		String strMilSecond = Integer.toString(milsubsecond);
		if(strMilSecond.length() == 1){
			strMilSecond = "00" + strMilSecond;
		}	
		else if(strMilSecond.length() == 2){
			strMilSecond = "0" + strMilSecond;
		}
		sb.append(Integer.toString(greCal.get(Calendar.YEAR)));
		sb.append("-");
		sb.append(strMonth);
		sb.append("-");
		sb.append(strDate);
		sb.append(" ");
		sb.append(strHour);
		sb.append(":");
		sb.append(strMinute);
		sb.append(":");
		sb.append(strSecond);
		//sb.append(":");
		//sb.append(strMilSecond);
		return sb.toString();
	}
	
	/** 
	 * 覆盖父类的toString()方法
	 * @param 
	 * @return 符合规范的日期时间形式的字符串
	 * @exception 
	 * @see 
	 */
	public String toString() {
		String str = toLocaleString();
		return str;
	}
	
}