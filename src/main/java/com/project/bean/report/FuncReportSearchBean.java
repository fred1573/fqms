package com.project.bean.report;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.project.common.Constants;
import com.project.entity.inn.InnFuncReport;
import com.project.utils.ListUtil;

public class FuncReportSearchBean {
	
	private String selectDate;
    private InnFuncReport lockScreen;
    private InnFuncReport notice;
    private InnFuncReport roomSort;
    private List<InnFuncReport> plugFunc;
    private List<InnFuncReport> ota;
    private List<InnFuncReport> roomStyle;
    private InnFuncReport active;
    private InnFuncReport chainStore;
    private Integer days = 6;
    
	public String getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(String selectDate) {
		this.selectDate = selectDate;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}
	

	public InnFuncReport getLockScreen() {
		return lockScreen;
	}

	public void setLockScreen(InnFuncReport lockScreen) {
		this.lockScreen = lockScreen;
	}

	public InnFuncReport getNotice() {
		return notice;
	}

	public void setNotice(InnFuncReport notice) {
		this.notice = notice;
	}

	public InnFuncReport getRoomSort() {
		return roomSort;
	}

	public void setRoomSort(InnFuncReport roomSort) {
		this.roomSort = roomSort;
	}

	public List<InnFuncReport> getPlugFunc() {
		return plugFunc;
	}

	public void setPlugFunc(List<InnFuncReport> plugFunc) {
		this.plugFunc = plugFunc;
	}

	public List<InnFuncReport> getOta() {
		return ota;
	}

	public void setOta(List<InnFuncReport> ota) {
		this.ota = ota;
	}

	public List<InnFuncReport> getRoomStyle() {
		return roomStyle;
	}

	public void setRoomStyle(List<InnFuncReport> roomStyle) {
		this.roomStyle = roomStyle;
	}

	public InnFuncReport getActive() {
		return active;
	}

	public void setActive(InnFuncReport active) {
		this.active = active;
	}

	public InnFuncReport getChainStore() {
		return chainStore;
	}

	public void setChainStore(InnFuncReport chainStore) {
		this.chainStore = chainStore;
	}

	public String getToDate() {
		DateTime fromDate = null;
		if(StringUtils.isBlank(this.getSelectDate())){
			fromDate = new DateTime().plusDays(-1);
			this.selectDate = fromDate.toString("yyyy-MM-dd");
		}else{
			fromDate = new DateTime(this.getSelectDate());
		}
		return fromDate.toString("yyyy-MM-dd");
	}

	public String getFromDate() {
		return new DateTime(this.getToDate()).plusDays(-this.getDays()).toString("yyyy-MM-dd");
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public void setReport(InnFuncReport report){
		Integer itemType = report.getFuncItemType();
		if(itemType >=1 && itemType <= 1000){
			if(ListUtil.isNotEmpty(this.plugFunc)){
				if(report.getFuncItemType() != 18 && report.getFuncItemType() != 8){
					this.plugFunc.add(report);
				}
			}else{
				this.plugFunc = Lists.newArrayList();
				if(report.getFuncItemType() != 18 && report.getFuncItemType() != 8){
					this.plugFunc.add(report);
				}
			}
			return;
		}else if(itemType >= 1001 && itemType <= 2000){
			if(ListUtil.isNotEmpty(this.ota)){
				this.ota.add(report);
			}else{
				this.ota = Lists.newArrayList();
				this.ota.add(report);
			}
			return;
		}else if(itemType >=2001 && itemType <= 3000){
			if(ListUtil.isNotEmpty(this.roomStyle)){
				this.roomStyle.add(report);
			}else{
				this.roomStyle = Lists.newArrayList();
				this.roomStyle.add(report);
			}
			return;
		}
		switch(itemType){
		case Constants.REPORT_ITEM_TYPE_LOCK:
			this.lockScreen = report;
			break;
		case Constants.REPORT_ITEM_TYPE_NOTICE:
			this.notice = report;
			break;
		case Constants.REPORT_ITEM_TYPE_ROOM_SORT:
			this.roomSort = report;
			break;
		case Constants.REPORT_ITEM_TYPE_ACTIVE:
			this.active = report;
			break;
		case Constants.REPORT_ITEM_TYPE_LINK_INN:
			this.chainStore = report;
			break;
		}
		
	}
	
}
