package com.project.entity.api;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;


@Entity
@Table(name = "tomato_api_channel")
public class ApiChannel implements Serializable{
	
	
	private static final long serialVersionUID = 1L;


	@Id
    @SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_api_channel")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
    private Integer id;
	
	private String name;
	
	private String userCode;
	
	private String userPasswd;
	
	private String authority;
	
	private String pricePolicy;
	
	private Integer upRatio;
	
	private Integer commissionRatio;
	
	private Integer apiId;
	
	private Integer otaId;
	
	private Date createdAt;
	
	private String createdUser;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserPasswd() {
		return userPasswd;
	}

	public void setUserPasswd(String userPasswd) {
		this.userPasswd = userPasswd;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getPricePolicy() {
		return pricePolicy;
	}

	public void setPricePolicy(String pricePolicy) {
		this.pricePolicy = pricePolicy;
	}

	public Integer getUpRatio() {
		return upRatio;
	}

	public void setUpRatio(Integer upRatio) {
		this.upRatio = upRatio;
	}

	public Integer getCommissionRatio() {
		return commissionRatio;
	}

	public void setCommissionRatio(Integer commissionRatio) {
		this.commissionRatio = commissionRatio;
	}

	public Integer getApiId() {
		return apiId;
	}

	public void setApiId(Integer apiId) {
		this.apiId = apiId;
	}

	public Integer getOtaId() {
		return otaId;
	}

	public void setOtaId(Integer otaId) {
		this.otaId = otaId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
