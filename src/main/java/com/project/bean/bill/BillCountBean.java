package com.project.bean.bill;

/**
 * Created by xiamaoxuan on 2014/8/1.
 * 用来记录页面统计
 * 例如：截止您所选时间段内，共有 300 个订单，
 * 总金额为 8000.0，其中 200个订单未结算，
 * 未结算金额为 4000.0
 */
public class BillCountBean {
    //总金额
    private Double totalAmount;
    //总订单数
    private Integer totalOrders;
    //未结算订单数量
    private Integer notBalanceOrders;
    //未结算金额
    private Double notBalanceAmount;

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getNotBalanceOrders() {
        return notBalanceOrders;
    }

    public void setNotBalanceOrders(Integer notBalanceOrders) {
        this.notBalanceOrders = notBalanceOrders;
    }

    public Double getNotBalanceAmount() {
        return notBalanceAmount;
    }

    public void setNotBalanceAmount(Double notBalanceAmount) {
        this.notBalanceAmount = notBalanceAmount;
    }
}
