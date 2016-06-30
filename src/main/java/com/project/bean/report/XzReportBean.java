package com.project.bean.report;

import java.util.Date;

public class XzReportBean {
	//客栈名称
	private String innName;
	
	private String webSite;
	
	private Date createdAt;
	
	private String openMC;
	//支付宝账户
	private String alipayUnit;
	
	public String getInnName() {
		return innName;
	}
	
	public void setInnName(String innName) {
		this.innName = innName;
	}
	
	public String getWebSite() {
		return webSite;
	}
	
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getAlipayUnit() {
		return alipayUnit;
	}
	
	public void setAlipayUnit(String alipayUnit) {
		this.alipayUnit = alipayUnit;
	}

	public String getOpenMC() {
		return openMC;
	}

	public void setOpenMC(String openMC) {
		this.openMC = openMC;
	}
	
}
