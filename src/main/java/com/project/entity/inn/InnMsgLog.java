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
 * 短信记录
 * @author X
 *
 */
@Entity
@Table(name = "tomato_msg_log")
public class InnMsgLog implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_msg_log_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	/** 接收/回复手机号 */
	@NotBlank(message="接收手机号必须填写")
	@Column(length = 11)
	private String mobile;
	
	/** 短信标题 */
	@NotBlank(message="短信标题 必须填写")
	@Column(length = 50)
	private String title;
	
	/** 短信内容 */
	@NotBlank(message="短信内容必须填写")
	@Column(length = 191)
	private String content;
	
	/** 状态 是否发送成功。0(全部使用赠送短信)，1(全部使用充值短信), 2(同时使用了赠送与充值短信的余额) */
	private Integer status;
	
	/** 消耗的充值短信数量 */
	private Integer paidNum;
	
	/** 订单类型。 2（预订） 3（入住） 4(退房) */
	private Integer orderType;
	
	/** 发送标示。1(上行)，2(下行) */
	private Integer flag;

	/** 发送人账号 */
//	@JsonIgnore
	private String sendUserCode;
	
	/** 发送类型  1：单发2：群发3：自动*/
	private Integer sendType;
	
	/** 发送条数  */
	private Integer sendNum;
	
	/** 剩余短信记录条数  */
	private Integer balance;
	
	/** 发送时间 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendTime;
	
	/** 所属房态订单id */
	private Integer statusId;
	
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



	public String getMobile() {
		return mobile;
	}



	public void setMobile(String mobile) {
		this.mobile = mobile;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	public Integer getStatus() {
		return status;
	}



	public void setStatus(Integer status) {
		this.status = status;
	}



	public Integer getPaidNum() {
		return paidNum;
	}



	public void setPaidNum(Integer paidNum) {
		this.paidNum = paidNum;
	}



	public Integer getOrderType() {
		return orderType;
	}



	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}



	public Integer getFlag() {
		return flag;
	}



	public void setFlag(Integer flag) {
		this.flag = flag;
	}



	public String getSendUserCode() {
		return sendUserCode;
	}



	public void setSendUserCode(String sendUserCode) {
		this.sendUserCode = sendUserCode;
	}



	public Integer getSendType() {
		return sendType;
	}



	public void setSendType(Integer sendType) {
		this.sendType = sendType;
	}



	public Integer getSendNum() {
		return sendNum;
	}



	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}



	public Integer getBalance() {
		return balance;
	}



	public void setBalance(Integer balance) {
		this.balance = balance;
	}



	public Date getSendTime() {
		return sendTime;
	}



	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}



	public Integer getStatusId() {
		return statusId;
	}



	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
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
