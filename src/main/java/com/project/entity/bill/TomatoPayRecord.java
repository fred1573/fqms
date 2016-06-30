package com.project.entity.bill;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TomatoPayRecord entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_pay_record", schema = "public")
public class TomatoPayRecord implements java.io.Serializable {

	// Fields

	private Integer id;
	private String orderCode;
	private String productCode;
	private String productName;
	private String payType;
	private String payMode;
	private Double payPrice;
	private String payStatus;
	private String payUser;
	private Timestamp payAt;
	private String tradeNo;
	private Integer innId;

	// Constructors

	/** default constructor */
	public TomatoPayRecord() {
	}

	/** minimal constructor */
	public TomatoPayRecord(Integer id, String orderCode) {
		this.id = id;
		this.orderCode = orderCode;
	}

	/** full constructor */
	public TomatoPayRecord(Integer id, String orderCode, String productCode,
			String productName, String payType, String payMode,
			Double payPrice, String payStatus, String payUser, Timestamp payAt,
			String tradeNo, Integer innId) {
		this.id = id;
		this.orderCode = orderCode;
		this.productCode = productCode;
		this.productName = productName;
		this.payType = payType;
		this.payMode = payMode;
		this.payPrice = payPrice;
		this.payStatus = payStatus;
		this.payUser = payUser;
		this.payAt = payAt;
		this.tradeNo = tradeNo;
		this.innId = innId;
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

	@Column(name = "order_code", nullable = false, length = 32)
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "product_code", length = 32)
	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@Column(name = "product_name", length = 50)
	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name = "pay_type", length = 1)
	public String getPayType() {
		return this.payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	@Column(name = "pay_mode", length = 1)
	public String getPayMode() {
		return this.payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	@Column(name = "pay_price", precision = 17, scale = 17)
	public Double getPayPrice() {
		return this.payPrice;
	}

	public void setPayPrice(Double payPrice) {
		this.payPrice = payPrice;
	}

	@Column(name = "pay_status", length = 1)
	public String getPayStatus() {
		return this.payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	@Column(name = "pay_user", length = 50)
	public String getPayUser() {
		return this.payUser;
	}

	public void setPayUser(String payUser) {
		this.payUser = payUser;
	}

	@Column(name = "pay_at", length = 29)
	public Timestamp getPayAt() {
		return this.payAt;
	}

	public void setPayAt(Timestamp payAt) {
		this.payAt = payAt;
	}

	@Column(name = "trade_no", length = 32)
	public String getTradeNo() {
		return this.tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@Column(name = "inn_id")
	public Integer getInnId() {
		return this.innId;
	}

	public void setInnId(Integer innId) {
		this.innId = innId;
	}

}