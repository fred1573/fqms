package com.project.entity.finance;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by admin on 2016/3/10.
 */
@Entity
@Table(name = "finance_special_order")
public class FinanceSpecialOrder {
    // 特殊账单的状态集合
    @Transient
    public static final String[] SPECIAL_STATUS = {"66", "77", "88"};
    // 赔付状态码
    @Transient
    public static final String DEBIT_STATUS = "66";
    // 退款状态码
    @Transient
    public static final String REFUND_STATUS = "77";
    // 补款状态码
    @Transient
    public static final String REPLENISHMENT_STATUS = "88";

    // 赔付状态标识
    @Transient
    public static final String STATUS_KEY_DEBIT = "debit";

    // 退款状态标识
    @Transient
    public static final String STATUS_KEY_REFUND = "refund";

    // 补款状态标识
    @Transient
    public static final String STATUS_KEY_REPLENISHMENT = "replenishment";
    // 往来状态，本期(平)
    @Transient
    public static final Short CONTACTS_STATUS_THIS = 2;
    // 往来状态，后期（挂）
    @Transient
    public static final Short CONTACTS_STATUS_PAST = 1;

    //主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 分销商扣赔付金额(赔付)
    @Column(name = "channel_debit")
    private BigDecimal channelDebit;
    // 客栈赔付金额(赔付)
    @Column(name = "inn_payment")
    private BigDecimal innPayment;
    // 客栈赔付番茄承担(赔付)
    @Column(name = "fq_bear")
    private BigDecimal fqBear;
    // 客栈赔付番茄收入(赔付)
    @Column(name = "fq_income")
    private BigDecimal fqIncome;

    // 分销商扣退款金额(退款)
    @Column(name = "channel_refund")
    private BigDecimal channelRefund;
    // 客栈退款金额(退款)
    @Column(name = "inn_refund")
    private BigDecimal innRefund;
    // 番茄退佣金收入(退款)
    @Column(name = "fq_refund_commission")
    private BigDecimal fqRefundCommission;
    // 番茄退佣金收入展示字段
    @Transient
    private String fqRefundCommissionStr;
    // 往来状态(1,后期(挂) 2，本期(平))
    @Column(name = "contacts_status")
    private Short contactsStatus;
    // 往来状态展示字段
    @Transient
    private String contactsStatusStr;
    // 番茄退往来款(退款)
    @Column(name = "fq_refund_contacts")
    private BigDecimal fqRefundContacts;
    // 番茄退往来款(退款)展示字段
    @Transient
    private String fqRefundContactsStr;
    // 是否与客栈结算
    @Column(name = "inn_settlement")
    private boolean innSettlement;

    // 番茄补款金额(补款)
    @Column(name = "fq_replenishment")
    private BigDecimal fqReplenishment;

    // 关联父订单
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private FinanceParentOrder financeParentOrder;

    public String getFqRefundCommissionStr() {
        String temp = "--";
        boolean innSettlement = this.getInnSettlement();
        if(innSettlement) {
            temp = String.valueOf(this.getFqRefundCommission());
        }
        return temp;
    }

    public void setFqRefundCommissionStr(String fqRefundCommissionStr) {
        this.fqRefundCommissionStr = fqRefundCommissionStr;
    }

    public String getFqRefundContactsStr() {
        String temp = "--";
        boolean innSettlement = this.getInnSettlement();
        if(!innSettlement) {
            temp = String.valueOf(this.getFqRefundContacts());
        }
        return temp;
    }

    public void setFqRefundContactsStr(String fqRefundContactsStr) {
        this.fqRefundContactsStr = fqRefundContactsStr;
    }

    public boolean getInnSettlement() {
        return innSettlement;
    }

    public void setInnSettlement(boolean innSettlement) {
        this.innSettlement = innSettlement;
    }

    public String getContactsStatusStr() {
        String temp = "--";
        boolean innSettlement = this.getInnSettlement();
        if(!innSettlement) {
            Short contactsStatus = this.getContactsStatus();
            if (contactsStatus != null) {
                if(contactsStatus == 1) {
                    temp = "后期(挂)";
                } else if(contactsStatus == 2) {
                    temp = "本期(平)";
                } else {
                    temp = "异常状态";
                }
            }
        }
        return temp;
    }

    public void setContactsStatusStr(String contactsStatusStr) {
        this.contactsStatusStr = contactsStatusStr;
    }

    public FinanceParentOrder getFinanceParentOrder() {
        return financeParentOrder;
    }

    public void setFinanceParentOrder(FinanceParentOrder financeParentOrder) {
        this.financeParentOrder = financeParentOrder;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getChannelDebit() {
        return channelDebit;
    }

    public void setChannelDebit(BigDecimal channelDebit) {
        this.channelDebit = channelDebit;
    }

    public BigDecimal getInnPayment() {
        return innPayment;
    }

    public void setInnPayment(BigDecimal innPayment) {
        this.innPayment = innPayment;
    }

    public BigDecimal getFqBear() {
        return fqBear;
    }

    public void setFqBear(BigDecimal fqBear) {
        this.fqBear = fqBear;
    }

    public BigDecimal getFqIncome() {
        return fqIncome;
    }

    public void setFqIncome(BigDecimal fqIncome) {
        this.fqIncome = fqIncome;
    }

    public BigDecimal getChannelRefund() {
        return channelRefund;
    }

    public void setChannelRefund(BigDecimal channelRefund) {
        this.channelRefund = channelRefund;
    }

    public BigDecimal getInnRefund() {
        return innRefund;
    }

    public void setInnRefund(BigDecimal innRefund) {
        this.innRefund = innRefund;
    }

    public BigDecimal getFqRefundCommission() {
        return fqRefundCommission;
    }

    public void setFqRefundCommission(BigDecimal fqRefundCommission) {
        this.fqRefundCommission = fqRefundCommission;
    }

    public Short getContactsStatus() {
        return contactsStatus;
    }

    public void setContactsStatus(Short contactsStatus) {
        this.contactsStatus = contactsStatus;
    }

    public BigDecimal getFqRefundContacts() {
        return fqRefundContacts;
    }

    public void setFqRefundContacts(BigDecimal fqRefundContacts) {
        this.fqRefundContacts = fqRefundContacts;
    }

    public BigDecimal getFqReplenishment() {
        return fqReplenishment;
    }

    public void setFqReplenishment(BigDecimal fqReplenishment) {
        this.fqReplenishment = fqReplenishment;
    }
}
