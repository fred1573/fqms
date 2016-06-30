package com.project.bean;


import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class SearchChannelOrderBean {
	
	private boolean isPage = true;
	
	/**1:客栈名称   2：订单号 */
	private int searchType = 1;
	
	private String input;
	
	private int innIds;
	
	private String fromDate;
	
	private String toDate;
	
	private int days = 30;
	
	private int channelId;
	
	private String isBalance;

	public boolean isPage() {
		return isPage;
	}

	public void setPage(boolean isPage) {
		this.isPage = isPage;
	}

	public int getSearchType() {
		return searchType;
	}

	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public int getInnIds() {
		return innIds;
	}

	public void setInnIds(int innIds) {
		this.innIds = innIds;
	}

	public String getFromDate() {
		DateTime fromDate = null;
		if(StringUtils.isBlank(this.fromDate)){
			fromDate = new DateTime().plusDays(-this.getDays()+1);
			this.fromDate = fromDate.toString("yyyy-MM-dd");
		}
		return this.fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
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

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getIsBalance() {
		return isBalance;
	}

	public void setIsBalance(String isBalance) {
		this.isBalance = isBalance;
	}
	
	
	
}
