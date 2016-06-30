package com.project.bean.direct;

import java.util.List;

/**
 * 用于直连订单展示的VO对象
 * Created by 番茄桑 on 2015/7/29.
 */
public class DirectOrderVo {
    // 0 未结算 1 结算
    private int balance;
    // 渠道订单Id
    private String channelId;
    // 渠道名称
    private String channelName;
    // 订单详情，子订单集合
    private List<SubOrder> channelOrderList;
    // 订单号
    private String channelOrderNo;
    // 订单状态（类型）
    private String conName;
    // 联系方式
    private String contact;
    // 主订单id
    private String id;
    // 操作人
    private String operatedUser;
    // 下单时间
    private String orderTime;
    // 订单已付金额
    private String paidAmount;
    // 支付类型(prepay(预付)、assure(担保))
    private String payType;
    // 特殊需求
    private String remark;
    // 订单总金额
    private String totalAmount;
    // 用户真实姓名（顾客姓名）
    private String userName;
    // 客栈名称
    private String innName;

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<SubOrder> getChannelOrderList() {
        return channelOrderList;
    }

    public void setChannelOrderList(List<SubOrder> channelOrderList) {
        this.channelOrderList = channelOrderList;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
    }

    public String getConName() {
        return conName;
    }

    public void setConName(String conName) {
        this.conName = conName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperatedUser() {
        return operatedUser;
    }

    public void setOperatedUser(String operatedUser) {
        this.operatedUser = operatedUser;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
