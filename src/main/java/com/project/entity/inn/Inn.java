package com.project.entity.inn;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tomato_inn")
public class Inn implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @SequenceGenerator(name = "seqGenerator", sequenceName = "seq_inn_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
    private Integer id;

    /**
     * 客栈名称
     */
    @Column(length = 64)
    @NotBlank(message = "客栈名称必填")
    private String name;
    
    /**
     * 客栈地址
     */
    @Column(length = 128)
    @JsonIgnore
    private String address;
    
    /**
     * 客栈审核人
     */
    private String auditor;

    /**
     * 客栈联系方式
     */
    @Column(length = 32)
    @Length(min = 11, max = 11, message = "请输入正确的手机号码")
    @Pattern(regexp = "^(13|14|15|18)[0-9]{9}$", message = "请输入正确的手机号码")
    private String contact;

    /**
     * 客栈应用ID,调用服务接口加密用
     */
    @Column(length = 32)
    private String appId;

    /**
     * 客栈应用秘匙,调用服务接口加密用
     */
    @Column(length = 64)
    private String appKey;
    
    /**
     * 是否加入代销平台, '0'：否   '1'：是
     */
    private String inMarket;
    
    /**
     * 加入代销平台的时间
     */
    private Date joinMarketTime;
    
    /**
     * 价格策略，'1':底价,'2'：卖价,'3':底价+卖价
     */
    private String pricePolicy;
    
    
    /**
     * 卖价分佣比例
     */
    private Integer totalCommissionRatio;
    
    
    /**
     * 代销平台加入操作人
     */
    private String inMarketCreatedUser;
    
    /**
     * 客栈账号 (非数据库字段)
     */
    @Transient
    private String mobile;
    
    /**
     * 可供第三方渠道销售的房间数量 (非数据库字段)
     */
    @Transient
    private Integer marketRooms;

    /**
     * 客栈注册时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date registeredAt;

    /**
     * 客栈更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date updatedAt = new Date();

    /**
     * 客栈所属地区
     */
    @ManyToOne(cascade={CascadeType.REFRESH})
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    private InnRegion region;
    
    /** 支付宝账号 */
    private String alipayCode;
	
	/** 支付宝收款人 */
    private String alipayUser;
	
	/** 财付通账号 */
    private String tenpayCode;
	
	/** 财付通收款人 */
    private String tenpayUser;

	/**是否安装水牌**/
	private Boolean hasBrand = false;
	
	/*** 1:个人账户/2:公司账户 */
	private Integer bankType;

    /*** 账户户名 */
	private String bankAccount;

    /*** 账户号码 */
	private String bankCode;

    /*** 开户银行 */
	private String bankName;

    /*** 开户地区 */
	private String bankRegion;
	
	/*** 开户银行所在省 */
	private String bankProvince;
	
	/*** 开户银行所在市 */
	private String bankCity;
	
	/*** 结算信息确认状态  0:未确认 1:已确认 */
	private Integer confirm;

    public String getBankProvince() {
		return bankProvince;
	}

	public void setBankProvince(String bankProvince) {
		this.bankProvince = bankProvince;
	}

	public String getBankCity() {
		return bankCity;
	}

	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}

	public Integer getConfirm() {
		return confirm;
	}

	public void setConfirm(Integer confirm) {
		this.confirm = confirm;
	}

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public Date getRegisteredAt() {
		return registeredAt;
	}

	public void setRegisteredAt(Date registeredAt) {
		this.registeredAt = registeredAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public InnRegion getRegion() {
		return region;
	}

	public void setRegion(InnRegion region) {
		this.region = region;
	}
	
	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
	
	public String getInMarket() {
		return inMarket;
	}

	public void setInMarket(String inMarket) {
		this.inMarket = inMarket;
	}

	public Date getJoinMarketTime() {
		return joinMarketTime;
	}

	public void setJoinMarketTime(Date joinMarketTime) {
		this.joinMarketTime = joinMarketTime;
	}

	public String getPricePolicy() {
		return pricePolicy;
	}

	public void setPricePolicy(String pricePolicy) {
		this.pricePolicy = pricePolicy;
	}

	public Integer getTotalCommissionRatio() {
		return totalCommissionRatio;
	}

	public void setTotalCommissionRatio(Integer totalCommissionRatio) {
		this.totalCommissionRatio = totalCommissionRatio;
	}

	public String getInMarketCreatedUser() {
		return inMarketCreatedUser;
	}

	public void setInMarketCreatedUser(String inMarketCreatedUser) {
		this.inMarketCreatedUser = inMarketCreatedUser;
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public Integer getMarketRooms() {
		return marketRooms;
	}

	public void setMarketRooms(Integer marketRooms) {
		this.marketRooms = marketRooms;
	}

	public String getAlipayCode() {
		return alipayCode;
	}

	public void setAlipayCode(String alipayCode) {
		this.alipayCode = alipayCode;
	}

	public String getAlipayUser() {
		return alipayUser;
	}

	public void setAlipayUser(String alipayUser) {
		this.alipayUser = alipayUser;
	}

	public String getTenpayCode() {
		return tenpayCode;
	}

	public void setTenpayCode(String tenpayCode) {
		this.tenpayCode = tenpayCode;
	}

	public String getTenpayUser() {
		return tenpayUser;
	}

	public void setTenpayUser(String tenpayUser) {
		this.tenpayUser = tenpayUser;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

	public Boolean getHasBrand() {
		return hasBrand;
	}

	public void setHasBrand(Boolean hasBrand) {
		this.hasBrand = hasBrand;
	}

	public Integer getBankType() {
		return bankType;
	}

	public void setBankType(Integer bankType) {
		this.bankType = bankType;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankRegion() {
		return bankRegion;
	}

	public void setBankRegion(String bankRegion) {
		this.bankRegion = bankRegion;
	}
	
}