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
 * TomatoInnRoom entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn_room", schema = "public")
public class TomatoInnRoom implements java.io.Serializable {

	// Fields

	private Integer id;
	private TomatoInn tomatoInn;
	private String roomNo;
	private Integer serialNo;
	private Double price;
	private Integer roomTypeId;
	private Integer status;
	private String feature;
	private Timestamp createdAt;
	private Set<TomatoInnRoomCheckOut> tomatoInnRoomCheckOuts = new HashSet<TomatoInnRoomCheckOut>(
			0);
	private Set<TomatoInnRoomCheckIn> tomatoInnRoomCheckIns = new HashSet<TomatoInnRoomCheckIn>(
			0);
	private Set<TomatoInnRoomOrder> tomatoInnRoomOrders = new HashSet<TomatoInnRoomOrder>(
			0);

	// Constructors

	/** default constructor */
	public TomatoInnRoom() {
	}

	/** minimal constructor */
	public TomatoInnRoom(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInnRoom(Integer id, TomatoInn tomatoInn, String roomNo,
			Integer serialNo, Double price, Integer roomTypeId, Integer status,
			String feature, Timestamp createdAt,
			Set<TomatoInnRoomCheckOut> tomatoInnRoomCheckOuts,
			Set<TomatoInnRoomCheckIn> tomatoInnRoomCheckIns,
			Set<TomatoInnRoomOrder> tomatoInnRoomOrders) {
		this.id = id;
		this.tomatoInn = tomatoInn;
		this.roomNo = roomNo;
		this.serialNo = serialNo;
		this.price = price;
		this.roomTypeId = roomTypeId;
		this.status = status;
		this.feature = feature;
		this.createdAt = createdAt;
		this.tomatoInnRoomCheckOuts = tomatoInnRoomCheckOuts;
		this.tomatoInnRoomCheckIns = tomatoInnRoomCheckIns;
		this.tomatoInnRoomOrders = tomatoInnRoomOrders;
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

	@Column(name = "room_no", length = 32)
	public String getRoomNo() {
		return this.roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	@Column(name = "serial_no")
	public Integer getSerialNo() {
		return this.serialNo;
	}

	public void setSerialNo(Integer serialNo) {
		this.serialNo = serialNo;
	}

	@Column(name = "price", precision = 17, scale = 17)
	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Column(name = "room_type_id")
	public Integer getRoomTypeId() {
		return this.roomTypeId;
	}

	public void setRoomTypeId(Integer roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "feature")
	public String getFeature() {
		return this.feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	@Column(name = "created_at", length = 29)
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoom")
	public Set<TomatoInnRoomCheckOut> getTomatoInnRoomCheckOuts() {
		return this.tomatoInnRoomCheckOuts;
	}

	public void setTomatoInnRoomCheckOuts(
			Set<TomatoInnRoomCheckOut> tomatoInnRoomCheckOuts) {
		this.tomatoInnRoomCheckOuts = tomatoInnRoomCheckOuts;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoom")
	public Set<TomatoInnRoomCheckIn> getTomatoInnRoomCheckIns() {
		return this.tomatoInnRoomCheckIns;
	}

	public void setTomatoInnRoomCheckIns(
			Set<TomatoInnRoomCheckIn> tomatoInnRoomCheckIns) {
		this.tomatoInnRoomCheckIns = tomatoInnRoomCheckIns;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoom")
	public Set<TomatoInnRoomOrder> getTomatoInnRoomOrders() {
		return this.tomatoInnRoomOrders;
	}

	public void setTomatoInnRoomOrders(
			Set<TomatoInnRoomOrder> tomatoInnRoomOrders) {
		this.tomatoInnRoomOrders = tomatoInnRoomOrders;
	}
}