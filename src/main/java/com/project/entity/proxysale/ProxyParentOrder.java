package com.project.entity.proxysale;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2015/7/3.
 */
@Entity
@Table(name = "tomato_proxysale_parent_order")
public class ProxyParentOrder {

    public static final Integer SUC = 1;
    public static final Integer CANCEL = 2;
    /**
     * 不能用otaOrderNo作父订单主键，有可能跟其它渠道订单ID重复
     */
    @Id
    private String id;

    /**
     * 价格模式
     * 1 底价 2 卖价
     */
    @Column(name = "price_pattern")
    private Short pricePattern;

    /**
     * 渠道订单号
     */
    @Column(name = "ota_order_no")
    private String otaOrderNo;

//    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "ota_id")
    private Integer otaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proxy_inn")
    private ProxyInn proxyInn;

    /**
     * 订房量
     */
    @Column(name = "room_type_num")
    private Integer roomTypeNum;

    /**
     * 违约金
     */
    @Column
    private BigDecimal penalty;

    /**
     * 状态 1：成功  2：取消
     */
    @Column
    private Integer status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parentOrder")
    private Set<ProxyOrder> childOrders = new HashSet<>();

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 根据此时间查找渠道比例
     */
    @Column(name = "per_time")
    private Date perTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Short getPricePattern() {
        return pricePattern;
    }

    public void setPricePattern(Short pricePattern) {
        this.pricePattern = pricePattern;
    }

    public String getOtaOrderNo() {
        return otaOrderNo;
    }

    public void setOtaOrderNo(String otaOrderNo) {
        this.otaOrderNo = otaOrderNo;
    }

    public Integer getOtaId() {
        return otaId;
    }

    public void setOtaId(Integer otaId) {
        this.otaId = otaId;
    }

    public ProxyInn getProxyInn() {
        return proxyInn;
    }

    public void setProxyInn(ProxyInn proxyInn) {
        this.proxyInn = proxyInn;
    }

    public Integer getRoomTypeNum() {
        return roomTypeNum;
    }

    public void setRoomTypeNum(Integer roomTypeNum) {
        this.roomTypeNum = roomTypeNum;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }

    public void setPenalty(BigDecimal penalty) {
        this.penalty = penalty;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Set<ProxyOrder> getChildOrders() {
        return childOrders;
    }

    public void setChildOrders(Set<ProxyOrder> childOrders) {
        this.childOrders = childOrders;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setPerTime(Date perTime) {
        this.perTime = perTime;
    }
}
