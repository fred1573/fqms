package com.project.entity.inn;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.bean.serializer.JsonDateTimeSerializer;

/**
 * 客栈-功能使用统计
 * 
 * @author X
 * 
 */
@Entity
@Table(name = "tomato_sys_func_report")
public class InnFuncReport implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_sys_func_report_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;
	
	/** 使用量*/
	private Integer applicationAmount;
	
	/** 使用率*/
	private Double innPercent;
	
	/** 使用功能编号*/
	private Integer funcItemType;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	/** 记录时间*/
	private Date recordedAt;
	
	
	
	

	public Integer getId() {
		return id;
	}





	public void setId(Integer id) {
		this.id = id;
	}





	public Integer getApplicationAmount() {
		return applicationAmount;
	}





	public void setApplicationAmount(Integer applicationAmount) {
		this.applicationAmount = applicationAmount;
	}





	public Double getInnPercent() {
		return innPercent;
	}





	public void setInnPercent(Double innPercent) {
		this.innPercent = innPercent;
	}





	public Integer getFuncItemType() {
		return funcItemType;
	}





	public void setFuncItemType(Integer funcItemType) {
		this.funcItemType = funcItemType;
	}





	public Date getRecordedAt() {
		return recordedAt;
	}





	public void setRecordedAt(Date recordedAt) {
		this.recordedAt = recordedAt;
	}
	


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
