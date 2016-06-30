package com.project.entity.plug;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;


import com.google.common.collect.Lists;

/**
 * 插件
 * @author X
 *
 */
@Entity
@Table(name = "tomato_plug")
public class InnPlug implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_plug_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	/** 插件code */
	@NotBlank(message="插件代码必须填写")
	@Column(length = 32)
	private String plugCode;
	
	/** 插件名称 */
	@NotBlank(message="插件名称必须填写")
	@Column(length = 32)
	private String plugName;
	
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
	
	/** 插件下属功能列表  */
	@OneToMany(cascade={CascadeType.ALL})
	@JoinColumn(name="plug_id", referencedColumnName = "id")
	private List<InnPlugFunc> innPlugFuncs = Lists.newArrayList();
	
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public String getPlugCode() {
		return plugCode;
	}



	public void setPlugCode(String plugCode) {
		this.plugCode = plugCode;
	}



	public String getPlugName() {
		return plugName;
	}



	public void setPlugName(String plugName) {
		this.plugName = plugName;
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



	public List<InnPlugFunc> getInnPlugFuncs() {
		return innPlugFuncs;
	}



	public void setInnPlugFuncs(List<InnPlugFunc> innPlugFuncs) {
		this.innPlugFuncs = innPlugFuncs;
	}



	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
