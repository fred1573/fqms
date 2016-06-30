package com.project.entity.bill;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * TomatoInnRoomType entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn_room_type", schema = "public")
public class TomatoInnRoomType implements java.io.Serializable {

	// Fields

	private Integer id;
	private TomatoInn tomatoInn;
	private String name;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private Double normalPrice;
	private Double weekendPrice;
	private Double holidayPrice;
	private Set<TomatoInnRoomTypePrice> tomatoInnRoomTypePrices = new HashSet<TomatoInnRoomTypePrice>(
			0);

	// Constructors

	/** default constructor */
	public TomatoInnRoomType() {
	}

	/** minimal constructor */
	public TomatoInnRoomType(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInnRoomType(Integer id, TomatoInn tomatoInn, String name,
			Timestamp createdAt, Timestamp updatedAt, Double normalPrice,
			Double weekendPrice, Double holidayPrice,
			Set<TomatoInnRoomTypePrice> tomatoInnRoomTypePrices) {
		this.id = id;
		this.tomatoInn = tomatoInn;
		this.name = name;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.normalPrice = normalPrice;
		this.weekendPrice = weekendPrice;
		this.holidayPrice = holidayPrice;
		this.tomatoInnRoomTypePrices = tomatoInnRoomTypePrices;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inn_id")
	public TomatoInn getTomatoInn() {
		return this.tomatoInn;
	}

	public void setTomatoInn(TomatoInn tomatoInn) {
		this.tomatoInn = tomatoInn;
	}

	@Column(name = "name", length = 32)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "created_at", length = 29)
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "updated_at", length = 29)
	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Column(name = "normal_price", precision = 17, scale = 17)
	public Double getNormalPrice() {
		return this.normalPrice;
	}

	public void setNormalPrice(Double normalPrice) {
		this.normalPrice = normalPrice;
	}

	@Column(name = "weekend_price", precision = 17, scale = 17)
	public Double getWeekendPrice() {
		return this.weekendPrice;
	}

	public void setWeekendPrice(Double weekendPrice) {
		this.weekendPrice = weekendPrice;
	}

	@Column(name = "holiday_price", precision = 17, scale = 17)
	public Double getHolidayPrice() {
		return this.holidayPrice;
	}

	public void setHolidayPrice(Double holidayPrice) {
		this.holidayPrice = holidayPrice;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoomType")
	public Set<TomatoInnRoomTypePrice> getTomatoInnRoomTypePrices() {
		return this.tomatoInnRoomTypePrices;
	}

	public void setTomatoInnRoomTypePrices(
			Set<TomatoInnRoomTypePrice> tomatoInnRoomTypePrices) {
		this.tomatoInnRoomTypePrices = tomatoInnRoomTypePrices;
	}

}