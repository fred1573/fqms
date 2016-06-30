package com.project.entity.finance;

import com.project.entity.account.User;
import com.project.entity.proxysale.Channel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author frd
 */
@Entity
@Table(name = "finance_manual_order")
public class FinanceManualOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 订单号
    @Column(name = "order_id")
    private String orderId;

    // 备注
    @Column
    private String remark;

    // 分销商扣番茄金额
    @Column
    private BigDecimal refund;

    // 是否可用 true:未删除，会被查询到；反之查不到，已删除
    @Column
    private boolean available = true;

    // 订单创建时间
    @Column(name = "create_time")
    private Date createTime = new Date();

    // 订单更新时间
    @Column(name = "update_time")
    private Date updateTime;

    // 订单创建人
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "create_user")
    private User createUser;

    // 订单编辑人
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "update_user")
    private User updateUser;

    // 分销商
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "channel")
    private Channel channel;

    // 账期
    @Column(name = "settlement_time")
    private String settlementTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getRefund() {
        return refund;
    }

    public void setRefund(BigDecimal refund) {
        this.refund = refund;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public User getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(User updateUser) {
        this.updateUser = updateUser;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }
}
