package com.project.entity.api;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;





import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.collect.Lists;

/**
 * 渠道主订单
 * 
 * @author mowei
 * 
 */
@Entity
@Table(name = "tomato_channel_main_order")
public class ChannelMainOrder implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @SequenceGenerator(name = "seqGenerator", sequenceName = "seq_tomato_channel_main_order_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	/** 预定人姓名 */
	private String userName;

	/** 联系方式 */
	private String contact;
	
	/** 渠道来源标示  */
	private Integer channelId;
	
	private Integer fxChannelId;
	
	/** 渠道价格策略(1:底价 2:卖价) */
	private String channelPricePolicy;

	/** 上浮比例 */
	private Integer channelUpRatio;

	/** 分佣比例 */
	private Integer channelCommissionRatio;

	/** 客栈价格策略(1:底价 2:卖价 3:底价+卖价) */
	private String innPricePolicy;

	/** 卖价时抽佣比例 */
	private Integer innCommissionRatio;

	/** 客栈id */
	private Integer innId;

	/** 渠道订单编号 **/
	private String channelOrderNo;

	/** 支付时间 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date payTime;
	
	private Date orderTime;

	/** 是否结算（0:未结算,1:已结算）  */
	private String isBalance;
	
	/** 结算时间 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date balanceTime;
	
	/** 渠道子订单 */
	@OneToMany(cascade = { CascadeType.ALL })
	@JoinColumn(name = "main_id", referencedColumnName = "id")
	@JsonManagedReference
	private List<ChannelOrder> channelOrders = Lists.newArrayList();
	
	@Transient
	private String otaName;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public String getChannelPricePolicy() {
		return channelPricePolicy;
	}

	public void setChannelPricePolicy(String channelPricePolicy) {
		this.channelPricePolicy = channelPricePolicy;
	}

	public String getInnPricePolicy() {
		return innPricePolicy;
	}

	public void setInnPricePolicy(String innPricePolicy) {
		this.innPricePolicy = innPricePolicy;
	}

	public Integer getInnId() {
		return innId;
	}

	public void setInnId(Integer innId) {
		this.innId = innId;
	}

	public String getChannelOrderNo() {
		return channelOrderNo;
	}

	public void setChannelOrderNo(String channelOrderNo) {
		this.channelOrderNo = channelOrderNo;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getIsBalance() {
		return isBalance;
	}

	public void setIsBalance(String isBalance) {
		this.isBalance = isBalance;
	}
	
	public Date getBalanceTime() {
		return balanceTime;
	}

	public void setBalanceTime(Date balanceTime) {
		this.balanceTime = balanceTime;
	}

	public List<ChannelOrder> getChannelOrders() {
		return channelOrders;
	}

	public void setChannelOrders(List<ChannelOrder> channelOrders) {
		this.channelOrders = channelOrders;
	}

	public Integer getChannelUpRatio() {
		return channelUpRatio;
	}

	public void setChannelUpRatio(Integer channelUpRatio) {
		this.channelUpRatio = channelUpRatio;
	}

	public Integer getChannelCommissionRatio() {
		return channelCommissionRatio;
	}

	public void setChannelCommissionRatio(Integer channelCommissionRatio) {
		this.channelCommissionRatio = channelCommissionRatio;
	}

	public Integer getInnCommissionRatio() {
		return innCommissionRatio;
	}

	public void setInnCommissionRatio(Integer innCommissionRatio) {
		this.innCommissionRatio = innCommissionRatio;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public String getOtaName() {
		return otaName;
	}

	public void setOtaName(String otaName) {
		this.otaName = otaName;
	}

	public Integer getFxChannelId() {
		return fxChannelId;
	}

	public void setFxChannelId(Integer fxChannelId) {
		this.fxChannelId = fxChannelId;
	}
	
	
}
