package com.project.entity.finance;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客栈渠道结算对象
 * Created by admin on 2016/1/13.
 */
@Entity
@Table(name = "finance_inn_channel_settlement")
public class FinanceInnChannelSettlement {

    //主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //最后修改时间
    @Column(name = "date_updated")
    private Date dateUpdate = new Date();
    //PMS客栈ID
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "inn_id")
    @JsonIgnore
    private FinanceInnSettlementInfo financeInnSettlementInfo;
    //渠道ID
    @Column(name = "channel_id")
    private Integer channelId;
    //渠道名称
    @Column(name = "channel_name")
    private String channelName;
    //账期
    @Column(name = "settlement_time")
    private String settlementTime;
    //订单总个数
    @Column(name = "total_order")
    private Integer totalOrder;
    //订单总金额
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    // 番茄结算金额
    @Column(name = "fq_settlement_amount")
    private BigDecimal fqSettlementAmount;
    // 客栈结算金额
    @Column(name = "inn_settlement_amount")
    private BigDecimal innSettlementAmount;
    //是否账实相符
    @Column(name = "is_match")
    private boolean isMatch;
    //渠道商结算金额
    @Column(name = "channel_settlement_amount")
    private BigDecimal channelSettlementAmount;
    //间夜量
    @Column(name = "room_nights")
    private Integer roomNights;
    //实付金额
    @Column(name = "real_payment")
    private BigDecimal realPayment;
    //实付备注
    @Column(name = "payment_remark")
    private String paymentRemark;
    //分销商订单总金额
    @Column(name = "channel_amount")
    private BigDecimal channelAmount;

    //是否含有特殊订单
    @Column(name = "is_special")
    private boolean isSpecial;
    //客栈赔付金额
    @Column(name = "inn_payment")
    private BigDecimal innPayment;
    //本期客栈退款金额
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;
    //番茄补款金额
    @Column(name = "fq_replenishment")
    private BigDecimal fqReplenishment;

    //客栈赔付番茄承担金额
    @Column(name = "fq_bear_amount")
    private BigDecimal fqBearAmount;
    //客栈赔付番茄收入金额
    @Column(name = "fq_income_amount")
    private BigDecimal fqIncomeAmount;
    //番茄退佣金收入总额
    @Column(name = "fq_refund_commission_amount")
    private BigDecimal fqRefundCommissionAmount;
    //本期番茄退往来金额
    @Column(name = "cur_fq_refund_contracts_amount")
    private BigDecimal curFqRefundContacts;
    //后期番茄退往来金额
    @Column(name = "aft_fq_refund_contracts_amount")
    private BigDecimal aftFqRefundContacts;
    // 分销商实际结算金额
    @Column(name = "channel_real_settlement_amount")
    private BigDecimal channelRealSettlementAmount;
    // 番茄实际订单收入
    @Column(name = "fq_normal_income")
    private BigDecimal fqNormalIncome;
    //客栈实际结算
    @Column(name = "inn_real_settlement")
    private BigDecimal innRealSettlement;
    //番茄暂收金额
    @Column(name = "fq_temp")
    private BigDecimal fqTemp;

    public BigDecimal getFqTemp() {
        return fqTemp;
    }

    public void setFqTemp(BigDecimal fqTemp) {
        this.fqTemp = fqTemp;
    }

    public BigDecimal getInnRealSettlement() {
        return innRealSettlement;
    }

    public void setInnRealSettlement(BigDecimal innRealSettlement) {
        this.innRealSettlement = innRealSettlement;
    }

    public boolean isMatch() {
        return isMatch;
    }

    public BigDecimal getFqReplenishment() {
        return fqReplenishment;
    }

    public void setFqReplenishment(BigDecimal fqReplenishment) {
        this.fqReplenishment = fqReplenishment;
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

    public boolean getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }


    public BigDecimal getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(BigDecimal channelAmount) {
        this.channelAmount = channelAmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(Date dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public FinanceInnSettlementInfo getFinanceInnSettlementInfo() {
        return financeInnSettlementInfo;
    }

    public void setFinanceInnSettlementInfo(FinanceInnSettlementInfo financeInnSettlementInfo) {
        this.financeInnSettlementInfo = financeInnSettlementInfo;
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

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public Integer getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(Integer totalOrder) {
        this.totalOrder = totalOrder;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getFqSettlementAmount() {
        return fqSettlementAmount;
    }

    public void setFqSettlementAmount(BigDecimal fqSettlementAmount) {
        this.fqSettlementAmount = fqSettlementAmount;
    }

    public BigDecimal getInnSettlementAmount() {
        return innSettlementAmount;
    }

    public void setInnSettlementAmount(BigDecimal innSettlementAmount) {
        this.innSettlementAmount = innSettlementAmount;
    }

    public boolean getIsMatch() {
        return isMatch;
    }

    public void setIsMatch(boolean isMatch) {
        this.isMatch = isMatch;
    }

    public Integer getRoomNights() {
        return roomNights;
    }

    public void setRoomNights(Integer roomNights) {
        this.roomNights = roomNights;
    }

    public BigDecimal getRealPayment() {
        return realPayment;
    }

    public void setRealPayment(BigDecimal realPayment) {
        this.realPayment = realPayment;
    }

    public String getPaymentRemark() {
        return paymentRemark;
    }

    public void setPaymentRemark(String paymentRemark) {
        this.paymentRemark = paymentRemark;
    }

    public BigDecimal getChannelSettlementAmount() {
        return channelSettlementAmount;
    }

    public void setChannelSettlementAmount(BigDecimal channelSettlementAmount) {
        this.channelSettlementAmount = channelSettlementAmount;
    }

    public BigDecimal getFqBearAmount() {
        return fqBearAmount;
    }

    public void setFqBearAmount(BigDecimal fqBearAmount) {
        this.fqBearAmount = fqBearAmount;
    }

    public BigDecimal getFqIncomeAmount() {
        return fqIncomeAmount;
    }

    public void setFqIncomeAmount(BigDecimal fqIncomeAmount) {
        this.fqIncomeAmount = fqIncomeAmount;
    }

    public BigDecimal getFqRefundCommissionAmount() {
        return fqRefundCommissionAmount;
    }

    public void setFqRefundCommissionAmount(BigDecimal fqRefundCommissionAmount) {
        this.fqRefundCommissionAmount = fqRefundCommissionAmount;
    }

    public BigDecimal getCurFqRefundContacts() {
        return curFqRefundContacts;
    }

    public void setCurFqRefundContacts(BigDecimal curFqRefundContacts) {
        this.curFqRefundContacts = curFqRefundContacts;
    }

    public BigDecimal getAftFqRefundContacts() {
        return aftFqRefundContacts;
    }

    public void setAftFqRefundContacts(BigDecimal aftFqRefundContacts) {
        this.aftFqRefundContacts = aftFqRefundContacts;
    }

    public BigDecimal getChannelRealSettlementAmount() {
        return channelRealSettlementAmount;
    }

    public void setChannelRealSettlementAmount(BigDecimal channelRealSettlementAmount) {
        this.channelRealSettlementAmount = channelRealSettlementAmount;
    }

    public BigDecimal getFqNormalIncome() {
        return fqNormalIncome;
    }

    public void setFqNormalIncome(BigDecimal fqNormalIncome) {
        this.fqNormalIncome = fqNormalIncome;
    }
}
