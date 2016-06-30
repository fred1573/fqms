package com.project.bean.bo;

import java.math.BigDecimal;

/**
 * 用于封装按照分销商ID、分销商名称、账单状态分组统计结果的对象
 * Created by Administrator on 2016/4/6.
 */
public class ChannelSettlementData {
    // 分销商ID
    private Integer channelId;
    // 分销商名称
    private String channelName;
    // 订单数量
    private int orderAmount = 0;
    // 客栈订单总金额（1、66、77、88）
    private BigDecimal innOrderAmount = BigDecimal.ZERO;
    // 分销商订单总金额（包括状态1、66、77）
    private BigDecimal channelOrderAmount = BigDecimal.ZERO;
    // 分销商结算金额（状态为1的账单结算金额）
    private BigDecimal channelSettlementAmount = BigDecimal.ZERO;
    // 获取渠道扣番茄金额（赔付)
    private BigDecimal channelDebit = BigDecimal.ZERO;
    // 获取渠道扣番茄退款金额
    private BigDecimal channelRefund = BigDecimal.ZERO;
    // 番茄退款佣金
    private BigDecimal totalFqRefundCommission = BigDecimal.ZERO;
    // 客栈结算金额(状态为1的账单结算金额)
    private BigDecimal innSettlementAmount = BigDecimal.ZERO;
    // 客栈赔付金额(赔付)
    private BigDecimal innPayment = BigDecimal.ZERO;
    // 客栈退款金额(退款)
    private BigDecimal innRefund = BigDecimal.ZERO;
    // 番茄补款金额(补款)
    private BigDecimal fqReplenishment = BigDecimal.ZERO;
    // 番茄正常订单收入
    private BigDecimal fqNormalOrderIncome = BigDecimal.ZERO;
    public BigDecimal getInnSettlementAmount() {
        return innSettlementAmount;
    }

    public void setInnSettlementAmount(BigDecimal innSettlementAmount) {
        this.innSettlementAmount = innSettlementAmount;
    }

    public BigDecimal getInnPayment() {
        return innPayment;
    }

    public void setInnPayment(BigDecimal innPayment) {
        this.innPayment = innPayment;
    }

    public BigDecimal getInnRefund() {
        return innRefund;
    }

    public void setInnRefund(BigDecimal innRefund) {
        this.innRefund = innRefund;
    }

    public BigDecimal getFqReplenishment() {
        return fqReplenishment;
    }

    public void setFqReplenishment(BigDecimal fqReplenishment) {
        this.fqReplenishment = fqReplenishment;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getInnOrderAmount() {
        return innOrderAmount;
    }

    public void setInnOrderAmount(BigDecimal innOrderAmount) {
        this.innOrderAmount = innOrderAmount;
    }

    public BigDecimal getChannelOrderAmount() {
        return channelOrderAmount;
    }

    public void setChannelOrderAmount(BigDecimal channelOrderAmount) {
        this.channelOrderAmount = channelOrderAmount;
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

    public BigDecimal getChannelSettlementAmount() {
        return channelSettlementAmount;
    }

    public void setChannelSettlementAmount(BigDecimal channelSettlementAmount) {
        this.channelSettlementAmount = channelSettlementAmount;
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

    public BigDecimal getTotalFqRefundCommission() {
        return totalFqRefundCommission;
    }

    public void setTotalFqRefundCommission(BigDecimal totalFqRefundCommission) {
        this.totalFqRefundCommission = totalFqRefundCommission;
    }

    public BigDecimal getFqNormalOrderIncome() {
        return fqNormalOrderIncome;
    }

    public void setFqNormalOrderIncome(BigDecimal fqNormalOrderIncome) {
        this.fqNormalOrderIncome = fqNormalOrderIncome;
    }
}
