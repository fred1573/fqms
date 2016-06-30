package com.project.entity.inn;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.bean.serializer.JsonDateTimeSerializer;

/**
 * 客栈-日志
 * 
 * @author momo
 * 
 */
@Entity
@Table(name = "tomato_inn_log")
public class InnLog implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_inn_log_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;
	
	private String content;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	private Date operateTime;
	
	private String userName;
	
	@JsonIgnore
	private Integer userType;
	
	@JsonIgnore
	private Integer logType;
	
	@JsonIgnore
	private Integer logFrom;
	
	@ManyToOne(cascade={CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinColumn(name = "inn_id", updatable = false, insertable = false)
	@JsonIgnore
	private Inn inn;
	
	@JsonIgnore
	private Integer logTypeId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getLogType() {
		return logType;
	}

	public void setLogType(Integer logType) {
		this.logType = logType;
	}

	public Integer getLogFrom() {
		return logFrom;
	}

	public void setLogFrom(Integer logFrom) {
		this.logFrom = logFrom;
	}

	public Inn getInn() {
		return inn;
	}

	public void setInn(Inn inn) {
		this.inn = inn;
	}

	public Integer getLogTypeId() {
		return logTypeId;
	}

	public void setLogTypeId(Integer logTypeId) {
		this.logTypeId = logTypeId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
