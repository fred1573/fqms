package com.project.entity.inn;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;


/**
 * 客栈-顾客来源
 * 
 * @author X
 * 
 */
@Entity
@Table(name = "tomato_inn_customer_from")
public class InnCustomerFrom implements Serializable{
	
	private static final String DEFAULT_COLOR_CLASS = "c_touming";
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_inn_customer_from_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	/** 名称 */
	@NotBlank(message="客人来源名称必须填写")
	@Column(length = 32)
	private String name;
	
	/** 自定义颜色样式值 */
	private String color = DEFAULT_COLOR_CLASS;

	/** 创建时间 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	/** 修改时间 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	/** 创建人 */
	private String createdUser;
	
	/** 修改人 */
	private String updatedUser;

	/** 所属客栈 */
	@ManyToOne
	@JoinColumn(name = "inn_id")
	private Inn inn;
	
	/**
	 * 所属otaID
	 */
	private Integer otaId;
	
	/** 排序 */
	@JoinColumn(name = "serial_no")
	private Integer serialNo;
	
	
	
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



	public String getColor() {
		return color;
	}



	public void setColor(String color) {
		this.color = color;
	}



	public Date getCreatedAt() {
		return createdAt;
	}



	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}



	public Date getUpdatedAt() {
		return updatedAt;
	}



	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}



	public String getCreatedUser() {
		return createdUser;
	}



	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}



	public String getUpdatedUser() {
		return updatedUser;
	}



	public void setUpdatedUser(String updatedUser) {
		this.updatedUser = updatedUser;
	}



	public Inn getInn() {
		return inn;
	}



	public void setInn(Inn inn) {
		this.inn = inn;
	}



	public Integer getOtaId() {
		return otaId;
	}



	public void setOtaId(Integer otaId) {
		this.otaId = otaId;
	}



	public Integer getSerialNo() {
		return serialNo;
	}



	public void setSerialNo(Integer serialNo) {
		this.serialNo = serialNo;
	}



	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
