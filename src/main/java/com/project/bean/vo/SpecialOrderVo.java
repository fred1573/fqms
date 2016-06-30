package com.project.bean.vo;

import com.project.entity.finance.FinanceParentOrder;
import com.project.entity.finance.FinanceSpecialOrder;

/**
 * @author frd
 */
public class SpecialOrderVo {

    private FinanceSpecialOrder financeSpecialOrder;
    private FinanceParentOrder financeParentOrder;

    public FinanceSpecialOrder getFinanceSpecialOrder() {
        return financeSpecialOrder;
    }

    public void setFinanceSpecialOrder(FinanceSpecialOrder financeSpecialOrder) {
        this.financeSpecialOrder = financeSpecialOrder;
    }

    public FinanceParentOrder getFinanceParentOrder() {
        return financeParentOrder;
    }

    public void setFinanceParentOrder(FinanceParentOrder financeParentOrder) {
        this.financeParentOrder = financeParentOrder;
    }
}
