package com.project.entity.proxysale;

import com.project.entity.account.User;
import com.project.entity.area.Area;
import com.project.entity.inn.InnRegion;
import com.project.entity.operation.OperationActivity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 代销客栈信息
 * Created by Administrator on 2015/6/9.
 */
@Entity
@Table(name = "tomato_proxysale_inn")
public class ProxyInn {

    public static final Integer PRICE_PATTERN_SIZE = 2;
    public static final Integer STATUS_ONSHELF = 3;//已上架（底价上架、卖价上架）
    public static final Integer STATUS_BASE_OFFSHELF = 1;//已底价下架（底价下架、卖价上架）
    public static final Integer STATUS_SALE_OFFSHELF = 2;//已卖价下架（底价上架、卖价下架）
    public static final Integer STATUS_OFFSHELF = 0;//已下架（底价下架、卖价下架）

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="inn")
    private Integer inn;

    /*
      开通时间
     */
    @Column(name="create_time")
    private Date createTime;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proxyInn", fetch = FetchType.LAZY)
    private Set<PricePattern> pricePatterns = new HashSet<>(PRICE_PATTERN_SIZE);

    /*
    可售房间数量
     */
    transient
    private Integer availableRoomNum;

    /*
    OTA链接地址
     */
    @Column(name = "ota_link")
    private String otaLink;

    /*
    老板电话，用于客栈状态修改后短信通知
     */
    private String phone;

    /*
    最后一次编辑时间
     */
    @Column(name = "edit_time")
    private Date editTime;

    /*
    最后一次底价上/下架时间
     */
    @Column(name = "base_price_onoff_time")
    private Date basePriceOnOffTime;

    /*
    最后一次卖价上/下架时间
     */
    @Column(name = "sale_price_onoff_time")
    private Date salePriceOnOffTime;

    /*
      状态：
      1-已上架
      2-已底价下架
      3-已卖价下架
      4-已下架
     */
    @Column
    private Integer status;

    /*
    false:视作被删除，客栈代销数据不能再使用，仅供数据留底
    true:数据可使用
     */
    @Column
    private boolean valid = true;

    /*
    最后一次编辑人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edit_operator")
    private User editOperator;

    /*
    最后一次上下架操作人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onoff_operator")
    private User onOffOperator;

    /*
    驳回原因
     */
    @Column(name = "failed_reason")
    private String failedReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area")
    private Area area;
    
    @OneToMany(cascade = CascadeType.ALL,mappedBy="proxyInn",fetch = FetchType.LAZY)
    private Set<ProxysaleChannel>  pcs;

    @ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "region")
    private InnRegion innRegion;

	public Set<ProxysaleChannel> getPcs() {
		return pcs;
	}

	public void setPcs(Set<ProxysaleChannel> pcs) {
		this.pcs = pcs;
	}

	@Column(name = "inn_name")
    private String innName;

    @Column(name = "inn_addr")
    private String innAddr;
    //代销卖价accountId
    @Transient
    private Integer accountId;

    @ManyToMany(mappedBy = "proxyInns")
    private Set<OperationActivity> OperationActivities = new HashSet<>();

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public ProxyInn() {
        this.pricePatterns.add(new PricePattern(this, null, PricePattern.PATTERN_BASE_PRICE, false));
        this.pricePatterns.add(new PricePattern(this, 0f, PricePattern.PATTERN_SALE_PRICE, false));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInn() {
        return inn;
    }

    public void setInn(Integer inn) {
        this.inn = inn;
    }

    public Set<PricePattern> getPricePatterns() {
        return pricePatterns;
    }

    /**
     * 返回有效的价格模式
     * @return
     */
    public Set<PricePattern> getValidPatterns(){
        Set<PricePattern> result = new HashSet<>();
        for (PricePattern pattern : pricePatterns) {
            if(pattern.isValid()){
                result.add(pattern);
            }
        }
        return result;
    }

    public void setPricePatterns(Set<PricePattern> pricePatterns) {
        this.pricePatterns = pricePatterns;
    }

    public Integer getAvailableRoomNum() {
        return availableRoomNum;
    }

    public void setAvailableRoomNum(Integer availableRoomNum) {
        this.availableRoomNum = availableRoomNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Date getBasePriceOnOffTime() {
        return basePriceOnOffTime;
    }

    public void setBasePriceOnOffTime(Date basePriceOnOffTime) {
        this.basePriceOnOffTime = basePriceOnOffTime;
    }

    public Date getSalePriceOnOffTime() {
        return salePriceOnOffTime;
    }

    public void setSalePriceOnOffTime(Date salePriceOnOffTime) {
        this.salePriceOnOffTime = salePriceOnOffTime;
    }

    public User getEditOperator() {
        return editOperator;
    }

    public void setEditOperator(User editOperator) {
        this.editOperator = editOperator;
    }

    public User getOnOffOperator() {
        return onOffOperator;
    }

    public void setOnOffOperator(User onOffOperator) {
        this.onOffOperator = onOffOperator;
    }

    public boolean isBasePriceOnshelfed(){
        return Integer.parseInt(Integer.toBinaryString(this.status & 3)) >= 10;
    }

    public boolean isSalePriceOnshelfed(){
        return Integer.parseInt(Integer.toBinaryString(this.status & 3)) % 2 != 0;
    }

    public boolean isSalePriceValid() {
        return isPricePatternValid(PricePattern.PATTERN_SALE_PRICE);
    }

    public boolean isBasePriceValid(){
        return isPricePatternValid(PricePattern.PATTERN_BASE_PRICE);
    }

    private boolean isPricePatternValid(Short pricePattern){
        for (PricePattern pattern : pricePatterns) {
            if(pattern.getPattern().shortValue() == pricePattern.shortValue()
                    && pattern.isValid()){
                return true;
            }
        }
        return false;
    }

    public void setSalePricePatternPercentage(Float percentage) {
        if(percentage != null){
            float scaledValue = new BigDecimal(percentage).setScale(2, RoundingMode.DOWN).floatValue();
            if(scaledValue < 0f || scaledValue > 100f){
                throw new RuntimeException("百分比例应界于0-100，且最多两位小数");
            }
            for (PricePattern pattern : pricePatterns) {
                if(pattern.getPattern().shortValue() == PricePattern.PATTERN_SALE_PRICE.shortValue()
                        && pattern.isValid()){
                    pattern.setPercentage(scaledValue);
                    return;
                }
            }
        }
    }

    /**
     * 页面显示时间：max{开通时间,编辑时间}
     */
    public Date getViewTime(){
        Date createTime = this.getCreateTime();
        Date editTime = this.getEditTime();
        if(editTime == null){
            return createTime;
        }
        return createTime.after(editTime) ? createTime : editTime;
    }

    public Float getSalePercentage(){
        for (PricePattern pattern : pricePatterns) {
            if(pattern.isValid() && pattern.getPattern().shortValue() == PricePattern.PATTERN_SALE_PRICE){
                return pattern.getPercentage();
            }
        }
        return null;
    }

    public void makeBasePricePatternValid(Integer outerId) {
        if(isBasePriceValid()){
           return;
        }
        for (PricePattern pricePattern : pricePatterns) {
            if(pricePattern.getPattern().shortValue() == PricePattern.PATTERN_BASE_PRICE.shortValue()){
                pricePattern.setValid(true);
                pricePattern.setOuterId(outerId);
            }
        }
    }

    public void makeSalePricePatternValid(Integer outerId) {
        if(isSalePriceValid()){
            return;
        }
        for (PricePattern pricePattern : pricePatterns) {
            if(pricePattern.getPattern().shortValue() == PricePattern.PATTERN_SALE_PRICE.shortValue()){
                pricePattern.setValid(true);
                pricePattern.setPercentage(PricePattern.DEFAULT_SALE_PERCENTAGE);
                pricePattern.setOuterId(outerId);
            }
        }
    }

    public Date getLastOnoffTime(){
        if(this.basePriceOnOffTime == null
                && this.salePriceOnOffTime == null){
            return null;
        }else if(this.basePriceOnOffTime != null
                && this.salePriceOnOffTime == null){
            return this.basePriceOnOffTime;
        }else if(this.basePriceOnOffTime == null
                && this.salePriceOnOffTime != null){
            return this.salePriceOnOffTime;
        }else{
            return basePriceOnOffTime.after(salePriceOnOffTime) ? basePriceOnOffTime : salePriceOnOffTime;
        }
    }

    public Integer getBaseOuterId(){
        for (PricePattern pricePattern : pricePatterns) {
            if(pricePattern.isValid() && pricePattern.getPattern().equals(PricePattern.PATTERN_BASE_PRICE)){
                return pricePattern.getOuterId();
            }
        }
        return null;
    }

    public Integer getSaleOuterId(){
        for (PricePattern pricePattern : pricePatterns) {
            if(pricePattern.isValid() && pricePattern.getPattern().equals(PricePattern.PATTERN_SALE_PRICE)){
                return pricePattern.getOuterId();
            }
        }
        return null;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getInnAddr() {
        Area city = this.area;
        Area province = city.getParent();

        return province.getName()
                + "\t" + city.getName()
                + "\t" + innAddr;
    }

    public void setInnAddr(String innAddr) {
        this.innAddr = innAddr;
    }

    public String getOtaLink() {
        return otaLink;
    }

    public void setOtaLink(String otaLink) {
        this.otaLink = otaLink;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public InnRegion getInnRegion() {
        return innRegion;
    }

    public void setInnRegion(InnRegion innRegion) {
        this.innRegion = innRegion;
    }
}
