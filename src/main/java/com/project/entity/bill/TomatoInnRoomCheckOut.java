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
 * TomatoInnRoomCheckOut entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn_room_check_out", schema = "public")
public class TomatoInnRoomCheckOut implements java.io.Serializable {

	// Fields

	private Integer id;
	private TomatoInnRoomCheckIn tomatoInnRoomCheckIn;
	private TomatoInnRoomMainCheckOut tomatoInnRoomMainCheckOut;
	private TomatoInnRoom tomatoInnRoom;
	private Double finalRoomPrice;
	private Timestamp checkInAt;
	private Timestamp checkOutAt;

	// Constructors

	/** default constructor */
	public TomatoInnRoomCheckOut() {
	}

	/** minimal constructor */
	public TomatoInnRoomCheckOut(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInnRoomCheckOut(Integer id,
			TomatoInnRoomCheckIn tomatoInnRoomCheckIn,
			TomatoInnRoomMainCheckOut tomatoInnRoomMainCheckOut,
			TomatoInnRoom tomatoInnRoom, Double finalRoomPrice,
			Timestamp checkInAt, Timestamp checkOutAt) {
		this.id = id;
		this.tomatoInnRoomCheckIn = tomatoInnRoomCheckIn;
		this.tomatoInnRoomMainCheckOut = tomatoInnRoomMainCheckOut;
		this.tomatoInnRoom = tomatoInnRoom;
		this.finalRoomPrice = finalRoomPrice;
		this.checkInAt = checkInAt;
		this.checkOutAt = checkOutAt;
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
	@JoinColumn(name = "check_in_id")
	public TomatoInnRoomCheckIn getTomatoInnRoomCheckIn() {
		return this.tomatoInnRoomCheckIn;
	}

	public void setTomatoInnRoomCheckIn(
			TomatoInnRoomCheckIn tomatoInnRoomCheckIn) {
		this.tomatoInnRoomCheckIn = tomatoInnRoomCheckIn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_id")
	public TomatoInnRoomMainCheckOut getTomatoInnRoomMainCheckOut() {
		return this.tomatoInnRoomMainCheckOut;
	}

	public void setTomatoInnRoomMainCheckOut(
			TomatoInnRoomMainCheckOut tomatoInnRoomMainCheckOut) {
		this.tomatoInnRoomMainCheckOut = tomatoInnRoomMainCheckOut;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	public TomatoInnRoom getTomatoInnRoom() {
		return this.tomatoInnRoom;
	}

	public void setTomatoInnRoom(TomatoInnRoom tomatoInnRoom) {
		this.tomatoInnRoom = tomatoInnRoom;
	}

	@Column(name = "final_room_price", precision = 17, scale = 17)
	public Double getFinalRoomPrice() {
		return this.finalRoomPrice;
	}

	public void setFinalRoomPrice(Double finalRoomPrice) {
		this.finalRoomPrice = finalRoomPrice;
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

}