package com.project.bean.finance;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

/**
 * 客栈未结算账期结算信息
 * Created by sam on 2016/4/15.
 */
public class InnUnSettlementInfo {
    // 应结金额，未结算且客栈结算金额大于或等于0的账期
    private BigDecimal incomeSettlementAmount;
    // 欠款金额，未结算且客栈结算金额小于0的账期
    private BigDecimal arrearsSettlementAmount;
    // 总计应结算金额，应结金额-欠款金额
    private BigDecimal totalSettlementAmount;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigDecimal getIncomeSettlementAmount() {
        return incomeSettlementAmount;
    }

    public void setIncomeSettlementAmount(BigDecimal incomeSettlementAmount) {
        this.incomeSettlementAmount = incomeSettlementAmount;
    }

    public BigDecimal getArrearsSettlementAmount() {
        return arrearsSettlementAmount;
    }

    public void setArrearsSettlementAmount(BigDecimal arrearsSettlementAmount) {
        this.arrearsSettlementAmount = arrearsSettlementAmount;
    }

    public BigDecimal getTotalSettlementAmount() {
        return totalSettlementAmount;
    }

    public void setTotalSettlementAmount(BigDecimal totalSettlementAmount) {
        this.totalSettlementAmount = totalSettlementAmount;
    }
}
