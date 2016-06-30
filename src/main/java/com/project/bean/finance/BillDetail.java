package com.project.bean.finance;

import java.util.List;

/**
 * 代销客栈账单明细VO对象
 * Created by sam on 2016/4/15.
 */
public class BillDetail {
    // 普通账单集合，包括补款状态的账单
    private List<ApiParentOrder> normalBillList;
    // 赔付账单集合
    private List<ApiParentOrder> debitBillList;
    // 退款账单集合
    private List<ApiParentOrder> refundBillList;

    public List<ApiParentOrder> getNormalBillList() {
        return normalBillList;
    }

    public void setNormalBillList(List<ApiParentOrder> normalBillList) {
        this.normalBillList = normalBillList;
    }

    public List<ApiParentOrder> getDebitBillList() {
        return debitBillList;
    }

    public void setDebitBillList(List<ApiParentOrder> debitBillList) {
        this.debitBillList = debitBillList;
    }

    public List<ApiParentOrder> getRefundBillList() {
        return refundBillList;
    }

    public void setRefundBillList(List<ApiParentOrder> refundBillList) {
        this.refundBillList = refundBillList;
    }
}
