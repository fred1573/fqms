package com.project.entity.proxysale;

import java.util.Date;

/**
 * @author yuneng.huang on 2016/6/15.
 */
public class ProxySaleSubOrder {

    private Long id;
    private Date createTime = new Date();
    private Date updateTime = new Date();
    //房型名
    private String channelRoomTypeName;
    //房间数
    private int roomNums;
    //入住日期
    private Date checkInAt;
    //离店日期
    private Date checkOutAt;
    //代销订单投诉id
    private Long orderComplaintId;

    public String getChannelRoomTypeName() {
        return channelRoomTypeName;
    }

    public void setChannelRoomTypeName(String channelRoomTypeName) {
        this.channelRoomTypeName = channelRoomTypeName;
    }

    public int getRoomNums() {
        return roomNums;
    }

    public void setRoomNums(int roomNums) {
        this.roomNums = roomNums;
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

    public Long getOrderComplaintId() {
        return orderComplaintId;
    }

    public void setOrderComplaintId(Long orderComplaintId) {
        this.orderComplaintId = orderComplaintId;
    }
}
