package com.project.entity.finance;

import java.util.Date;

/**
 * Created by admin on 2016/4/1.
 */
//仅用于统计子订单房间数，夜数
public class UtilsOrder {
    //房间数
    private Integer rooms;
    //夜数
    private Integer nights;
    //住店日期
    private Date checkIn;
    //离店日期
    private Date checkOut;

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public Integer getNights() {
        return nights;
    }

    public void setNights(Integer nights) {
        this.nights = nights;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }
}
