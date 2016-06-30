package com.project.entity.account;

import java.util.Date;

import javax.persistence.*;

import com.project.enumeration.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.project.entity.IdEntity;

/**
 * 权限.
 * 
 * 注释见{@link User}.
 * 
 * @author mowei
 */
@Entity
@Table(name = "TOMATO_SYS_AUTHORITY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority extends IdEntity{

	private static final long serialVersionUID = 1L;
	/**
	 * SpringSecurity中默认的角色/授权名前缀.
	 */
	public static final String AUTHORITY_PREFIX = "ROLE_";
	
	private String sysAuthorityCode;
	private String sysAuthorityName;
	private Status status=Status.ENABLED;
	private String rmk;
	private String createUserCode;		//创建人
	private Date createTime;			//创建时间
	private String updateUserCode;		//修改人
	private Date updateTime;			//修改时间

	public Authority() {
	}

	public Authority(Long id, String sysAuthorityName) {
		this.id = id;
		this.sysAuthorityName = sysAuthorityName;
	}

	public String getSysAuthorityCode() {
		return sysAuthorityCode;
	}

	public void setSysAuthorityCode(String sysAuthorityCode) {
		this.sysAuthorityCode = sysAuthorityCode;
	}

	@Column(nullable = false, unique = true)
	public String getSysAuthorityName() {
		return sysAuthorityName;
	}

	public void setSysAuthorityName(String sysAuthorityName) {
		this.sysAuthorityName = sysAuthorityName;
	}

	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getRmk() {
		return rmk;
	}

	public void setRmk(String rmk) {
		this.rmk = rmk;
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

	@Transient
	public String getPrefixedName() {
		return AUTHORITY_PREFIX + sysAuthorityName;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
