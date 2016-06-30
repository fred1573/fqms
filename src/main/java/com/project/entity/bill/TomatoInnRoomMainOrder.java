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
 * TomatoInnRoomMainOrder entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn_room_main_order", schema = "public")
public class TomatoInnRoomMainOrder implements java.io.Serializable {

	// Fields

	private Integer id;
	private String userRealName;
	private Integer cardType;
	private String cardNo;
	private String contact;
	private Short sex;
	private Double roomFee;
	private Double commission;
	private Double realRoomFee;
	private Double frontMoney;
	private Double frontMoneyBack;
	private Timestamp orderedAt;
	private Timestamp updatedAt;
	private String orderNo;
	private String createdUser;
	private String updatedUser;
	private Integer orderFrom;
	private String comeFrom;
	private String otaOrderNo;
	private String nation;
	private String address;
	private String uniqueCode;
	private Double totalAmount;
	private Double paidAmount;
	private Double paidPayment;
	private String picStream;
    private String isBalance;
	private Set<TomatoInnRoomOrder> tomatoInnRoomOrders = new HashSet<TomatoInnRoomOrder>(
			0);
	private Set<TomatoInnRoomMainCheckIn> tomatoInnRoomMainCheckIns = new HashSet<TomatoInnRoomMainCheckIn>(
			0);

	// Constructors

	/** default constructor */
	public TomatoInnRoomMainOrder() {
	}

	/** minimal constructor */
	public TomatoInnRoomMainOrder(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInnRoomMainOrder(Integer id,
			Integer cardType, String cardNo, String contact, Short sex,
			Double roomFee, Double commission, Double realRoomFee,
			Double frontMoney, Double frontMoneyBack, Timestamp orderedAt,
			Timestamp updatedAt, String orderNo, String createdUser,
			String updatedUser, Integer orderFrom, String comeFrom,
			String otaOrderNo, String nation, String address,
			String uniqueCode, Double totalAmount, Double paidAmount,
			Double paidPayment, String picStream,
			Set<TomatoInnRoomOrder> tomatoInnRoomOrders,
			Set<TomatoInnRoomMainCheckIn> tomatoInnRoomMainCheckIns) {
		this.id = id;
		this.userRealName = userRealName;
		this.cardType = cardType;
		this.cardNo = cardNo;
		this.contact = contact;
		this.sex = sex;
		this.roomFee = roomFee;
		this.commission = commission;
		this.realRoomFee = realRoomFee;
		this.frontMoney = frontMoney;
		this.frontMoneyBack = frontMoneyBack;
		this.orderedAt = orderedAt;
		this.updatedAt = updatedAt;
		this.orderNo = orderNo;
		this.createdUser = createdUser;
		this.updatedUser = updatedUser;
		this.orderFrom = orderFrom;
		this.comeFrom = comeFrom;
		this.otaOrderNo = otaOrderNo;
		this.nation = nation;
		this.address = address;
		this.uniqueCode = uniqueCode;
		this.totalAmount = totalAmount;
		this.paidAmount = paidAmount;
		this.paidPayment = paidPayment;
		this.picStream = picStream;
		this.tomatoInnRoomOrders = tomatoInnRoomOrders;
		this.tomatoInnRoomMainCheckIns = tomatoInnRoomMainCheckIns;
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

    @Column(name = "is_balance", length = 1)
    public String getIsBalance() {
        return isBalance;
    }

    public void setIsBalance(String isBalance) {
        this.isBalance = isBalance;
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

	@Column(name = "sex")
	public Short getSex() {
		return this.sex;
	}

	public void setSex(Short sex) {
		this.sex = sex;
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

	@Column(name = "real_room_fee", precision = 17, scale = 17)
	public Double getRealRoomFee() {
		return this.realRoomFee;
	}

	public void setRealRoomFee(Double realRoomFee) {
		this.realRoomFee = realRoomFee;
	}

	@Column(name = "front_money", precision = 17, scale = 17)
	public Double getFrontMoney() {
		return this.frontMoney;
	}

	public void setFrontMoney(Double frontMoney) {
		this.frontMoney = frontMoney;
	}

	@Column(name = "front_money_back", precision = 17, scale = 17)
	public Double getFrontMoneyBack() {
		return this.frontMoneyBack;
	}

	public void setFrontMoneyBack(Double frontMoneyBack) {
		this.frontMoneyBack = frontMoneyBack;
	}

	@Column(name = "ordered_at", length = 29)
	public Timestamp getOrderedAt() {
		return this.orderedAt;
	}

	public void setOrderedAt(Timestamp orderedAt) {
		this.orderedAt = orderedAt;
	}

	@Column(name = "updated_at", length = 29)
	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Column(name = "order_no", length = 50)
	public String getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	@Column(name = "order_from")
	public Integer getOrderFrom() {
		return this.orderFrom;
	}

	public void setOrderFrom(Integer orderFrom) {
		this.orderFrom = orderFrom;
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
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoomMainOrder")
	public Set<TomatoInnRoomOrder> getTomatoInnRoomOrders() {
		return this.tomatoInnRoomOrders;
	}

	public void setTomatoInnRoomOrders(
			Set<TomatoInnRoomOrder> tomatoInnRoomOrders) {
		this.tomatoInnRoomOrders = tomatoInnRoomOrders;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoomMainOrder")
	public Set<TomatoInnRoomMainCheckIn> getTomatoInnRoomMainCheckIns() {
		return this.tomatoInnRoomMainCheckIns;
	}

	public void setTomatoInnRoomMainCheckIns(
			Set<TomatoInnRoomMainCheckIn> tomatoInnRoomMainCheckIns) {
		this.tomatoInnRoomMainCheckIns = tomatoInnRoomMainCheckIns;
	}

}