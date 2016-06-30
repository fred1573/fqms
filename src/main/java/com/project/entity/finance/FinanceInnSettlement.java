package com.project.entity.finance;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 客栈对账对象
 * Created by 番茄桑 on 2015/8/31.
 */
@Entity
@Table(name = "finance_inn_settlement")
public class FinanceInnSettlement {

    // 正常结算状态码
    @Transient
    public static final String NORMAL_STATUS = "normal";
    // 特殊结算状态码
    @Transient
    public static final String SPECIAL_STATUS = "special";
    // 延期结算状态码
    @Transient
    public static final String DELAY_STATUS = "delay";
    // 挂账结算状态码
    @Transient
    public static final String ARREARS_STATUS = "arrears";
    // 挂账结算标示
    @Transient
    public static final String ARREARS_TAG = "3";
    // 平账结算标示
    @Transient
    public static final String LEVEL_ARREARS_TAG = "1";
    // 平账结算状态码
    @Transient
    public static final String LEVEL_ARREARS_STATUS = "level";
    // 部分平账结算标示
    @Transient
    public static final String PARTIAL_ARREARS_TAG = "2";
    // 部分平账结算状态码
    @Transient
    public static final String PARTIAL_ARREARS_STATUS = "partial";


    // 主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 客栈结算基本信息对象
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "inn_id")
    private FinanceInnSettlementInfo financeInnSettlementInfo;
    // 订单总数
    @Column(name = "total_order")
    private int totalOrder;
    // 渠道商结算金额
    @Column(name = "channel_settlement_amount")
    private BigDecimal channelSettlementAmount;
    // 番茄结算金额
    @Column(name = "fq_settlement_amount")
    private BigDecimal fqSettlementAmount;
    // 客栈结算金额
    @Column(name = "inn_settlement_amount")
    private BigDecimal innSettlementAmount;
    // 客栈确认状态，0：未确认，1：已确认，2：系统自动确认
    @Column(name = "confirm_status")
    private String confirmStatus;
    // 结算状态（0:未结算，1:已结算,2:纠纷延期）
    @Column(name = "settlement_status")
    private String settlementStatus;
    // 是否标注
    @Column(name = "is_tagged")
    private boolean isTagged;
    // 结算时间
    @Column(name = "settlement_time")
    private String settlementTime;
    // 账单状态,true:已发送,false:未发送
    @Column(name = "bill_status")
    private boolean billStatus;
    // 客栈订单总金额
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    //间夜数
    @Column(name = "room_nights")
    private Integer roomNights;
    // 统计的实付金额
    @Transient
    private BigDecimal payment;
    // 分销商订单总额
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
    //客栈挂账状态(0,不存在挂账1,平账结算2,部分平账3,挂账)
    @Column(name = "is_arrears")
    private String isArrears;
    //经特殊订单处理后客栈结算金额
    @Column(name = "after_payment_amount")
    private BigDecimal afterPaymentAmount;
    //经挂账处理后的客栈结算金额
    @Column(name = "after_arrears_amount")
    private BigDecimal afterArrearsAmount;
    //往期挂账
    @Column(name = "arrears_past")
    private BigDecimal arrearsPast;
    //剩余挂账
    @Column(name = "arrears_remaining")
    private BigDecimal arrearsRemaining;
    //账实是否相符
    @Column(name="is_match")
    private boolean isMatch;
    @Column(name ="channel_real_settlement")
    //分销商实际结算
    private BigDecimal channelRealSettlement;

    public boolean isMatch() {
        return isMatch;
    }
    public void setIsMatch(boolean isMatch) {
        this.isMatch = isMatch;
    }
    public BigDecimal getChannelRealSettlement() {
        return channelRealSettlement;
    }

    public void setChannelRealSettlement(BigDecimal channelRealSettlement) {
        this.channelRealSettlement = channelRealSettlement;
    }

    public boolean isTagged() {
        return isTagged;
    }

    public BigDecimal getArrearsPast() {
        return arrearsPast;
    }

    public void setArrearsPast(BigDecimal arrearsPast) {
        this.arrearsPast = arrearsPast;
    }

    public BigDecimal getArrearsRemaining() {
        return arrearsRemaining;
    }

    public void setArrearsRemaining(BigDecimal arrearsRemaining) {
        this.arrearsRemaining = arrearsRemaining;
    }

    public BigDecimal getAfterPaymentAmount() {

        return afterPaymentAmount;
    }

    public void setAfterPaymentAmount(BigDecimal afterPaymentAmount) {
        this.afterPaymentAmount = afterPaymentAmount;
    }

    public BigDecimal getAfterArrearsAmount() {
        return afterArrearsAmount;
    }

    public void setAfterArrearsAmount(BigDecimal afterArrearsAmount) {
        this.afterArrearsAmount = afterArrearsAmount;
    }

    public String getIsArrears() {
        return isArrears;
    }

    public void setIsArrears(String isArrears) {
        this.isArrears = isArrears;
    }

    public BigDecimal getFqReplenishment() {
        return fqReplenishment;
    }

    public void setFqReplenishment(BigDecimal fqReplenishment) {
        this.fqReplenishment = fqReplenishment;
    }


    public BigDecimal getInnPayment() {
        return innPayment;
    }

    public void setInnPayment(BigDecimal innPayment) {
        this.innPayment = innPayment;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
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

    public FinanceInnSettlementInfo getFinanceInnSettlementInfo() {
        return financeInnSettlementInfo;
    }

    public void setFinanceInnSettlementInfo(FinanceInnSettlementInfo financeInnSettlementInfo) {
        this.financeInnSettlementInfo = financeInnSettlementInfo;
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

    public String getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(String confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public boolean getIsTagged() {
        return isTagged;
    }

    public void setIsTagged(boolean isTagged) {
        this.isTagged = isTagged;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public boolean getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(boolean billStatus) {
        this.billStatus = billStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getRoomNights() {
        return roomNights;
    }

    public void setRoomNights(Integer roomNights) {
        this.roomNights = roomNights;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }
}
