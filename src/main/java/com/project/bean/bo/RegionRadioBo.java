package com.project.bean.bo;

import java.util.Date;

import com.project.utils.time.DateUtil;

public class RegionRadioBo {
	
	private Integer areaId;
	
	private Integer checkInRooms;
	
	private Double avgPrice;
	
	private Double checkInRadio;
	
	private String createTime;
	
	public RegionRadioBo(Integer id, Double avgPrice, Double checkInRadio, Date time, Integer checkInRooms){
		this.areaId = id;
		this.avgPrice = avgPrice;
		this.checkInRadio = checkInRadio;
		this.createTime = DateUtil.format(time);
		this.checkInRooms = checkInRooms;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public Double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Double avgPrice) {
		this.avgPrice = avgPrice;
	}

	public Double getCheckInRadio() {
		return checkInRadio;
	}

	public void setCheckInRadio(Double checkInRadio) {
		this.checkInRadio = checkInRadio;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getCheckInRooms() {
		return checkInRooms;
	}

	public void setCheckInRooms(Integer checkInRooms) {
		this.checkInRooms = checkInRooms;
	}
}
