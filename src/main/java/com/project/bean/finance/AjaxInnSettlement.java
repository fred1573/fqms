package com.project.bean.finance;

import com.project.bean.vo.AjaxBase;
import com.project.entity.finance.FinanceInnSettlement;

import java.util.List;

/**
 * 客栈结算列表对象
 * Created by 番茄桑 on 2015/9/19.
 */
public class AjaxInnSettlement extends AjaxBase {
    // 客栈结算对象集合
    private List<FinanceInnSettlement> list;

    private long totalCount;

    public AjaxInnSettlement(int status) {
        super(status);
    }

    public AjaxInnSettlement(int status, String message) {
        super(status, message);
    }

    public AjaxInnSettlement(int status, String message, List<FinanceInnSettlement> list, long totalCount) {
        super(status, message);
        this.list = list;
        this.totalCount = totalCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<FinanceInnSettlement> getList() {
        return list;
    }

    public void setList(List<FinanceInnSettlement> list) {
        this.list = list;
    }
}
