package com.project.bean.finance;

import java.math.BigDecimal;

/**
 * 对外提供的子账单VO对象
 * Created by sam on 2016/4/15.
 */
public class ApiOrder {
    // 房型
    private String channelRoomTypeName;
    // 房间数
    private Integer roomTypeNums;
    // 客栈单价
    private BigDecimal innAmount;
    // 入住日期
    private String checkInAt;
    // 退房日期
    private String checkOutAt;

    public String getChannelRoomTypeName() {
        return channelRoomTypeName;
    }

    public void setChannelRoomTypeName(String channelRoomTypeName) {
        this.channelRoomTypeName = channelRoomTypeName;
    }

    public Integer getRoomTypeNums() {
        return roomTypeNums;
    }

    public void setRoomTypeNums(Integer roomTypeNums) {
        this.roomTypeNums = roomTypeNums;
    }

    public BigDecimal getInnAmount() {
        return innAmount;
    }

    public void setInnAmount(BigDecimal innAmount) {
        this.innAmount = innAmount;
    }

    public String getCheckInAt() {
        return checkInAt;
    }

    public void setCheckInAt(String checkInAt) {
        this.checkInAt = checkInAt;
    }

    public String getCheckOutAt() {
        return checkOutAt;
    }

    public void setCheckOutAt(String checkOutAt) {
        this.checkOutAt = checkOutAt;
    }
}
