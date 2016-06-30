package com.project.entity.wg;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


import com.google.common.collect.Lists;
import com.project.bean.serializer.JsonDateSerializer;

/**
 * wg账号
 * 
 * @author momo
 * 
 */
@Entity
@Table(name = "wg_account")
public class WgAccount implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @SequenceGenerator(name = "seqGenerator", sequenceName = "seq_wg_account_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	private Integer innId;

	@ManyToOne
	@JoinColumn(name = "ota_id")
	private WgOtaInfo wgOtaInfo = new WgOtaInfo();

	private Integer hotelId;

	private String hotelName;

	private String name;

	@JsonIgnore
	private String password;

	@JsonIgnore
	private String mobile;

	@JsonIgnore
	private String email;

	@JsonIgnore
	public String qq;

	@JsonIgnore
	private String status;

	private String cookies;

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonIgnore
	private Date createdAt;

	@JsonIgnore
	private String lastLoginedIp;

	@JsonIgnore
	private Date lastLoginedAt;

	@JsonIgnore
	private String rmk;

	// /** 所关联客栈以及渠道的账户 */
	// @OneToMany(cascade={CascadeType.ALL})
	// @JoinColumn(name="account_id", referencedColumnName = "id")
	// @JsonIgnore
	// public List<WgInnOtaAccount> wgInnOtaAccounts = Lists.newArrayList();

	/** 所关联房型 */
	@OneToMany(cascade = { CascadeType.ALL })
	@JoinColumn(name = "account_id", referencedColumnName = "id")
	@OrderBy("id")
	@JsonIgnore
	private List<WgRoomType> wgRoomTypes = Lists.newArrayList();

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getInnId() {
		return innId;
	}

	public void setInnId(Integer innId) {
		this.innId = innId;
	}

	public WgOtaInfo getWgOtaInfo() {
		return wgOtaInfo;
	}

	public void setWgOtaInfo(WgOtaInfo wgOtaInfo) {
		this.wgOtaInfo = wgOtaInfo;
	}

	public Integer getHotelId() {
		return hotelId;
	}

	public void setHotelId(Integer hotelId) {
		this.hotelId = hotelId;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getLastLoginedIp() {
		return lastLoginedIp;
	}

	public void setLastLoginedIp(String lastLoginedIp) {
		this.lastLoginedIp = lastLoginedIp;
	}

	public Date getLastLoginedAt() {
		return lastLoginedAt;
	}

	public void setLastLoginedAt(Date lastLoginedAt) {
		this.lastLoginedAt = lastLoginedAt;
	}

	public String getRmk() {
		return rmk;
	}

	public void setRmk(String rmk) {
		this.rmk = rmk;
	}

	public List<WgRoomType> getWgRoomTypes() {
		return wgRoomTypes;
	}

	public void setWgRoomTypes(List<WgRoomType> wgRoomTypes) {
		this.wgRoomTypes = wgRoomTypes;
	}
	
}
