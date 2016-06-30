package com.project.entity.proxysale;

import com.project.entity.account.User;
import com.project.entity.area.Area;

import javax.persistence.*;
import java.util.*;

/**
 * 代销渠道
 * Created by Administrator on 2015/6/4.
 */
@Entity
@Table(name = "tomato_proxysale_channel")
public class Channel {

    @Id
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel", fetch = FetchType.LAZY)
    private List<PriceStrategy> priceStrategies = new ArrayList<>();

    @Column(name = "update_time")
    private Date updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator")
    private User operator;

    @ManyToMany(targetEntity = Area.class, cascade = CascadeType.ALL)
    @JoinTable(
            name = "tomato_proxysale_channel_area",
            joinColumns = {@JoinColumn(name = "channel")},
            inverseJoinColumns = {@JoinColumn(name = "area")}
    )
    private Set<Area> saleArea = new HashSet<>();

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY)
    private Set<ProxysaleChannel> pcs;

    @Column(name = "channel_name")
    private String channelName;

    public Set<ProxysaleChannel> getPcs() {
        return pcs;
    }

    public void setPcs(Set<ProxysaleChannel> pcs) {
        this.pcs = pcs;
    }

    @Column(name = "company_code")
    private String companyCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<PriceStrategy> getPriceStrategies() {
        return this.priceStrategies;
    }

    /**
     * 返回有效的价格策略
     *
     * @return Set
     */
    public Set<PriceStrategy> getValidStrategies() {
        Set<PriceStrategy> result = new HashSet<>();
        for (PriceStrategy priceStrategy : priceStrategies) {
            if (priceStrategy.isValid()) {
                result.add(priceStrategy);
            }
        }
        return result;
    }

    public void setPriceStrategies(List<PriceStrategy> priceStrategies) {
        this.priceStrategies = priceStrategies;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public Set<Area> getSaleArea() {
        return saleArea;
    }

    public void setSaleArea(Set<Area> saleArea) {
        this.saleArea = saleArea;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * 获取有效的底价策略，没有否返回null
     *
     * @return
     */
    public Float getValidBasePriceStrategy() {
        return getValidPriceStrategy(PriceStrategy.STRATEGY_BASE_PRICE);
    }

    /**
     * 获取有效的卖价策略，没有否返回null
     *
     * @return
     */
    public Float getValidSalePriceStrategy() {
        return getValidPriceStrategy(PriceStrategy.STRATEGY_SALE_PRICE);
    }

    /**
     * 获取有效的卖转底策略，没有则返回Null
     *
     * @return
     */
    public Float getValidSaleBasePriceStrategy() {
        return getValidPriceStrategy(PriceStrategy.STRATEGY_SALE_BASE_PRICE);
    }

    private Float getValidPriceStrategy(Short strategy) {
        for (PriceStrategy priceStrategy : priceStrategies) {
            if (priceStrategy.isValid() && priceStrategy.getStrategy().shortValue() == strategy.shortValue()) {
                return priceStrategy.getPercentage();
            }
        }
        return null;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", priceStrategies=" + priceStrategies +
                ", updateTime=" + updateTime +
                ", operator=" + operator +
                ", saleArea=" + saleArea +
                '}';
    }

}
