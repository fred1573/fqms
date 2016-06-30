package com.project.entity.proxysale;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 代销订单
 * Created by Administrator on 2015/7/2.
 */
@Entity
@Table(name = "tomato_proxysale_order")
public class ProxyOrder {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_order")
    private ProxyParentOrder parentOrder;

    /**
     * 订单价格
     */
    @Column(name = "book_room_price")
    private BigDecimal bookRoomPrice;

    /**
     * 入住日期
     */
    @Column(name = "check_in_at")
    private Date checkInAt;

    /**
     * 退房日期
     */
    @Column(name = "check_out_at")
    private Date checkOutAt;

    /**
     * 房型ID
     */
    @Column(name = "room_type_id")
    private Integer roomTypeId;

    /**
     * 房型名称
     */
    @Column(name = "room_type_name")
    private String roomTypeName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProxyParentOrder getParentOrder() {
        return parentOrder;
    }

    public void setParentOrder(ProxyParentOrder parentOrder) {
        this.parentOrder = parentOrder;
    }

    public BigDecimal getBookRoomPrice() {
        return bookRoomPrice;
    }

    public void setBookRoomPrice(BigDecimal bookRoomPrice) {
        this.bookRoomPrice = bookRoomPrice;
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

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }
}
