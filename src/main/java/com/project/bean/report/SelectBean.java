package com.project.bean.report;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class SelectBean {
	
	private String selectDate;
	
	private int pageNo = 1;
	
	private int type;
	
	private String input;
	
	public String getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(String selectDate) {
		this.selectDate = selectDate;
	}
	
	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getToDate() {
		DateTime fromDate = null;
		if(StringUtils.isBlank(this.getSelectDate())){
			fromDate = new DateTime();
			this.selectDate = fromDate.toString("yyyy-MM-dd");
		}else{
			fromDate = new DateTime(this.getSelectDate());
		}
		return fromDate.toString("yyyy-MM-dd");
	}
	
}
