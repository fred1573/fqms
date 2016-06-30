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
 * TomatoInnRoomOrder entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn_room_order", schema = "public")
public class TomatoInnRoomOrder implements java.io.Serializable {

	// Fields

	private Integer id;
	private TomatoInnRoomMainOrder tomatoInnRoomMainOrder;
	private TomatoInnRoom tomatoInnRoom;
	private Timestamp checkInAt;
	private Timestamp checkOutAt;
	private Double bookRoomPrice;
	private Double incomeRoomPrice;
	private Integer status;
	private Set<TomatoInnRoomCheckIn> tomatoInnRoomCheckIns = new HashSet<TomatoInnRoomCheckIn>(
			0);

	// Constructors

	/** default constructor */
	public TomatoInnRoomOrder() {
	}

	/** minimal constructor */
	public TomatoInnRoomOrder(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInnRoomOrder(Integer id,
			TomatoInnRoomMainOrder tomatoInnRoomMainOrder,
			TomatoInnRoom tomatoInnRoom, Timestamp checkInAt,
			Timestamp checkOutAt, Double bookRoomPrice, Double incomeRoomPrice,
			Integer status, Set<TomatoInnRoomCheckIn> tomatoInnRoomCheckIns) {
		this.id = id;
		this.tomatoInnRoomMainOrder = tomatoInnRoomMainOrder;
		this.tomatoInnRoom = tomatoInnRoom;
		this.checkInAt = checkInAt;
		this.checkOutAt = checkOutAt;
		this.bookRoomPrice = bookRoomPrice;
		this.incomeRoomPrice = incomeRoomPrice;
		this.status = status;
		this.tomatoInnRoomCheckIns = tomatoInnRoomCheckIns;
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
	@JoinColumn(name = "main_id")
	public TomatoInnRoomMainOrder getTomatoInnRoomMainOrder() {
		return this.tomatoInnRoomMainOrder;
	}

	public void setTomatoInnRoomMainOrder(
			TomatoInnRoomMainOrder tomatoInnRoomMainOrder) {
		this.tomatoInnRoomMainOrder = tomatoInnRoomMainOrder;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	public TomatoInnRoom getTomatoInnRoom() {
		return this.tomatoInnRoom;
	}

	public void setTomatoInnRoom(TomatoInnRoom tomatoInnRoom) {
		this.tomatoInnRoom = tomatoInnRoom;
	}

	@Column(name = "check_in_at", length = 29)
	public Timestamp getCheckInAt() {
		return this.checkInAt;
	}

	public void setCheckInAt(Timestamp checkInAt) {
		this.checkInAt = checkInAt;
	}

	@Column(name = "check_out_at", length = 29)
	public Timestamp getCheckOutAt() {
		return this.checkOutAt;
	}

	public void setCheckOutAt(Timestamp checkOutAt) {
		this.checkOutAt = checkOutAt;
	}

	@Column(name = "book_room_price", precision = 17, scale = 17)
	public Double getBookRoomPrice() {
		return this.bookRoomPrice;
	}

	public void setBookRoomPrice(Double bookRoomPrice) {
		this.bookRoomPrice = bookRoomPrice;
	}

	@Column(name = "income_room_price", precision = 17, scale = 17)
	public Double getIncomeRoomPrice() {
		return this.incomeRoomPrice;
	}

	public void setIncomeRoomPrice(Double incomeRoomPrice) {
		this.incomeRoomPrice = incomeRoomPrice;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoomOrder")
	public Set<TomatoInnRoomCheckIn> getTomatoInnRoomCheckIns() {
		return this.tomatoInnRoomCheckIns;
	}

	public void setTomatoInnRoomCheckIns(
			Set<TomatoInnRoomCheckIn> tomatoInnRoomCheckIns) {
		this.tomatoInnRoomCheckIns = tomatoInnRoomCheckIns;
	}

}