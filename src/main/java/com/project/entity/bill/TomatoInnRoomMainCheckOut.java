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
 * TomatoInnRoomMainCheckOut entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn_room_main_check_out", schema = "public")
public class TomatoInnRoomMainCheckOut implements java.io.Serializable {

	// Fields

	private Integer id;
	private TomatoInnRoomMainCheckIn tomatoInnRoomMainCheckIn;
	private String userRealName;
	private String contact;
	private Double costAmount;
	private Double paidInAmount;
	private Double moneyBack;
	private Double moneyFill;
	private Timestamp createdAt;
	private String orderNo;
	private Integer checkOutFrom;
	private Timestamp updatedAt;
	private String createdUser;
	private String updatedUser;
	private String uniqueCode;
	private Set<TomatoInnRoomCheckOut> tomatoInnRoomCheckOuts = new HashSet<TomatoInnRoomCheckOut>(
			0);

	// Constructors

	/** default constructor */
	public TomatoInnRoomMainCheckOut() {
	}

	/** minimal constructor */
	public TomatoInnRoomMainCheckOut(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInnRoomMainCheckOut(Integer id,
			TomatoInnRoomMainCheckIn tomatoInnRoomMainCheckIn,
			String userRealName, String contact, Double costAmount,
			Double paidInAmount, Double moneyBack, Double moneyFill,
			Timestamp createdAt, String orderNo, Integer checkOutFrom,
			Timestamp updatedAt, String createdUser, String updatedUser,
			String uniqueCode, Set<TomatoInnRoomCheckOut> tomatoInnRoomCheckOuts) {
		this.id = id;
		this.tomatoInnRoomMainCheckIn = tomatoInnRoomMainCheckIn;
		this.userRealName = userRealName;
		this.contact = contact;
		this.costAmount = costAmount;
		this.paidInAmount = paidInAmount;
		this.moneyBack = moneyBack;
		this.moneyFill = moneyFill;
		this.createdAt = createdAt;
		this.orderNo = orderNo;
		this.checkOutFrom = checkOutFrom;
		this.updatedAt = updatedAt;
		this.createdUser = createdUser;
		this.updatedUser = updatedUser;
		this.uniqueCode = uniqueCode;
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
	@JoinColumn(name = "main_check_in_id")
	public TomatoInnRoomMainCheckIn getTomatoInnRoomMainCheckIn() {
		return this.tomatoInnRoomMainCheckIn;
	}

	public void setTomatoInnRoomMainCheckIn(
			TomatoInnRoomMainCheckIn tomatoInnRoomMainCheckIn) {
		this.tomatoInnRoomMainCheckIn = tomatoInnRoomMainCheckIn;
	}

	@Column(name = "user_real_name", length = 32)
	public String getUserRealName() {
		return this.userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	@Column(name = "contact", length = 16)
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "cost_amount", precision = 17, scale = 17)
	public Double getCostAmount() {
		return this.costAmount;
	}

	public void setCostAmount(Double costAmount) {
		this.costAmount = costAmount;
	}

	@Column(name = "paid_in_amount", precision = 17, scale = 17)
	public Double getPaidInAmount() {
		return this.paidInAmount;
	}

	public void setPaidInAmount(Double paidInAmount) {
		this.paidInAmount = paidInAmount;
	}

	@Column(name = "money_back", precision = 17, scale = 17)
	public Double getMoneyBack() {
		return this.moneyBack;
	}

	public void setMoneyBack(Double moneyBack) {
		this.moneyBack = moneyBack;
	}

	@Column(name = "money_fill", precision = 17, scale = 17)
	public Double getMoneyFill() {
		return this.moneyFill;
	}

	public void setMoneyFill(Double moneyFill) {
		this.moneyFill = moneyFill;
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

	@Column(name = "check_out_from")
	public Integer getCheckOutFrom() {
		return this.checkOutFrom;
	}

	public void setCheckOutFrom(Integer checkOutFrom) {
		this.checkOutFrom = checkOutFrom;
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

	@Column(name = "unique_code", length = 64)
	public String getUniqueCode() {
		return this.uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tomatoInnRoomMainCheckOut")
	public Set<TomatoInnRoomCheckOut> getTomatoInnRoomCheckOuts() {
		return this.tomatoInnRoomCheckOuts;
	}

	public void setTomatoInnRoomCheckOuts(
			Set<TomatoInnRoomCheckOut> tomatoInnRoomCheckOuts) {
		this.tomatoInnRoomCheckOuts = tomatoInnRoomCheckOuts;
	}

}