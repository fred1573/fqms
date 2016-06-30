package com.project.dao.finance;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceInnSettlementInfo;
import org.springframework.stereotype.Component;

/**
 * 客栈结算基本信息对象
 * Created by sam on 2016/1/13.
 */
@Component("financeInnSettlementInfoDao")
public class FinanceInnSettlementInfoDao extends HibernateDao<FinanceInnSettlementInfo, Integer> {
    public FinanceInnSettlementInfo financeInnSettlementInfoWithId(Integer innId) {
        return findUniqueWithSql("select * from finance_inn_settlement_info where id=?", innId);
    }

    /**
     * 根据客栈ID和客栈名称修改结算客栈的名称
     *
     * @param innId
     * @param innName
     */
    public void updateFinanceInnSettlementInfo(Integer innId, String innName) {
        executeUpdateWithSql("UPDATE finance_inn_settlement_info SET inn_name='" + innName + "' WHERE id='" + innId + "'");
    }
}
