package com.project.dao.finance;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceSpecialOrder;
import com.project.utils.CollectionsUtil;
import com.project.utils.FinanceHelper;
import com.project.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2016/3/10.
 */
@Component("financeSpecialOrderDao")
public class FinanceSpecialOrderDao extends HibernateDao<FinanceSpecialOrder, Integer> {
    /**
     * 分页查询特殊账单
     *
     * @param page
     * @param statusKey      特殊订单状态key
     * @param innId          客栈ID（PMS客栈ID）
     * @param channelId      分销商ID
     * @param settlementTime 账期
     * @param orderNo        订单号，模糊匹配分销商订单号和OMS订单号
     * @param contactsStatus 往来状态
     * @param auditStatus    核单状态
     * @param priceStrategy  价格模式
     * @param isBalance      是否结算
     * @param keyWord        搜索关键字，模糊匹配客栈名称、订单号，精确匹配PMS客栈名称
     * @return
     */
    public Page<FinanceSpecialOrder> selectSpecialOrderList(Page<FinanceSpecialOrder> page, String statusKey, Integer innId, Integer channelId, String settlementTime, String orderNo, String contactsStatus, String auditStatus, Short priceStrategy, Short isBalance, String keyWord) {
        StringBuilder sql = new StringBuilder("SELECT * FROM finance_special_order t1 LEFT JOIN finance_parent_order t2 ON t1.order_id = t2. ID WHERE 1=1");
        if (StringUtils.isNotBlank(statusKey)) {
            sql.append(" AND t2.status = '" + FinanceHelper.getStatusByKey(statusKey) + "'");
        }
        if (innId != null) {
            sql.append(" AND t2.inn_id = '" + innId + "'");
        }
        if (channelId != null) {
            sql.append(" AND t2.channel_id = '" + channelId + "'");
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sql.append(" AND t2.settlement_time = '" + settlementTime + "'");
        }
        if (StringUtil.isNotNull(orderNo)) {
            sql.append(" AND (t2.channel_order_no like '%" + orderNo + "%' OR t2.order_no like '%" + orderNo + "%') ");
        }
        if (StringUtils.isNotBlank(contactsStatus)) {
            sql.append(" AND t1.contacts_status='" + contactsStatus + "'");
        }
        if (StringUtils.isNotBlank(auditStatus)) {
            sql.append(" AND t2.audit_status = '" + auditStatus + "'");
        }
        if (priceStrategy != null) {
            sql.append(" AND t2.price_strategy = '" + priceStrategy + "'");
        }
        if (isBalance != null) {
            sql.append(" AND t2.is_balance = '" + isBalance + "'");
        }
        if (StringUtils.isNotBlank(keyWord)) {
            sql.append(" AND (t2.inn_name like '%" + keyWord + "%' OR t2.channel_order_no like '%" + keyWord + "%'");
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(keyWord);
            // 如果谁纯数字查询客栈ID
            if (isNum.matches() && keyWord.length() < 10) {
                sql.append(" OR t2.inn_id = '" + keyWord + "'");
            }
            sql.append(")");
        }
        return findPageWithSql(page, sql.toString());
    }

    public Page<FinanceSpecialOrder> selectSpecialOrderList(Page<FinanceSpecialOrder> page, String statusKey, Integer channelId, String settlementTime, String auditStatus, Short priceStrategy, Short isBalance, String keyWord) {
        return selectSpecialOrderList(page, statusKey, null, channelId, settlementTime, null, null, auditStatus, priceStrategy, isBalance, keyWord);
    }

    public Page<FinanceSpecialOrder> findFinanceSpecialOrder(Page<FinanceSpecialOrder> page, String statusKey, Integer innId, Integer channelId, String settlementTime, String orderNo, String auditStatus, Short priceStrategy) {
        return selectSpecialOrderList(page, statusKey, innId, channelId, settlementTime, orderNo, null, auditStatus, priceStrategy, null, null);
    }

    public Page<FinanceSpecialOrder> findFinanceSpecialOrder(Page<FinanceSpecialOrder> page, Integer innId, Integer channelId, String settlementTime, String orderNo, String status, String contactsStatus) {
        return selectSpecialOrderList(page, status, innId, channelId, settlementTime, orderNo, contactsStatus, null, null, null, null);
    }

    /**
     * 根据父订单ID查询特殊订单对象，如果存在多条 只返回最新的特殊订单对象
     *
     * @param orderId
     * @return
     */
    public FinanceSpecialOrder selectFinanceSpecialOrderByOrderId(String orderId) {
        List<FinanceSpecialOrder> financeSpecialOrderList = findWithSql("SELECT * FROM finance_special_order WHERE order_id ='" + orderId + "' ORDER BY id DESC LIMIT 1");
        if (CollectionsUtil.isNotEmpty(financeSpecialOrderList)) {
            return financeSpecialOrderList.get(0);
        }
        return null;
    }

    /**
     * 赔付订单统计
     *
     * @return
     */
    public Map<String, Object> debitOrderStatistic(String settlementTime, Integer channelId, Integer innId) {
        String sql = new StringBuilder("select count(t2.id) as order_num, SUM (t2.channel_debit) as channel_debits, SUM (t2.inn_payment) as inn_payments, SUM (t2.fq_bear) as fq_bears, SUM (t2.fq_income) as fq_incomes from finance_parent_order t1 right join finance_special_order t2 on t1.id=t2.order_id where t1.status='")
                .append(FinanceSpecialOrder.DEBIT_STATUS)
                .append("' and t1.settlement_time='")
                .append(settlementTime)
                .append("'and t1.channel_id=")
                .append(channelId)
                .append(" and t1.inn_id=")
                .append(innId).toString();
        return findMapWithSql(sql);
    }

    /**
     * 退款订单统计
     *
     * @param settlementTime
     * @param channelId
     * @param innId
     * @return
     */
    public Map<String, Object> refundOrderStatistic(String settlementTime, Integer channelId, Integer innId) {
        String sql = new StringBuilder("select count(t2.id) as order_num, SUM (t2.channel_refund) as channel_refunds, SUM (t2.inn_refund) as inn_refunds, SUM (t2.fq_refund_commission) as fq_refund_commissions, SUM (t2.fq_refund_contacts) as fq_refund_contactss from finance_parent_order t1 right join finance_special_order t2 on t1.id=t2.order_id where  t1.status='")
                .append(FinanceSpecialOrder.REFUND_STATUS)
                .append("' and t1.settlement_time='")
                .append(settlementTime)
                .append("'and t1.channel_id=")
                .append(channelId)
                .append(" and t1.inn_id=")
                .append(innId).toString();
        return findMapWithSql(sql);
    }


    public Map<String, Object> replenishmentOrderStatistic(String settlementTime, Integer channelId, Integer innId) {
        String sql = new StringBuilder("select count(t2.id) as order_num, SUM (t1.channel_settlement_amount) as channel_settlement_amounts, SUM (t1.inn_settlement_amount) as inn_settlement_amounts, SUM (t2.fq_replenishment) as fq_replenishments from finance_parent_order t1 right join finance_special_order t2 on t1.id=t2.order_id where  t1.status='")
                .append(FinanceSpecialOrder.REPLENISHMENT_STATUS)
                .append("' and t1.settlement_time='")
                .append(settlementTime)
                .append("'and t1.channel_id=")
                .append(channelId)
                .append(" and t1.inn_id=")
                .append(innId).toString();
        return findMapWithSql(sql);
    }

    /**
     * 根据主键ID查询特殊账单对象
     *
     * @param id
     * @return
     */
    public FinanceSpecialOrder selectFinanceSpecialOrderById(Integer id) {
        List<FinanceSpecialOrder> financeSpecialOrderList = findWithSql("SELECT * FROM finance_special_order WHERE id ='" + id + "'");
        if (CollectionsUtil.isNotEmpty(financeSpecialOrderList)) {
            return financeSpecialOrderList.get(0);
        }
        return null;
    }

    /**
     * 按ID删除
     *
     * @param id
     */
    public void delete(String id) {
        String sql = "DELETE FROM finance_special_order WHERE order_id=?";
        executeUpdateWithSql(sql,id);
    }

    /**
     * 查询给客栈老板展示的特殊账单
     * @param page
     * @param status
     * @param innId
     * @param settlementTime
     * @param priceStrategy
     * @param isPage
     * @return
     */
    public Page<FinanceSpecialOrder> selectSpecialBillList(Page<FinanceSpecialOrder> page, String status, Integer innId, String settlementTime, Short priceStrategy, boolean isPage) {
        StringBuilder sql = new StringBuilder("SELECT * FROM finance_special_order t1 LEFT JOIN finance_parent_order t2 ON t1.order_id = t2. ID WHERE 1=1");
        if (StringUtils.isNotBlank(status)) {
            sql.append(" AND t2.status = '" + status+ "'");
        }
        // 退款订单中不与客栈结算的账单不展示给客栈老板
        if(FinanceSpecialOrder.REFUND_STATUS.equals(status)) {
            sql.append(" AND t1.inn_settlement = true");
        }
        if (innId != null) {
            sql.append(" AND t2.inn_id = '" + innId + "'");
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sql.append(" AND t2.settlement_time = '" + settlementTime + "'");
        }
        if (priceStrategy != null) {
            String priceStrategyStr = "'1'";
            if(priceStrategy == 2) {
                priceStrategyStr = "'2','3'";
            }
            sql.append(" AND t2.price_strategy in (" + priceStrategyStr + ")");
        }
        sql.append(" order by t2.order_time");
        return findPageWithSql(isPage, page, sql.toString());
    }
}
