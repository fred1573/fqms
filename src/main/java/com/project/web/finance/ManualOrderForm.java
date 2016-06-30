package com.project.web.finance;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author frd
 */
public class ManualOrderForm {

    @NotNull(message = "分销商ID不能为空")
    private Integer channelId;

    @NotNull(message = "账期不能为空")
    private String settlementTime;

    @NotNull(message = "订单号不能为空")
    private String orderId;

    @NotNull(message = "备注不能为空")
    private String remark;

    @NotNull(message = "扣款金额不能为空")
    private BigDecimal refund;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getRefund() {
        return refund;
    }

    public void setRefund(BigDecimal refund) {
        this.refund = refund;
    }
}
