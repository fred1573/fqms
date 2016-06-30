package com.project.bean.finance;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 渠道对账订单对象
 * Created by 番茄桑 on 2015/9/22.
 */
public class ChannelReconciliation {
    // 渠道订单号
    private String channelOrderNo;
    // 客栈名称
    private String innName;
    // 房型
    private String channelRoomTypeName;
    // 入住人
    private String userName;
    // 预订人联系电话
    private String contact;
    // 入住日期
    private Date checkInAt;
    // 离店日期
    private Date checkOutAt;
    // 房间数
    private Integer roomTypeNums;
    // 间夜数
    private Integer nights;
    // 价格模式
    private Short priceStrategy;
    // 订单金额
    private BigDecimal totalAmount;
    // 结算金额
    private BigDecimal channelSettlementAmount;
    // 客栈结算金额
    private BigDecimal innSettlementAmount;
    // 费用类型(1:房费，2:违约金)
    private Short costType;

    public BigDecimal getInnSettlementAmount() {
        return innSettlementAmount;
    }

    public void setInnSettlementAmount(BigDecimal innSettlementAmount) {
        this.innSettlementAmount = innSettlementAmount;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getChannelRoomTypeName() {
        return channelRoomTypeName;
    }

    public void setChannelRoomTypeName(String channelRoomTypeName) {
        this.channelRoomTypeName = channelRoomTypeName;
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

    public Date getCheckInAt() {
        return checkInAt;
    }

    public void setCheckInAt(Date checkInAt) {
        this.checkInAt = checkInAt;
    }

    public Date getCheckOutAt() {
        return checkOutAt;
    }

    public void setCheckOutAt(Date checkOutAt) {
        this.checkOutAt = checkOutAt;
    }

    public Integer getRoomTypeNums() {
        return roomTypeNums;
    }

    public void setRoomTypeNums(Integer roomTypeNums) {
        this.roomTypeNums = roomTypeNums;
    }

    public Integer getNights() {
        return nights;
    }

    public void setNights(Integer nights) {
        this.nights = nights;
    }

    public Short getPriceStrategy() {
        return priceStrategy;
    }

    public void setPriceStrategy(Short priceStrategy) {
        this.priceStrategy = priceStrategy;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getChannelSettlementAmount() {
        return channelSettlementAmount;
    }

    public void setChannelSettlementAmount(BigDecimal channelSettlementAmount) {
        this.channelSettlementAmount = channelSettlementAmount;
    }

    public Short getCostType() {
        return costType;
    }

    public void setCostType(Short costType) {
        this.costType = costType;
    }
}
