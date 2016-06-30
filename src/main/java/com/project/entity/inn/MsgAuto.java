package com.project.entity.inn;

import java.io.Serializable;
import java.util.Date;

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


/**
 * 智能发送短信
 * @author X
 *
 */
@Entity
@Table(name = "tomato_msg_auto")
public class MsgAuto implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_msg_auto_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;
	
	/** 操作类别  2:已预定; 3:已入住; 4:已退房 */
	private Integer operateType;
	
	/** 所属短信模板 */
	@ManyToOne
	@JoinColumn(name = "msg_template_id")
	private InnMsgTemplate innMsgTemplate;
	
//	@ManyToOne
//	@JoinColumn(name = "customer_from_id")
//	public InnCustomerFrom customerFrom;
	
	/** 所属客栈 */
	@ManyToOne
	@JoinColumn(name = "inn_id")
	private Inn inn;

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
	
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public Integer getOperateType() {
		return operateType;
	}



	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}



	public InnMsgTemplate getInnMsgTemplate() {
		return innMsgTemplate;
	}



	public void setInnMsgTemplate(InnMsgTemplate innMsgTemplate) {
		this.innMsgTemplate = innMsgTemplate;
	}



	public Inn getInn() {
		return inn;
	}



	public void setInn(Inn inn) {
		this.inn = inn;
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



	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
