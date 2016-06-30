package com.project.entity.proxysale;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.entity.account.User;

import static com.project.entity.proxysale.PriceStrategy.STRATEGY_BASE_PRICE;
import static com.project.entity.proxysale.PriceStrategy.STRATEGY_SALE_BASE_PRICE;
import static com.project.entity.proxysale.PriceStrategy.STRATEGY_SALE_PRICE;

@Entity
@Table(name="tomato_proxysale_channel_inn")
public class ProxysaleChannel {
	
	@Id
	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private  Integer  id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "proxy_inn")
	private ProxyInn  proxyInn; //  客栈
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "channel")
	private  Channel channel; //  渠道

	private  Short strategy;
	
	private Date createTime;
	
	private Boolean valid = true;
	

	  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	  @JoinColumn(name = "operator")
	  private User operator;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ProxyInn getProxyInn() {
		return proxyInn;
	}

	public void setProxyInn(ProxyInn proxyInn) {
		this.proxyInn = proxyInn;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Short getStrategy() {
		return strategy;
	}

	public void setStrategy(Short strategy) {
		this.strategy = strategy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}


	/**
	 * 策略转换到模式
	 * @return 模式
	 * @throws RuntimeException 没有匹配的模式时抛出异常
     */
	public Short strategy2Pattern() throws RuntimeException {
		if (STRATEGY_BASE_PRICE.equals(this.strategy)) {
			return PricePattern.PATTERN_BASE_PRICE;
		} else if (STRATEGY_SALE_PRICE.equals(this.strategy)|| STRATEGY_SALE_BASE_PRICE.equals(this.strategy)) {
			return PricePattern.PATTERN_SALE_PRICE;
		} else {
			throw new RuntimeException("策略异常, strategy=" + this.strategy);
		}
	}

}
