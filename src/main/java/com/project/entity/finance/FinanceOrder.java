package com.project.entity.finance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 子订单对象
 * Created by 番茄桑 on 2015/8/31.
 */
@Entity
@Table(name = "finance_order")
public class FinanceOrder {
    // 主键ID
    @Id
    private String id;
    // 父订单对象
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "main_id")
    @JsonIgnore
    private FinanceParentOrder financeParentOrder;
    // 入住日期
    @Column(name = "check_in_at")
    private Date checkInAt;
    // 退房日期
    @Column(name = "check_out_at")
    private Date checkOutAt;
    // 代销平台中录入的原价
    @Column(name = "original_price")
    private BigDecimal originalPrice;
    // 进价，也是下单的预定价格
    @Column(name = "book_price")
    private BigDecimal bookPrice;
    // 给第三方渠道的售价
    @Column(name = "sale_price")
    private BigDecimal salePrice;
    // 所关联的房型名称
    @Column(name = "channel_room_type_name")
    private String channelRoomTypeName;
    // 所关联的房间号
    @Column(name = "room_no")
    private String roomNo;
    // 房型间数
    @Column(name = "room_type_nums")
    private Integer roomTypeNums;
    // 房型ID
    @Column(name = "room_type_id")
    private Integer roomTypeId;
    // 夜数
    @Column(name = "nights")
    private Integer nights;
    // 是否删除，默认为false（未删除），true为删除
    @Column(name = "deleted")
    private boolean deleted;
    // 入住时间的字符串展示
    @Transient
    private String checkInAtStr;
    // 退房时间的字符串展示
    @Transient
    private String checkOutAtStr;
    // 渠道商单价
    @Column(name = "channel_amount")
    private BigDecimal channelAmount;
    // 番茄加减价金额
    @Column(name = "extra_price")
    private BigDecimal extraPrice;
    // 客栈订单价格（PMS订单价格）
    @Column(name = "inn_amount")
    private BigDecimal innAmount;

    public BigDecimal getInnAmount() {
        return innAmount;
    }

    public void setInnAmount(BigDecimal innAmount) {
        this.innAmount = innAmount;
    }

    public BigDecimal getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(BigDecimal channelAmount) {
        this.channelAmount = channelAmount;
    }

    public BigDecimal getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(BigDecimal extraPrice) {
        this.extraPrice = extraPrice;
    }

    public String getCheckInAtStr() {
        Date checkInAt = getCheckInAt();
        if(checkInAt != null) {
            return DateFormatUtils.format(checkInAt, "yyyy-MM-dd");
        }
        return checkInAtStr;
    }

    public void setCheckInAtStr(String checkInAtStr) {
        this.checkInAtStr = checkInAtStr;
    }

    public String getCheckOutAtStr() {
        Date checkOutAt = getCheckOutAt();
        if (checkOutAt != null) {
            return DateFormatUtils.format(checkOutAt, "yyyy-MM-dd");
        }
        return checkOutAtStr;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setCheckOutAtStr(String checkOutAtStr) {
        this.checkOutAtStr = checkOutAtStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FinanceParentOrder getFinanceParentOrder() {
        return financeParentOrder;
    }

    public void setFinanceParentOrder(FinanceParentOrder financeParentOrder) {
        this.financeParentOrder = financeParentOrder;
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

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(BigDecimal bookPrice) {
        this.bookPrice = bookPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getChannelRoomTypeName() {
        return channelRoomTypeName;
    }

    public void setChannelRoomTypeName(String channelRoomTypeName) {
        this.channelRoomTypeName = channelRoomTypeName;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Integer getRoomTypeNums() {
        return roomTypeNums;
    }

    public void setRoomTypeNums(Integer roomTypeNums) {
        this.roomTypeNums = roomTypeNums;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public Integer getNights() {

        return nights;
    }

    public void setNights(Integer nights) {
        this.nights = nights;
    }
}
