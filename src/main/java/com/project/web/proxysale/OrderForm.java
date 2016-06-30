package com.project.web.proxysale;

/**
 * order={
 "accountId": 1000926,
 "childOrders": [{
 bookRoomPrice: 100,
 checkInAt: "2015-08-11",
 checkOutAt: "2015-08-12",
 roomTypeId: 111111,
 roomTypeName: "海景房"
 }],
 "innId": 26083,
 "otaId": 903,
 "otaOrderNo": "999888777",
 "perTime": "2015-08-11",
 "roomTypeNum": 1
 }
 * Created by Administrator on 2015/8/12.
 */
public class OrderForm {

    private Integer accountId;

    private Integer innId;

    private Integer otaId;

    private String otaOrderNo;

    private String perTime;

    private Integer roomTypeNum;

    private String bookRoomPrices;

    private String checkInAts;

    private String checkOutAts;

    private String roomTypeIds;

    private String roomTypeNames;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public Integer getOtaId() {
        return otaId;
    }

    public void setOtaId(Integer otaId) {
        this.otaId = otaId;
    }

    public String getOtaOrderNo() {
        return otaOrderNo;
    }

    public void setOtaOrderNo(String otaOrderNo) {
        this.otaOrderNo = otaOrderNo;
    }

    public String getPerTime() {
        return perTime;
    }

    public void setPerTime(String perTime) {
        this.perTime = perTime;
    }

    public Integer getRoomTypeNum() {
        return roomTypeNum;
    }

    public void setRoomTypeNum(Integer roomTypeNum) {
        this.roomTypeNum = roomTypeNum;
    }

    public String getBookRoomPrices() {
        return bookRoomPrices;
    }

    public void setBookRoomPrices(String bookRoomPrices) {
        this.bookRoomPrices = bookRoomPrices;
    }

    public String getCheckInAts() {
        return checkInAts;
    }

    public void setCheckInAts(String checkInAts) {
        this.checkInAts = checkInAts;
    }

    public String getCheckOutAts() {
        return checkOutAts;
    }

    public void setCheckOutAts(String checkOutAts) {
        this.checkOutAts = checkOutAts;
    }

    public String getRoomTypeIds() {
        return roomTypeIds;
    }

    public void setRoomTypeIds(String roomTypeIds) {
        this.roomTypeIds = roomTypeIds;
    }

    public String getRoomTypeNames() {
        return roomTypeNames;
    }

    public void setRoomTypeNames(String roomTypeNames) {
        this.roomTypeNames = roomTypeNames;
    }
}
