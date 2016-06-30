package com.project.entity.bill;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * TomatoInnRoomTypePrice entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn_room_type_price", schema = "public")
public class TomatoInnRoomTypePrice implements java.io.Serializable {

	// Fields

	private Integer id;
	private TomatoInnRoomType tomatoInnRoomType;
	private Timestamp specialDate;
	private Double price;
	private String reason;
	private Timestamp createdAt;
	private String createdUser;
	private String flag;
	private Integer otaId;

	// Constructors

	/** default constructor */
	public TomatoInnRoomTypePrice() {
	}

	/** minimal constructor */
	public TomatoInnRoomTypePrice(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInnRoomTypePrice(Integer id,
			TomatoInnRoomType tomatoInnRoomType, Timestamp specialDate,
			Double price, String reason, Timestamp createdAt,
			String createdUser, String flag, Integer otaId) {
		this.id = id;
		this.tomatoInnRoomType = tomatoInnRoomType;
		this.specialDate = specialDate;
		this.price = price;
		this.reason = reason;
		this.createdAt = createdAt;
		this.createdUser = createdUser;
		this.flag = flag;
		this.otaId = otaId;
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
	@JoinColumn(name = "room_type_id")
	public TomatoInnRoomType getTomatoInnRoomType() {
		return this.tomatoInnRoomType;
	}

	public void setTomatoInnRoomType(TomatoInnRoomType tomatoInnRoomType) {
		this.tomatoInnRoomType = tomatoInnRoomType;
	}

	@Column(name = "special_date", length = 29)
	public Timestamp getSpecialDate() {
		return this.specialDate;
	}

	public void setSpecialDate(Timestamp specialDate) {
		this.specialDate = specialDate;
	}

	@Column(name = "price", precision = 17, scale = 17)
	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Column(name = "reason", length = 50)
	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "created_at", length = 29)
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "created_user", length = 64)
	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	@Column(name = "flag", length = 36)
	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Column(name = "ota_id")
	public Integer getOtaId() {
		return this.otaId;
	}

	public void setOtaId(Integer otaId) {
		this.otaId = otaId;
	}

}