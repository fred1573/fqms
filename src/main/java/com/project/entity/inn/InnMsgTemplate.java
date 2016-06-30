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
 * 短信模板
 * @author X
 *
 */
@Entity
@Table(name = "tomato_msg_template")
public class InnMsgTemplate implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_msg_template_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	/** 短信标题 */
	@NotBlank(message="短信标题必须填写")
	@Column(length = 50)
	private String msgTitle;
	
	/** 短信内容 */
	@NotBlank(message="短信内容必须填写")
	@Column(length = 191)
	private String msgContent;
	
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
	
	/** 所属客栈 */
	@ManyToOne
	@JoinColumn(name = "inn_id")
	private Inn inn;
	
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public String getMsgTitle() {
		return msgTitle;
	}



	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}



	public String getMsgContent() {
		return msgContent;
	}



	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
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



	public Inn getInn() {
		return inn;
	}



	public void setInn(Inn inn) {
		this.inn = inn;
	}



	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
