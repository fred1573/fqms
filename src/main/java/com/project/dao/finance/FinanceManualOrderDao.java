package com.project.dao.finance;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceManualOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author frd
 */
@Repository("financeManualOrderDao")
public class FinanceManualOrderDao extends HibernateDao<FinanceManualOrder, Integer> {

    /**
     * 获取无订单赔付金额总额
     * @param channelId 分销商ID
     * @param settlementTime 账期
     * @return 无订单赔付金额总额
     */
    public BigDecimal getAmount(Integer channelId, String settlementTime) {
        return (BigDecimal) getSession().createSQLQuery("select sum(t.refund) from finance_manual_order t where t.channel=" + channelId + " and t.settlement_time='" + settlementTime + "' and t.available=TRUE group by t.channel")
                .uniqueResult();
    }

    public Map<String, Object> getManualOrderAmount(Integer channelId, String settlementTime, String orderId) {
        StringBuilder sql = new StringBuilder("select count(*) as count, sum(o.refund) as amount from finance_manual_order o where o.channel=" + channelId + " and o.settlement_time='" + settlementTime + "' and o.available=TRUE");
        if(StringUtils.isNoneBlank(orderId)){
            sql.append(" and o.order_id='").append(orderId).append("'");
        }
        return findMapWithSql(sql.toString());
    }

    /**
     * 根据渠道查询订单
     *
     * @return
     */
    public List<FinanceManualOrder> findFinanceManualOrdersWithChannelId(Integer channelId, String settlementTime) {
        String sql = "SELECT * FROM finance_manual_order WHERE channel=? AND settlement_time=?";
        return findWithSql(sql, channelId, settlementTime);
    }

    public Page<FinanceManualOrder> list(Page<FinanceManualOrder> page, Integer channelId, String settlementTime, String orderId) {
        StringBuilder sql = new StringBuilder("select * from finance_manual_order o where o.channel=" + channelId + " and o.available=true and o.settlement_time='" + settlementTime + "'");
        if(StringUtils.isNotBlank(orderId)) {
            sql.append(" and o.order_id='").append(orderId).append("'");
        }
        return findPageWithSql(page, sql.toString());
    }

}
