package com.project.bean.bo;

public class RegionCheckInBo {
	
	private int totalCheckIn;
	
	private int baseNum;
	
	private String areaName;
	
	private double avgPrice;

	public int getTotalCheckIn() {
		return totalCheckIn;
	}

	public void setTotalCheckIn(int totalCheckIn) {
		this.totalCheckIn = totalCheckIn;
	}

	public int getBaseNum() {
		return baseNum;
	}

	public void setBaseNum(int baseNum) {
		this.baseNum = baseNum;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(double avgPrice) {
		this.avgPrice = avgPrice;
	}
	
}
