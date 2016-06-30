package com.project.bean.finance;

import com.project.bean.vo.AjaxBase;
import com.project.entity.finance.FinanceParentOrder;

import java.util.List;

/**
 * 客栈订单详情对象
 * Created by 番茄桑 on 2015/9/19.
 */
public class AjaxInnOrder extends AjaxBase {
    // 客栈结算对象集合
    private List<FinanceParentOrder> list;

    private long totalCount;

    public AjaxInnOrder(int status) {
        super(status);
    }

    public AjaxInnOrder(int status, String message) {
        super(status, message);
    }

    public AjaxInnOrder(int status, String message, List<FinanceParentOrder> list, long totalCount) {
        super(status, message);
        this.list = list;
        this.totalCount = totalCount;
    }

    public List<FinanceParentOrder> getList() {
        return list;
    }

    public void setList(List<FinanceParentOrder> list) {
        this.list = list;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
