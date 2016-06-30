package com.project.bean.finance;

import java.math.BigDecimal;
import java.util.List;

/**
 * 对外提供的账单VO对象
 * Created by sam on 2016/4/15.
 */
public class ApiParentOrder {
    // 合作模式
    private String priceStrategy;
    // 分销商订单号
    private String channelOrderNo;
    // 客人姓名
    private String userName;
    // 手机号
    private String contact;
    // 客栈账单总价
    private BigDecimal innAmount;
    // 下单时间
    private String orderTime;
    // 佣金（正常订单）
    private BigDecimal fqSettlementAmount;
    // 结算金额（正常订单）
    private BigDecimal innSettlementAmount;
    // 赔付金额（赔付订单）
    private BigDecimal innPayment;
    // 退款金额（退款订单）
    private BigDecimal innRefund;
    // 子订单集合
    private List<ApiOrder> orderList;
    // 账单类型，1：正常订单，2：赔付订单，3：退款订单
    private Integer billType;

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public String getPriceStrategy() {
        return priceStrategy;
    }

    public void setPriceStrategy(String priceStrategy) {
        this.priceStrategy = priceStrategy;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
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

    public BigDecimal getInnAmount() {
        return innAmount;
    }

    public void setInnAmount(BigDecimal innAmount) {
        this.innAmount = innAmount;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getFqSettlementAmount() {
        return fqSettlementAmount;
    }

    public void setFqSettlementAmount(BigDecimal fqSettlementAmount) {
        this.fqSettlementAmount = fqSettlementAmount;
    }

    public BigDecimal getInnSettlementAmount() {
        return innSettlementAmount;
    }

    public void setInnSettlementAmount(BigDecimal innSettlementAmount) {
        this.innSettlementAmount = innSettlementAmount;
    }

    public BigDecimal getInnPayment() {
        return innPayment;
    }

    public void setInnPayment(BigDecimal innPayment) {
        this.innPayment = innPayment;
    }

    public BigDecimal getInnRefund() {
        return innRefund;
    }

    public void setInnRefund(BigDecimal innRefund) {
        this.innRefund = innRefund;
    }

    public List<ApiOrder> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<ApiOrder> orderList) {
        this.orderList = orderList;
    }
}
