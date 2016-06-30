package com.project.dao.finance;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceAccountPeriod;
import com.project.utils.CollectionsUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 账期持久化对象
 * Created by sam on 2015/12/30.
 */
@Component("financeAccountPeriodDao")
public class FinanceAccountPeriodDao extends HibernateDao<FinanceAccountPeriod, Integer> {
    /**
     * 按照账期降序查询全部有效的账期对象
     * @return
     */
    public List<FinanceAccountPeriod> selectAllFinanceAccountPeriod() {
        return findWithSql("SELECT * FROM finance_account_period WHERE deleted = FALSE ORDER BY settlement_time DESC");
    }

    /**
     * 根据结算周期查询账期对象
     * @param settlementTime
     * @return
     */
    public FinanceAccountPeriod findFinanceAccountPeriodWithSettlementTime(String settlementTime) {
        String sql = "select * from finance_account_period fap where fap.settlement_time=?";
        return findUniqueWithSql(sql, settlementTime);
    }
    /**
     * 查询结账周期
     * @param page
     * @return
     */
    public Page<FinanceAccountPeriod> selectPeriodListByPage(Page<FinanceAccountPeriod> page) {
        return findPageWithSql(page, "select * from finance_account_period ORDER BY settlement_time DESC");
    }

    /**
     * 根据结账周期更新是否结算账单状态
     * @param settlementTime
     */
    public void updateSettlementStatus(String settlementTime) {
        executeUpdateWithSql("update finance_account_period set version=version+1, date_updated=now() where settlement_time=?", settlementTime);
    }
    /**
     * 根据结账周期更新是否发送账单状态
     * @param settlementTime
     */
    public void updateSendBillStatus(String settlementTime) {
        executeUpdateWithSql("update finance_account_period set version=version+1, date_updated=now(),send_bill_status=true where settlement_time=?", settlementTime);
    }

    /**
     * 获取最近账期
     * @return
     */
    public String selectRecentlyPeriod() {
        List<FinanceAccountPeriod> financeAccountPeriods = findWithSql("SELECT * FROM finance_account_period ORDER BY settlement_time DESC");
        if (CollectionsUtil.isNotEmpty(financeAccountPeriods)) {
            String settlementTime = financeAccountPeriods.get(0).getSettlementTime();
            return settlementTime;
        }
        return null;
    }

    /**
     * 根据指定账期查询在此账期之后的账期集合，包括本账期
     * @return
     */
    public List<String> selectFinanceAccountPeriodListBySettlementTime(String settlementTime) {
        return find("SELECT t.settlementTime FROM FinanceAccountPeriod t WHERE settlementTime>='2016-02-01至2016-02-14' ORDER BY settlementTime");
    }
}
