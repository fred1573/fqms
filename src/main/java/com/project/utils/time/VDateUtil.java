
package com.project.utils.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;



public final class VDateUtil implements Serializable {
	
	 //年
	private final int year;
	
	 // 月
	private final int month;
	
	 // 日
	private final int date;

	/**
	 * 得到当前对象的年
	 * @param int 返回年份
	 * @return 当前对象的年
	 * @exception 
	 * @see
	 */
	public final int getYear() {
		return year;
	}
	/**
	 * 得到当前对象的月份
	 * @param int 返回月份
	 * @return 当前对象的月份
	 * @exception 
	 * @see
	 */
	public final int getMonth() {
		return month;
	}
	/**
	 * 得到当前对象的日期
	 * @param int 返回当前日期
	 * @return 当前对象的日期
	 * @exception 
	 * @see
	 */
	public final int getDate() {
		return date;
	}
	/**
	 * 得到当前对象的日期
	 * @param int 返回当前日期
	 * @return 当前对象的日期
	 * @exception 
	 * @see
	 */
	public VDateUtil() {
		GregorianCalendar gc = new GregorianCalendar();
		this.year = gc.get(Calendar.YEAR);
		this.month = gc.get(Calendar.MONTH) + 1;
		this.date = gc.get(Calendar.DATE);
	}

	/**
	 * 构造函数
	 * 注意：日期的大小必须限制在1850-1-1到2050-12-31日之间。
	 * @param year	年
	 * @param month 月
	 * @param date  日
	 * @return 
	 * @exception 
	 * @see 
	
	 */

	public VDateUtil(int year, int month, int date) {
		if ((year > 2050) || (year < 1850) || (month > 12) || (month < 1)) {
			throw new IllegalArgumentException();
		}
		if (month == 2) {
			if (VDateTimeUtil.isLeapYear(year)) {
				if ((date < 0) || (date > 29))
					throw new IllegalArgumentException();
			} else {
				if ((date < 0) || (date > 28))
					throw new IllegalArgumentException();
			}
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			if ((date < 0) || (date > 31))
				throw new IllegalArgumentException();
		} else if ((date < 0) || (date > 32))
			throw new IllegalArgumentException();
		this.year = year;
		this.month = month;
		this.date = date;
	}

	/**
	 * 构造函数<br>
	 * 注意：日期的大小必须限制在1850-1-1到2050-12-31日之间。
	 * @param year  年
	 * @param month 月
	 * @param date  日
	 * @param hour  小时
	 * @param minute 分
	 * @return 
	 * @exception 
	 * @see 
	 */
	public VDateUtil(int year, int month, int date, int hour, int minute) {
		this(year, month, date);
	}

	/** 构造函数<br>
	 * 注意：日期的大小必须限制在1850-1-1到2050-12-31日之间。
	 * @param year   年
	 * @param month  月
	 * @param date   日
	 * @param hour   小时
	 * @param minute 分
	 * @param second 秒
	 * @return 
	 * @exception 
	 * @see 
	 */
	public VDateUtil(
		int year,
		int month,
		int date,
		int hour,
		int minute,
		int second) {
		this(year, month, date);
	}
	/**
	 * 构造函数<br>
	 * @param millis 毫秒数
	 * @return 
	 * @exception 
	 * @see 
	 */
	public VDateUtil(long millis) {
		GregorianCalendar gc = new GregorianCalendar();
		java.util.Date date = new java.util.Date(millis);
		gc.setTime(date);
		this.year = gc.get(Calendar.YEAR);
		this.month = gc.get(Calendar.MONTH) + 1;
		this.date = gc.get(Calendar.DATE);
	}
	/**
	 * 构造函数<br>
	 * @param strDate 日期形式的字符串，比如：1982-3-12
	 * @return 
	 * @exception 
	 * @see 
	 */
	public VDateUtil(String strDate) {
		this(strDate, true);
	}
	/**
	 * 构造函数<br>
	 * @param strDate 字符串
	 * @param isParse 是否需要判断字符串符合规范
	 * @return 
	 * @exception 
	 * @see 
	 */
	public VDateUtil(String strDate, boolean isParse) {
		if (isParse) {
			if (!VDateUtil.isAllowDate(strDate))
				throw new IllegalArgumentException();
		}
		strDate = strDate.trim();
		String s = null;
		int j = 0;
		int year = 0;
		int month = 0;
		int date = 0;
		StringTokenizer st = null;
		if ((strDate.indexOf('-') != -1) && (strDate.indexOf('/') == -1)) {
			st = new StringTokenizer(strDate, "-", false);
		} else if (
			(strDate.indexOf('/') != -1) && (strDate.indexOf('-') == -1)) {
			st = new StringTokenizer(strDate, "/", false);
		}
		while (st.hasMoreTokens()) {
			s = st.nextToken();
			if (j == 0) {
				year = Integer.parseInt(s);
			} else if (j == 1) {
				month = Integer.parseInt(s);
			} else if (j == 2) {
				date = Integer.parseInt(s);
			}
			j++;
		}
		this.year = year;
		this.month = month;
		this.date = date;
	}
	/**
	 * 构造函数<br>
	 * @param datetime 日期时间对象
	 * @return 
	 * @exception 
	 * @see 
	 */
	public VDateUtil(VDateTimeUtil datetime) {
		this(datetime.getTime());
	}
	/**
	 * 判断当前对象是否在日期when之后
	 * @param when 日期对象
	 * @return 如果在日期when之后,返回true;否则，返回false 
	 * @exception 
	 * @see 
	 */
	public final boolean after(VDateUtil when) {
		return getYear() != when.getYear()
			? getYear() > when.getYear()
			: getMonth() != when.getMonth()
			? getMonth() > when.getMonth()
			: (getDate() != when.getDate() ? getDate() > when.getDate() : false);
	}
	/**
	 * 判断当前对象是否在日期when之前
	 * @param when 日期对象
	 * @return 如果在日期when之前,返回true;否则，返回false
	 * @exception 
	 * @see 
	 */
	public final boolean before(VDateUtil when) {
		return getYear() != when.getYear()
			? getYear() < when.getYear()
			: getMonth() != when.getMonth()
			? getMonth() < when.getMonth()
			: (getDate() != when.getDate() ? getDate() < when.getDate() : false);
	}
	/**
	 * 比较当前对象与date时间顺序的前后
		 * 
	 * @param date 日期对象
	 * @return 如果在date之后，返回1;如果在之前，返回－1;同一时间返回0
	 * @exception 
	 * @see 
	 */
	public final int compareTo(VDateUtil date) {
		if (date == null) {
			return Integer.MIN_VALUE;
		} else if (after(date)) {
			return 1;
		} else if (before(date)) {
			return -1;
		} else
			return 0;
	}
	
	/**
	 * 判断是否为同一对象
	 * @param Object o　比较对像
	 * @return 如果为同一对象，则返回true;如果不为同一对象，则返回为false
	 * @exception 
	 * @see 
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o != null && getClass() == o.getClass()) {
			VDateUtil date = (VDateUtil) o;
			if (getYear() == date.getYear()
				&& getMonth() == date.getMonth()
				&& getDate() == date.getDate()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 得到当前对象days天后的日期
	 * @param int days
	 * @return 当前对象days天后的日期对象
	 * @exception 
	 * @see 
	 */
	public final VDateUtil getDateAfter(int days) {
		GregorianCalendar gc =
			new GregorianCalendar(getYear(), getMonth() - 1, getDate());
		java.util.Date udate = gc.getTime();
		long datetime = days * (long)(24 * 3600 * 1000);
		udate = new Date(udate.getTime() + datetime);
		gc.setTime(udate);
		int year = gc.get(Calendar.YEAR);
		int month = gc.get(Calendar.MONTH) + 1;
		int date = gc.get(Calendar.DATE);
		return new VDateUtil(year, month, date);
	}
	/**
	 * 得到当前对象days天前的日期
	 * @param days 天数
	 * @return 当前对象days天前日期对象
	 * @exception 
	 * @see 
	 */

	public final VDateUtil getDateBefore(int days) {
		GregorianCalendar gc =
			new GregorianCalendar(getYear(), getMonth() - 1, getDate());
		java.util.Date udate = gc.getTime();
		long datetime = days * (long)(24 * 3600 * 1000);
		udate = new Date(udate.getTime() - datetime);
		gc.setTime(udate);
		int year = gc.get(Calendar.YEAR);
		int month = gc.get(Calendar.MONTH) + 1;
		int date = gc.get(Calendar.DATE);
		return new VDateUtil(year, month, date);
	}
	/**
	 * 得到当前对象与when之间的天数
	 * @param when 日期对象
	 * @return 当前对象与when之间的天数
	 * @exception 
	 * @see 
	 */
	public final int getDaysAfter(VDateUtil when) {
		GregorianCalendar gc =
			new GregorianCalendar(getYear(), getMonth(), getDate());
		Date udate = gc.getTime();
		long thisTime = udate.getTime();
		gc =
			new GregorianCalendar(
				when.getYear(),
				when.getMonth(),
				when.getDate());
		udate = gc.getTime();
		long whenTime = udate.getTime();
		long millis = thisTime - whenTime;
		return (int) (millis / (24 * 3600 * 1000));
	}
	/**
	 * 得到两个日期间的天数
	 * @param begin 开始日期对象
	 * @param end   到达日期对象
	 * @return  两个日期间的天数
	 * @exception 
	 * @see 
	 */
	public final static int getDaysBetween(VDateUtil begin, VDateUtil end) {
		return end.getDaysAfter(begin);
	}
	/**
	 * 获取输入年的月份的天数
	 * @param year   年
	 * @param month  月
	 * @return 月份的天数
	 * @exception 
	 * @see 
	 */

	public final static int getDaysMonth(int year, int month) {
		if ((year > 2050) || (year < 1850) || (month < 1) || (month > 12)) {
			throw new IllegalArgumentException();
		}
		if (month == 2) {
			if (VDateTimeUtil.isLeapYear(year)) {
				return 29;
			} else
				return 28;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		}
		return 31;
	}
	/**
	 * 得到合法的日期形式
	 * @param sDate  字符串
	 * @return 合法的日期形式
	 * @exception 
	 * @see 
	 */
	public final static String getValidVDateString(String sDate) {
		if (!(VDateUtil.isAllowDate(sDate))) {
			throw new IllegalArgumentException();
		}
		sDate = sDate.trim();
		String s = null;
		StringBuffer sb = new StringBuffer();
		String[] strDateArr = new String[3];
		int j = 0;
		StringTokenizer st = null;
		if ((sDate.indexOf('-') != -1) && (sDate.indexOf('/') == -1)) {
			st = new StringTokenizer(sDate, "-", false);
		} else if ((sDate.indexOf('/') != -1) && (sDate.indexOf('-') == -1)) {
			st = new StringTokenizer(sDate, "/", false);
		}
		if (st == null)
			throw new IllegalArgumentException();
		while (st.hasMoreTokens()) {
			s = st.nextToken();
			if (j != 0) {
				if (s.length() == 1) {
					s = "0" + s;
				}
			}
			strDateArr[j++] = s;
		}

		return (
			sb.append(strDateArr[0]).append("-").append(strDateArr[1]).append(
				"-").append(
				strDateArr[2]))
			.toString();
	}
	
	/**
	 * 生成散列代码
	 * @param sDate  字符串
	 * @return  生成的散列代码
	 * @exception 
	 * @see 
	 */

	public int hashCode() {
		int result = 17;
		result = 37 * result + year;
		result = 37 * result + month;
		result = 37 * result + date;
		return result;
	}
	/**
	 * 判断一个字符串是否为合乎系统规定的字符串
	 * @param strDate 字符串
	 * @return 如果与系统规定相符，则返回为true;否则为false
	 * @exception 
	 * @see 
	 */

	public final static boolean isAllowDate(String strDate) {
		if ((strDate == null) || (strDate.length() == 0)) {
			return false;
		}
		strDate = strDate.trim();
		String s = null;
		int year = 0;
		int month = 0;
		int date = 0;
		int j = 0;
		StringTokenizer st = null;
		if ((strDate.indexOf('-') != -1) && (strDate.indexOf('/') == -1)) {
			st = new StringTokenizer(strDate, "-", false);
		} else if (
			(strDate.indexOf('/') != -1) && (strDate.indexOf('-') == -1)) {
			st = new StringTokenizer(strDate, "/", false);
		}
		if (st == null) {
			return false;
		} else if (st.countTokens() != 3) {
			return false;
		}
		while (st.hasMoreTokens()) {
			s = st.nextToken();
			if (j == 0) {
				try {
					year = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					return false;
				}
				if ((s.length() != 4) || (year > 2050) || (year < 1850)) {
					return false;
				}
			} else if (j == 1) {
				try {
					month = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					return false;
				}
				if ((month > 12) || (month < 1)) {
					return false;
				}
			} else if (j == 2) {
				try {
					date = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					return false;
				}
				if (month == 2) {
					if (VDateTimeUtil.isLeapYear(year)) {
						if ((date < 0) || (date > 30))
							return false;
					} else {
						if ((date < 0) || (date > 29))
							return false;
					}
				} else if (
					month == 4 || month == 6 || month == 9 || month == 11) {
					if ((date < 0) || (date > 31))
						return false;
				}
				if ((date < 1) || (date > 31))
					return false;
			}
			j++;
		}
		if (j != 3)
			return false;
		return true;
	}
	/**
	 * 把一个不合法的字符串转换为合法的字符串
	 * @param 
	 * @return 日期形式的字符串
	 * @exception 
	 * @see 
	 */
	public final String toLocaleString() {
		String strMonth = Integer.toString(month);
		if (strMonth.length() == 1) {
			strMonth = "0" + strMonth;
		}
		String strDate = Integer.toString(date);
		if (strDate.length() == 1) {
			strDate = "0" + strDate;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString(year));
		sb.append("-");
		sb.append(strMonth);
		sb.append("-");
		sb.append(strDate);
		return sb.toString();
	}
	/**
	 * 覆盖父类的toString()方法
	 * @param 
	 * @return 
	 * @exception 
	 * @see 
	 */
	public String toString() {
		String str = toLocaleString();
		return str;
	}
	
	/**
	 * 把把日期转换为十位的字符串
	 * @return 十位日期形式的字符串
	 */
	public final String toSimpleString() {
		String strMonth = Integer.toString(month);
		if (strMonth.length() == 1) {
			strMonth = "0" + strMonth;
		}
		String strDate = Integer.toString(date);
		if (strDate.length() == 1) {
			strDate = "0" + strDate;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString(year));
		sb.append(strMonth);
		sb.append(strDate);
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new VDateUtil());
		System.out.println(new VDateTimeUtil().toLocaleString());
		System.out.println(VDateTimeUtil.getCurrentVTS());
		
		
		for(int i=0;i<30;i++){
			System.out.println(new VDateTimeUtil().getTime() + "  " + System.currentTimeMillis());
		}
	}
}