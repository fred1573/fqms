package com.project.bean.finance;

import com.project.bean.vo.AjaxBase;

/**
 * 用于返回渠道对账单上传后，提示执行结果
 * Created by 番茄桑 on 2015/9/21.
 */
public class AjaxChannelReconciliation extends AjaxBase {
    // 本月番茄订单数量
    private Integer fqOrders;
    // 渠道上传对账单中的订单数量
    private Integer channelOrders;
    // 对账成功订单数
    private Integer successOrders;
    // 对账失败订单数
    private Integer failureOrders;
    // 本次对账的结果，1：已核成功，2：已核失败
    private String auditStatus;
    // 以番茄订单为蓝本，渠道对账单中遗漏的订单数量
    private int channelMissOrderAmount;
    // 以番茄订单为蓝本，渠道对账单中遗漏的订单号码
    private String channelMissOrderNo;
    // 以渠道对账单为蓝本，番茄遗漏的订单数量
    private int fqMissOrderAmount;
    // 以渠道对账单为蓝本，番茄遗漏的订单号码
    private String fqMissOrderNo;

    public AjaxChannelReconciliation(int status) {
        super(status);
    }

    public AjaxChannelReconciliation(int status, String message) {
        super(status, message);
    }

    public AjaxChannelReconciliation(int status, String message, Integer fqOrders, Integer channelOrders, Integer successOrders, Integer failureOrders, String auditStatus, int fqMissOrderAmount, String fqMissOrderNo) {
        super(status, message);
        this.fqOrders = fqOrders;
        this.channelOrders = channelOrders;
        this.successOrders = successOrders;
        this.failureOrders = failureOrders;
        this.auditStatus = auditStatus;
        this.fqMissOrderAmount = fqMissOrderAmount;
        this.fqMissOrderNo = fqMissOrderNo;
    }

    public int getChannelMissOrderAmount() {
        return channelMissOrderAmount;
    }

    public void setChannelMissOrderAmount(int channelMissOrderAmount) {
        this.channelMissOrderAmount = channelMissOrderAmount;
    }

    public String getChannelMissOrderNo() {
        return channelMissOrderNo;
    }

    public void setChannelMissOrderNo(String channelMissOrderNo) {
        this.channelMissOrderNo = channelMissOrderNo;
    }

    public int getFqMissOrderAmount() {
        return fqMissOrderAmount;
    }

    public void setFqMissOrderAmount(int fqMissOrderAmount) {
        this.fqMissOrderAmount = fqMissOrderAmount;
    }

    public String getFqMissOrderNo() {
        return fqMissOrderNo;
    }

    public void setFqMissOrderNo(String fqMissOrderNo) {
        this.fqMissOrderNo = fqMissOrderNo;
    }

    public Integer getFqOrders() {
        return fqOrders;
    }

    public void setFqOrders(Integer fqOrders) {
        this.fqOrders = fqOrders;
    }

    public Integer getChannelOrders() {
        return channelOrders;
    }

    public void setChannelOrders(Integer channelOrders) {
        this.channelOrders = channelOrders;
    }

    public Integer getSuccessOrders() {
        return successOrders;
    }

    public void setSuccessOrders(Integer successOrders) {
        this.successOrders = successOrders;
    }

    public Integer getFailureOrders() {
        return failureOrders;
    }

    public void setFailureOrders(Integer failureOrders) {
        this.failureOrders = failureOrders;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }
}
