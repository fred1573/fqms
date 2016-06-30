package com.project.entity.finance;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 渠道对账对象
 * Created by 番茄桑 on 2015/8/31.
 */
@Entity
@Table(name = "finance_channel_settlement")
public class FinanceChannelSettlement {
    // 主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 渠道ID
    @Column(name = "channel_id")
    private Integer channelId;
    // 渠道名称
    @Column(name = "channel_name")
    private String channelName;
    // 订单总数
    @Column(name = "total_order")
    private int totalOrder;
    // 渠道结算金额
    @Column(name = "channel_settlement_amount")
    private BigDecimal channelSettlementAmount;
    // 核单状态(0:未核,1:已核成功,2:已核失败)
    @Column(name = "audit_status")
    private String auditStatus = "0";
    // 渠道商款项是否收到
    @Column(name = "is_arrival")
    private boolean isArrival;
    // 结算时间
    @Column(name = "settlement_time")
    private String settlementTime;
    // 订单总金额
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    // 实收金额
    @Column(name = "income_amount")
    private BigDecimal incomeAmount;
    // 实收情况备注
    @Column(name = "remarks")
    private String remarks;
    // 分销商订单总额
    @Column(name = "channel_amount")
    private BigDecimal channelAmount;


    // 分销商扣番茄赔付金额
    @Column(name = "channel_debit")
    private BigDecimal channelDebit;
    // 分销商扣番茄退款金额
    @Column(name = "channel_refund")
    private BigDecimal channelRefund;
    // 番茄收入（实际收入）
    @Column(name = "fq_real_income")
    private BigDecimal fqRealIncome;
    // 分销商实际结算金额
    @Column(name = "channel_real_amount")
    private BigDecimal channelRealAmount;

    // 无订单赔付金额总额
    @Column(name = "no_order_debit_amount")
    private BigDecimal noOrderDebitAmount;
    //跨期退款,上期未结算，本期平账
    @Column(name = "current_refund_amount")
    private BigDecimal currentRefundAmount;
    //跨期退款,已结算退款
    @Column(name = "refunded_amount")
    private BigDecimal refundedAmount;
    //跨期退款,本期不结算，下期平账
    @Column(name = "next_refund_amount")
    private BigDecimal nextRefundAmount;
    //番茄暂收
    @Column(name="fq_temp")
    private BigDecimal fqTemp;

    public BigDecimal getFqTemp() {
        return fqTemp;
    }

    public void setFqTemp(BigDecimal fqTemp) {
        this.fqTemp = fqTemp;
    }

    public BigDecimal getCurrentRefundAmount() {
        return currentRefundAmount;
    }

    public void setCurrentRefundAmount(BigDecimal currentRefundAmount) {
        this.currentRefundAmount = currentRefundAmount;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public BigDecimal getNextRefundAmount() {
        return nextRefundAmount;
    }

    public void setNextRefundAmount(BigDecimal nextRefundAmount) {
        this.nextRefundAmount = nextRefundAmount;
    }

    public BigDecimal getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(BigDecimal channelAmount) {
        this.channelAmount = channelAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(int totalOrder) {
        this.totalOrder = totalOrder;
    }

    public BigDecimal getChannelSettlementAmount() {
        return channelSettlementAmount;
    }

    public void setChannelSettlementAmount(BigDecimal channelSettlementAmount) {
        this.channelSettlementAmount = channelSettlementAmount;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public boolean getIsArrival() {
        return isArrival;
    }

    public void setIsArrival(boolean isArrival) {
        this.isArrival = isArrival;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public BigDecimal getChannelDebit() {
        return channelDebit;
    }

    public void setChannelDebit(BigDecimal channelDebit) {
        this.channelDebit = channelDebit;
    }

    public BigDecimal getChannelRefund() {
        return channelRefund;
    }

    public void setChannelRefund(BigDecimal channelRefund) {
        this.channelRefund = channelRefund;
    }

    public BigDecimal getFqRealIncome() {
        return fqRealIncome;
    }

    public void setFqRealIncome(BigDecimal fqRealIncome) {
        this.fqRealIncome = fqRealIncome;
    }

    public BigDecimal getChannelRealAmount() {
        return channelRealAmount;
    }

    public void setChannelRealAmount(BigDecimal channelRealAmount) {
        this.channelRealAmount = channelRealAmount;
    }

    public BigDecimal getNoOrderDebitAmount() {
        return noOrderDebitAmount;
    }

    public void setNoOrderDebitAmount(BigDecimal noOrderDebitAmount) {
        this.noOrderDebitAmount = noOrderDebitAmount;
    }
}
