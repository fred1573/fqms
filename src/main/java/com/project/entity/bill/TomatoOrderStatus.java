package com.project.entity.bill;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * TomatoOrderStatus entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_order_status", schema = "public")
public class TomatoOrderStatus implements java.io.Serializable {

	// Fields

	private Integer id;
	private Short orderType;
	private String uniqueCode;
	private String createdUser;
	private Timestamp createdAt;

	// Constructors

	/** default constructor */
	public TomatoOrderStatus() {
	}

	/** minimal constructor */
	public TomatoOrderStatus(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoOrderStatus(Integer id, Short orderType, String uniqueCode,
			String createdUser, Timestamp createdAt) {
		this.id = id;
		this.orderType = orderType;
		this.uniqueCode = uniqueCode;
		this.createdUser = createdUser;
		this.createdAt = createdAt;
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

	@Column(name = "order_type")
	public Short getOrderType() {
		return this.orderType;
	}

	public void setOrderType(Short orderType) {
		this.orderType = orderType;
	}

	@Column(name = "unique_code", length = 64)
	public String getUniqueCode() {
		return this.uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@Column(name = "created_user", length = 64)
	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	@Column(name = "created_at", length = 29)
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

}