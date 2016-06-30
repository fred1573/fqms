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
 * TomatoInnRoomCheckIn entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn_room_check_in", schema = "public")
public class TomatoInnRoomCheckIn implements java.io.Serializable {

	// Fields

	private Integer id;
	private TomatoInnRoomMainCheckIn tomatoInnRoomMainCheckIn;
	private TomatoInnRoomOrder tomatoInnRoomOrder;
	private TomatoInnRoom tomatoInnRoom;
	private String userRealName;
	private Integer cardType;
	private String cardNo;
	private String contact;
	private Double realRoomPrice;
	private Double incomeRoomPrice;
	private Timestamp checkInAt;
	private Timestamp checkOutAt;
	private Integer status;
	private Set<TomatoInnRoomCheckOut> tomatoInnRoomCheckOuts = new HashSet<TomatoInnRoomCheckOut>(
			0);

	// Constructors

	/** default constructor */
	public TomatoInnRoomCheckIn() {
	}

	/** minimal constructor */
	public TomatoInnRoomCheckIn(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInnRoomCheckIn(Integer id,
			TomatoInnRoomMainCheckIn tomatoInnRoomMainCheckIn,
			TomatoInnRoomOrder tomatoInnRoomOrder, TomatoInnRoom tomatoInnRoom,
			String userRealName, Integer cardType, String cardNo,
			String contact, Double realRoomPrice, Double incomeRoomPrice,
			Timestamp checkInAt, Timestamp checkOutAt, Integer status,
			Set<TomatoInnRoomCheckOut> tomatoInnRoomCheckOuts) {
		this.id = id;
		this.tomatoInnRoomMainCheckIn = tomatoInnRoomMainCheckIn;
		this.tomatoInnRoomOrder = tomatoInnRoomOrder;
		this.tomatoInnRoom = tomatoInnRoom;
		this.userRealName = userRealName;
		this.cardType = cardType;
		this.cardNo = cardNo;
		this.contact = contact;
		this.realRoomPrice = realRoomPrice;
		this.incomeRoomPrice = incomeRoomPrice;
		this.checkInAt = checkInAt;
		this.checkOutAt = checkOutAt;
		this.status = status;
		this.tomatoInnRoomCheckOuts = tomatoInnRoomCheckOuts;
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
	public TomatoInnRoomMainCheckIn getTomatoInnRoomMainCheckIn() {
		return this.tomatoInnRoomMainCheckIn;
	}

	public void setTomatoInnRoomMainCheckIn(
			TomatoInnRoomMainCheckIn tomatoInnRoomMainCheckIn) {
		this.tomatoInnRoomMainCheckIn = tomatoInnRoomMainCheckIn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	public TomatoInnRoomOrder getTomatoInnRoomOrder() {
		return this.tomatoInnRoomOrder;
	}

	public void setTomatoInnRoomOrder(TomatoInnRoomOrder tomatoInnRoomOrder) {
		this.tomatoInnRoomOrder = tomatoInnRoomOrder;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	public TomatoInnRoom getTomatoInnRoom() {
		return this.tomatoInnRoom;
	}

	public void setTomatoInnRoom(TomatoInnRoom tomatoInnRoom) {
		this.tomatoInnRoom = tomatoInnRoom;
	}

	@Column(name = "user_real_name", length = 32)
	public String getUserRealName() {
		return this.userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	@Column(name = "card_type")
	public Integer getCardType() {
		return this.cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	@Column(name = "card_no", length = 64)
	public String getCardNo() {
		return this.cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	@Column(name = "contact", length = 32)
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "real_room_price", precision = 17, scale = 17)
	public Double getRealRoomPrice() {
		return this.realRoomPrice;
	}

	public void setRealRoomPrice(Double realRoomPrice) {
		this.realRoomPrice = realRoomPrice;
	}

	@Column(name = "income_room_price", precision = 17, scale = 17)
	public Double getIncomeRoomPrice() {
		return this.incomeRoomPrice;
	}

	public void setIncomeRoomPrice(Double incomeRoomPrice) {
		this.incomeRoomPrice = incomeRoomPrice;
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

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoomCheckIn")
	public Set<TomatoInnRoomCheckOut> getTomatoInnRoomCheckOuts() {
		return this.tomatoInnRoomCheckOuts;
	}

	public void setTomatoInnRoomCheckOuts(
			Set<TomatoInnRoomCheckOut> tomatoInnRoomCheckOuts) {
		this.tomatoInnRoomCheckOuts = tomatoInnRoomCheckOuts;
	}

}