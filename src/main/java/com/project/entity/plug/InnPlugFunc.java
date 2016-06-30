package com.project.entity.plug;

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
 * 插件下属功能
 * @author X
 *
 */
@Entity
@Table(name = "tomato_plug_func")
public class InnPlugFunc implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_plug_func_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	/** 功能code */
	@NotBlank(message="功能代码必须填写")
	@Column(length = 32)
	private String funcCode;
	
	/** 功能名称 */
	@NotBlank(message="功能名称必须填写")
	@Column(length = 32)
	private String funcName;
	
	/** 状态 */
	private Integer status;

	/** 创建人 */
	private String createUserCode;
	
	/** 创建时间 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	/** 修改人 */
	private String updateUserCode;
	
	/** 修改时间 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;
	
	/** 备注 */
	private String rmk;
	
	/** 所属插件 */
	@ManyToOne
	@JoinColumn(name = "plug_id")
	private InnPlug innPlug;
	
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public String getFuncCode() {
		return funcCode;
	}



	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}



	public String getFuncName() {
		return funcName;
	}



	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}



	public Integer getStatus() {
		return status;
	}



	public void setStatus(Integer status) {
		this.status = status;
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



	public String getRmk() {
		return rmk;
	}



	public void setRmk(String rmk) {
		this.rmk = rmk;
	}



	public InnPlug getInnPlug() {
		return innPlug;
	}



	public void setInnPlug(InnPlug innPlug) {
		this.innPlug = innPlug;
	}



	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
