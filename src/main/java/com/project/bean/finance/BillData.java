package com.project.bean.finance;

import java.math.BigDecimal;

/**
 * 客栈账期展示对象，用于对外提供的接口
 * Created by sam on 2016/4/14.
 */
public class BillData {
    private Integer innId;
    // 结算周期
    private String settlementTime;
    // 订单总数
    private int totalOrder;
    // 客栈订单总金额
    private BigDecimal totalAmount;
    // 应结金额
    private BigDecimal innSettlementAmount;
    // 退款金额
    private BigDecimal refundAmount;
    // 赔付金额
    private BigDecimal innPayment;
    // 实结金额=应结金额-退款金额-赔付金额+补款金额
    private BigDecimal afterPaymentAmount;
    // 结算状态
    private String settlementStatus;
    // 账单确认状况，未确认，已确认，系统自动确认
    private String confirmStatus;

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public int getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(int totalOrder) {
        this.totalOrder = totalOrder;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getInnSettlementAmount() {
        return innSettlementAmount;
    }

    public void setInnSettlementAmount(BigDecimal innSettlementAmount) {
        this.innSettlementAmount = innSettlementAmount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getInnPayment() {
        return innPayment;
    }

    public void setInnPayment(BigDecimal innPayment) {
        this.innPayment = innPayment;
    }

    public BigDecimal getAfterPaymentAmount() {
        return afterPaymentAmount;
    }

    public void setAfterPaymentAmount(BigDecimal afterPaymentAmount) {
        this.afterPaymentAmount = afterPaymentAmount;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(String confirmStatus) {
        this.confirmStatus = confirmStatus;
    }
}
