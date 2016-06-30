package com.project.entity.report;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.project.entity.inn.InnRegion;

@Entity
@Table(name = "tomato_region_count")
public class RegionCount {
	
	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_region_count_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;
	
	private Double avgPrice;
	
	private Double checkInRadio;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	private Integer checkInRooms;
	
	@Transient
	private Integer noMergeRooms;
	
	@Transient
	private Integer totalRooms;
	
	@Transient
	private Double totalFee; 
	
	/**
     * 所属地区
     */
    @ManyToOne(cascade={CascadeType.REFRESH})
    @JoinColumn(name = "region_id", updatable = false)
    private InnRegion region;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public InnRegion getRegion() {
		return region;
	}

	public void setRegion(InnRegion region) {
		this.region = region;
	}

	public Integer getCheckInRooms() {
		return checkInRooms;
	}

	public void setCheckInRooms(Integer checkInRooms) {
		this.checkInRooms = checkInRooms;
	}

	public Integer getNoMergeRooms() {
		return noMergeRooms;
	}

	public void setNoMergeRooms(Integer noMergeRooms) {
		this.noMergeRooms = noMergeRooms;
	}

	public Integer getTotalRooms() {
		return totalRooms;
	}

	public void setTotalRooms(Integer totalRooms) {
		this.totalRooms = totalRooms;
	}

	public Double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Double totalFee) {
		this.totalFee = totalFee;
	}
}
