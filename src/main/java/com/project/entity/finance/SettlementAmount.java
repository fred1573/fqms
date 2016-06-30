package com.project.entity.finance;

import java.math.BigDecimal;

/**
 * Created by admin on 2016/4/20.
 */
public class SettlementAmount {

    private BigDecimal channelAmount;
    private BigDecimal innAmount;

    public BigDecimal getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(BigDecimal channelAmount) {
        this.channelAmount = channelAmount;
    }

    public BigDecimal getInnAmount() {
        return innAmount;
    }

    public void setInnAmount(BigDecimal innAmount) {
        this.innAmount = innAmount;
    }
}
