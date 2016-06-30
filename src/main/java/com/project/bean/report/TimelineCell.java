package com.project.bean.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

/**
 * 时间轴-单元格
 *
 */
public class TimelineCell {
	
	private Date cdate;
	private int dayOfWeek;
	private boolean isYesterday = false;
	private boolean isToday = false;
	private String weekday;
	
	public Date getCdate() {
		return cdate;
	}

	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public boolean isYesterday() {
		return isYesterday;
	}

	public void setYesterday(boolean isYesterday) {
		this.isYesterday = isYesterday;
	}

	public boolean isToday() {
		return isToday;
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}
	
	public String getCdateOfString() {
		return new DateTime(cdate).toString("yyyy-MM-dd");
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return "TimelineCell [cdate=" + sdf.format(cdate) + ",dayOfWeek="+dayOfWeek+", weekday=" + weekday
				+ ", isYesterday=" + isYesterday + ", isToday=" + isToday + "]";
	}
	
}
