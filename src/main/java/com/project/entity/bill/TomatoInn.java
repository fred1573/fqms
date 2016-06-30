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
 * TomatoInn entity. @author xiamaoxuan Persistence Tools
 */
@Entity
@Table(name = "tomato_inn", schema = "public")
public class TomatoInn implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private String address;
	private String contact;
	private String appId;
	private String appKey;
	private Timestamp registeredAt;
	private Timestamp updatedAt;
	private Integer tip;
	private String description;
	private Integer rechargeMsgs;
	private Integer autoCheckOut;

	// Constructors

	/** default constructor */
	public TomatoInn() {
	}

	/** minimal constructor */
	public TomatoInn(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TomatoInn(Integer id, String name,
			String address, String contact, String appId, String appKey,
			Timestamp registeredAt, Timestamp updatedAt, Integer tip,
			String description, Integer rechargeMsgs, Integer autoCheckOut
			) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.contact = contact;
		this.appId = appId;
		this.appKey = appKey;
		this.registeredAt = registeredAt;
		this.updatedAt = updatedAt;
		this.tip = tip;
		this.description = description;
		this.rechargeMsgs = rechargeMsgs;
		this.autoCheckOut = autoCheckOut;
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

	@Column(name = "name", length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "address", length = 128)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "contact", length = 16)
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "app_id", length = 32)
	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Column(name = "app_key", length = 64)
	public String getAppKey() {
		return this.appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	@Column(name = "registered_at", length = 29)
	public Timestamp getRegisteredAt() {
		return this.registeredAt;
	}

	public void setRegisteredAt(Timestamp registeredAt) {
		this.registeredAt = registeredAt;
	}

	@Column(name = "updated_at", length = 29)
	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Column(name = "tip")
	public Integer getTip() {
		return this.tip;
	}

	public void setTip(Integer tip) {
		this.tip = tip;
	}

	@Column(name = "description", length = 2000)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "recharge_msgs")
	public Integer getRechargeMsgs() {
		return this.rechargeMsgs;
	}

	public void setRechargeMsgs(Integer rechargeMsgs) {
		this.rechargeMsgs = rechargeMsgs;
	}

	@Column(name = "auto_check_out")
	public Integer getAutoCheckOut() {
		return this.autoCheckOut;
	}

	public void setAutoCheckOut(Integer autoCheckOut) {
		this.autoCheckOut = autoCheckOut;
	}
}