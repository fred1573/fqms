package com.project.entity.log;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.project.entity.IdEntity;

/**
 * CbsSystemLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "TOMATO_SYSTEM_LOG")
//默认的缓存策略.
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SystemLog extends IdEntity{

	private static final long serialVersionUID = 1L;
	
	private String content;
	private Date operateTime;
	private String userCode;
	private String userType;//操作人类型（0：会员，1：代理商 ，2：系统用户）
	private String logType;
	
	public SystemLog(){
		
	}
	
	public SystemLog(String content, Date operateTime, String userCode,
			String userType, String logType) {
		super();
		this.content = content;
		this.operateTime = operateTime;
		this.userCode = userCode;
		this.userType = userType;
		this.logType = logType;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getOperateTime() {
		return this.operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getUserCode() {
		return this.userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	@Column(name = "USER_TYPE", length = 1)
	public String getUserType() {
		return this.userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}