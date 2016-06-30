package com.project.entity.common;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.project.entity.IdEntity;

/**
 * CbsSysDictionaryId entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "TOMATO_SYS_DICTIONARY")
//默认的缓存策略.
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysDictionary extends IdEntity{

	private static final long serialVersionUID = 1L;
	
	private String contyp;
	private String connam;
	private String conval;
	private String condes;
	private Integer conseq;
	private String status;
	private String createUserCode;
	private Date createTime;
	private String updateUserCode;
	private Date updateTime;

	public String getConnam() {
		return this.connam;
	}

	public void setConnam(String connam) {
		this.connam = connam;
	}

	public String getCondes() {
		return this.condes;
	}

	public void setCondes(String condes) {
		this.condes = condes;
	}

	public String getContyp() {
		return this.contyp;
	}

	public void setContyp(String contyp) {
		this.contyp = contyp;
	}

	public String getConval() {
		return this.conval;
	}

	public void setConval(String conval) {
		this.conval = conval;
	}

	public void setConseq(Integer conseq) {
		this.conseq = conseq;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getConseq() {
		return conseq;
	}

	public String getUpdateUserCode() {
		return updateUserCode;
	}

	public void setUpdateUserCode(String updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(String createUserCode) {
		this.createUserCode = createUserCode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}