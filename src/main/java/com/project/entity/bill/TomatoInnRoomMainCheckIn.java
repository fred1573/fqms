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
 * TomatoInnRoomMainCheckIn entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn_room_main_check_in", schema = "public")
public class TomatoInnRoomMainCheckIn implements java.io.Serializable {

	// Fields

	private Integer id;
	private TomatoInnRoomMainOrder tomatoInnRoomMainOrder;
	private String userRealName;
	private Integer cardType;
	private String cardNo;
	private String contact;
	private Double roomFee;
	private Double commission;
	private Double fillRoomFee;
	private Double realRoomFee;
	private Double roomFeeBack;
	private Double payment;
	private Double paymentBack;
	private Timestamp createdAt;
	private String orderNo;
	private Timestamp updatedAt;
	private String createdUser;
	private String updatedUser;
	private Integer checkInFrom;
	private String comeFrom;
	private String otaOrderNo;
	private String nation;
	private String address;
	private String uniqueCode;
	private Double totalAmount;
	private Double paidAmount;
	private Double paidPayment;
	private String picStream;
	private Set<TomatoInnRoomCheckIn> tomatoInnRoomCheckIns = new HashSet<TomatoInnRoomCheckIn>(
			0);
	private Set<TomatoInnRoomMainCheckOut> tomatoInnRoomMainCheckOuts = new HashSet<TomatoInnRoomMainCheckOut>(
			0);
	// Constructors

	/** default constructor */
	public TomatoInnRoomMainCheckIn() {
	}

	/** minimal constructor */
	public TomatoInnRoomMainCheckIn(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInnRoomMainCheckIn(Integer id,
			Integer cardType, String cardNo, String contact, Double roomFee,
			Double commission, Double fillRoomFee, Double realRoomFee,
			Double roomFeeBack, Double payment, Double paymentBack,
			Timestamp createdAt, String orderNo, Timestamp updatedAt,
			String createdUser, String updatedUser, Integer checkInFrom,
			String comeFrom, String otaOrderNo, String nation, String address,
			String uniqueCode, Double totalAmount, Double paidAmount,
			Double paidPayment, String picStream,
			Set<TomatoInnRoomCheckIn> tomatoInnRoomCheckIns,
			Set<TomatoInnRoomMainCheckOut> tomatoInnRoomMainCheckOuts
			) {
		this.id = id;
		this.userRealName = userRealName;
		this.cardType = cardType;
		this.cardNo = cardNo;
		this.contact = contact;
		this.roomFee = roomFee;
		this.commission = commission;
		this.fillRoomFee = fillRoomFee;
		this.realRoomFee = realRoomFee;
		this.roomFeeBack = roomFeeBack;
		this.payment = payment;
		this.paymentBack = paymentBack;
		this.createdAt = createdAt;
		this.orderNo = orderNo;
		this.updatedAt = updatedAt;
		this.createdUser = createdUser;
		this.updatedUser = updatedUser;
		this.checkInFrom = checkInFrom;
		this.comeFrom = comeFrom;
		this.otaOrderNo = otaOrderNo;
		this.nation = nation;
		this.address = address;
		this.uniqueCode = uniqueCode;
		this.totalAmount = totalAmount;
		this.paidAmount = paidAmount;
		this.paidPayment = paidPayment;
		this.picStream = picStream;
		this.tomatoInnRoomCheckIns = tomatoInnRoomCheckIns;
		this.tomatoInnRoomMainCheckOuts = tomatoInnRoomMainCheckOuts;
		
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
	@JoinColumn(name = "main_order_id")
	public TomatoInnRoomMainOrder getTomatoInnRoomMainOrder() {
		return this.tomatoInnRoomMainOrder;
	}

	public void setTomatoInnRoomMainOrder(
			TomatoInnRoomMainOrder tomatoInnRoomMainOrder) {
		this.tomatoInnRoomMainOrder = tomatoInnRoomMainOrder;
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

	@Column(name = "contact", length = 16)
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "room_fee", precision = 17, scale = 17)
	public Double getRoomFee() {
		return this.roomFee;
	}

	public void setRoomFee(Double roomFee) {
		this.roomFee = roomFee;
	}

	@Column(name = "commission", precision = 17, scale = 17)
	public Double getCommission() {
		return this.commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	@Column(name = "fill_room_fee", precision = 17, scale = 17)
	public Double getFillRoomFee() {
		return this.fillRoomFee;
	}

	public void setFillRoomFee(Double fillRoomFee) {
		this.fillRoomFee = fillRoomFee;
	}

	@Column(name = "real_room_fee", precision = 17, scale = 17)
	public Double getRealRoomFee() {
		return this.realRoomFee;
	}

	public void setRealRoomFee(Double realRoomFee) {
		this.realRoomFee = realRoomFee;
	}

	@Column(name = "room_fee_back", precision = 17, scale = 17)
	public Double getRoomFeeBack() {
		return this.roomFeeBack;
	}

	public void setRoomFeeBack(Double roomFeeBack) {
		this.roomFeeBack = roomFeeBack;
	}

	@Column(name = "payment", precision = 17, scale = 17)
	public Double getPayment() {
		return this.payment;
	}

	public void setPayment(Double payment) {
		this.payment = payment;
	}

	@Column(name = "payment_back", precision = 17, scale = 17)
	public Double getPaymentBack() {
		return this.paymentBack;
	}

	public void setPaymentBack(Double paymentBack) {
		this.paymentBack = paymentBack;
	}

	@Column(name = "created_at", length = 29)
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "order_no", length = 50)
	public String getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "updated_at", length = 29)
	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Column(name = "created_user", length = 64)
	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	@Column(name = "updated_user", length = 64)
	public String getUpdatedUser() {
		return this.updatedUser;
	}

	public void setUpdatedUser(String updatedUser) {
		this.updatedUser = updatedUser;
	}

	@Column(name = "check_in_from")
	public Integer getCheckInFrom() {
		return this.checkInFrom;
	}

	public void setCheckInFrom(Integer checkInFrom) {
		this.checkInFrom = checkInFrom;
	}

	@Column(name = "come_from", length = 3)
	public String getComeFrom() {
		return this.comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}

	@Column(name = "ota_order_no", length = 50)
	public String getOtaOrderNo() {
		return this.otaOrderNo;
	}

	public void setOtaOrderNo(String otaOrderNo) {
		this.otaOrderNo = otaOrderNo;
	}

	@Column(name = "nation", length = 5)
	public String getNation() {
		return this.nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	@Column(name = "address", length = 200)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "unique_code", length = 64)
	public String getUniqueCode() {
		return this.uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@Column(name = "total_amount", precision = 17, scale = 17)
	public Double getTotalAmount() {
		return this.totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Column(name = "paid_amount", precision = 17, scale = 17)
	public Double getPaidAmount() {
		return this.paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	@Column(name = "paid_payment", precision = 17, scale = 17)
	public Double getPaidPayment() {
		return this.paidPayment;
	}

	public void setPaidPayment(Double paidPayment) {
		this.paidPayment = paidPayment;
	}

	@Column(name = "pic_stream")
	public String getPicStream() {
		return this.picStream;
	}

	public void setPicStream(String picStream) {
		this.picStream = picStream;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoomMainCheckIn")
	public Set<TomatoInnRoomCheckIn> getTomatoInnRoomCheckIns() {
		return this.tomatoInnRoomCheckIns;
	}

	public void setTomatoInnRoomCheckIns(
			Set<TomatoInnRoomCheckIn> tomatoInnRoomCheckIns) {
		this.tomatoInnRoomCheckIns = tomatoInnRoomCheckIns;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoomMainCheckIn")
	public Set<TomatoInnRoomMainCheckOut> getTomatoInnRoomMainCheckOuts() {
		return this.tomatoInnRoomMainCheckOuts;
	}

	public void setTomatoInnRoomMainCheckOuts(
			Set<TomatoInnRoomMainCheckOut> tomatoInnRoomMainCheckOuts) {
		this.tomatoInnRoomMainCheckOuts = tomatoInnRoomMainCheckOuts;
	}
}