package com.project.bean.report;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

public class ActiveSearchBean {
	
	// 活跃类型：（0：全部 1：登陆，2：预定，3：入住）
	public static final int ACTIVE_TYPE_ALL = 0;
	public static final int ACTIVE_TYPE_LOGIN = 1;
	public static final int ACTIVE_TYPE_BOOK = 2;
	public static final int ACTIVE_TYPE_CHECK = 3;
		
	private String selectDate;
    private int areaId = 1;
    private String areaName = "大理";
    private Boolean activeFlag;
    private int activeType = 0;
    private String innIds;
    private boolean isPage = true;
    private String sortDate;
    private String toDate;
    private String fromDate;
    
    private Integer days;
    
	public String getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(String selectDate) {
		this.selectDate = selectDate;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Boolean getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public int getActiveType() {
		return activeType;
	}

	public void setActiveType(int activeType) {
		this.activeType = activeType;
	}

	public String getInnIds() {
		return innIds;
	}

	public void setInnIds(String innIds) {
		this.innIds = innIds;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}
	
	

	public boolean isPage() {
		return isPage;
	}

	public void setPage(boolean isPage) {
		this.isPage = isPage;
	}
	
	public String getSortDate() {
		if(StringUtils.isBlank(this.sortDate)){
			this.sortDate = this.getToDate();
		}
		return this.sortDate;
	}

	public void setSortDate(String sortDate) {
		this.sortDate = sortDate;
	}

	public String getFromDate() {
		DateTime fromDate = null;
		if(StringUtils.isBlank(this.getSelectDate())){
			fromDate = new DateTime().plusDays(-this.getDays()+1);
		}else{
			fromDate = new DateTime(this.getSelectDate());
		}
		this.fromDate = fromDate.toString("yyyy-MM-dd");
		return this.fromDate;
	}

	public String getToDate() {
		if(StringUtils.isBlank(this.toDate)){
			this.toDate = new DateTime(this.getFromDate()).plusDays(this.getDays()-1).toString("yyyy-MM-dd");
		}
		return this.toDate;
	}
	
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
