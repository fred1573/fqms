package com.project.dao.finance;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceArrearInn;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 挂账记录持久化操作对象
 * Created by admin on 2016/3/9.
 */
@Component("financeArrearInnDao")
public class FinanceArrearInnDao extends HibernateDao<FinanceArrearInn, Integer> {
    /**
     * 查找客栈挂账记录
     *
     * @param innId
     * @return
     */
    public List<FinanceArrearInn> findFinanceArrearInn(Integer innId,String settlementTime) {
        String sql = "select * from finance_arrear_inn where inn_id=? and settlement_time <= ?  ORDER BY operate_time DESC";
        return findWithSql(sql, innId,settlementTime);
    }

    /**
     * 查询往期挂账记录
     * @param innId
     * @param settlementTime
     * @return
     */
    public List<FinanceArrearInn> selectPastFinanceArrearInn(Integer innId, String settlementTime) {
        return findWithSql("SELECT * FROM finance_arrear_inn where inn_id=? and settlement_time < ? ORDER BY operate_time DESC", innId, settlementTime);
    }

    /**
     * 根据账期客栈Id查询挂账信息
     *
     * @param settlementTime
     * @param innId
     * @return
     */
    public List<FinanceArrearInn> findFinanceArrearInn(String settlementTime, Integer innId) {
        String sql = "select * from finance_arrear_inn where inn_id=? and settlement_time=?";
        return findWithSql(sql, innId, settlementTime);
    }

    /**
     * 根据账期删除挂账记录
     * @param settlementTime
     */
    public void deleteFinanceArrearsInn(String settlementTime) {
        String sql = "DELETE from finance_arrear_inn WHERE settlement_time=? and (manual_level=false or manual_level IS NULL)";
        executeUpdateWithSql(sql, settlementTime);
    }
}
