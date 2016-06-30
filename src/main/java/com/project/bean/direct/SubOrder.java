package com.project.bean.direct;

/**
 * 子订单
 * Created by 番茄桑 on 2015/7/29.
 */
public class SubOrder {
    // 单价
    private String bookPrice;
    // 房型名称
    private String channelRoomTypeName;
    // 入住时间
    private String checkInAt;
    // 离日期
    private String checkOutAt;
    // 子订单id
    private String id;
    // 房间数
    private int roomTypeNums;
    // 夜数
    private String nightNumber;
    //测试字段
    private  int status;
    //测试字段
    private boolean eqStatus;
    //单价
    private String bossUnitPrice;

    public String getBossUnitPrice() {
        return bossUnitPrice;
    }

    public void setBossUnitPrice(String bossUnitPrice) {
        this.bossUnitPrice = bossUnitPrice;
    }

    public boolean isEqStatus() {
        return eqStatus;
    }

    public void setEqStatus(boolean eqStatus) {
        this.eqStatus = eqStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SubOrder{" +
                "bookPrice='" + bookPrice + '\'' +
                ", channelRoomTypeName='" + channelRoomTypeName + '\'' +
                ", checkInAt='" + checkInAt + '\'' +
                ", checkOutAt='" + checkOutAt + '\'' +
                ", id='" + id + '\'' +
                ", roomTypeNums=" + roomTypeNums +
                ", nightNumber='" + nightNumber + '\'' +
                ", status=" + status +
                ", eqStatus=" + eqStatus +
                '}';
    }
    public SubOrder() {

    }

    public String getNightNumber() {
        return nightNumber;
    }

    public void setNightNumber(String nightNumber) {
        this.nightNumber = nightNumber;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getChannelRoomTypeName() {
        return channelRoomTypeName;
    }

    public void setChannelRoomTypeName(String channelRoomTypeName) {
        this.channelRoomTypeName = channelRoomTypeName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRoomTypeNums() {
        return roomTypeNums;
    }

    public void setRoomTypeNums(int roomTypeNums) {
        this.roomTypeNums = roomTypeNums;
    }
}
