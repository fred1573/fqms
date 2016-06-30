package com.project.enumeration;

/**
 * 代销订单投诉类型
 *
 * @author yuneng.huang on 2016/6/12.
 */
public enum ProxySaleOrderComplaintType implements EnumDescription{
    //    不与番茄合作
    NONCOOPERATION("不与番茄合作"),
    //    到店无房
    NO_ROOM("到店无房"),
    //    客栈加价
    PRICE_INCREMENT("客栈加价"),
    //    不会操作系统
    UNABLE("不会操作系统"),
    //    系统原因
    SYSTEM_ERROR("系统原因"),
    //    房型/外网匹配错误
    ROOM_TYPE_ERROR("外网匹配错误"),
    //    客人原因取消订单
    CANCEL_ORDER("客人原因取消订单"),
    //    客人联系不上商家
    CAN_NOT_CONTACT("客人联系不上商家"),
    //    暂停营业
    SUSPENSION_BUSINESS("暂停营业"),
    //    其他
    OTHER("其他");

    ProxySaleOrderComplaintType(String description) {
        this.description = description;
    }

    private String description;

    @Override
    public String getDescription() {
        return description;
    }
}
