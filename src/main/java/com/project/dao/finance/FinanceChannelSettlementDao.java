package com.project.dao.finance;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceChannelSettlement;
import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 渠道结算持久化操作对象
 * Created by 番茄桑 on 2015/9/15.
 */
@Component("financeChannelSettlementDao")
public class FinanceChannelSettlementDao extends HibernateDao<FinanceChannelSettlement, Integer> {

    /**
     * 根据渠道ID和结算时间查询结算对象
     *
     * @param channelId      渠道ID
     * @param settlementTime 结算时间
     * @return
     */
    public FinanceChannelSettlement selectFinanceChannelSettlement(Integer channelId, String settlementTime) {
        List<FinanceChannelSettlement> financeChannelSettlementList = findWithSql("select * from finance_channel_settlement where channel_id='" + channelId + "' and settlement_time='" + settlementTime + "'");
        if (!CollectionsUtil.isEmpty(financeChannelSettlementList)) {
            return financeChannelSettlementList.get(0);
        }
        return null;
    }

    /**
     * 根据结算时间查询全部渠道的结算明细
     *
     * @param settlementTime
     * @return
     */
    public List<FinanceChannelSettlement> selectFinanceChannelSettlement(String settlementTime) {
        return findWithSql("select * from finance_channel_settlement where settlement_time='" + settlementTime + "'");
    }

    /**
     * 分页查询渠道结算列表
     *
     * @param page           分页对象
     * @param settlementTime 结算时间
     * @param channelName    渠道名称（模糊查询）
     * @param auditStatus    核单状态
     * @param isArrival      是否收到款项
     * @param isPage         是否分页
     * @return
     */
    public Page<FinanceChannelSettlement> selectFinanceChannelSettlement(Page<FinanceChannelSettlement> page, String settlementTime, String channelName, String auditStatus, Boolean isArrival, boolean isPage) {
        StringBuilder sqlBuilder = new StringBuilder("select * from finance_channel_settlement where 1=1 ");
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append("and settlement_time='" + settlementTime + "' ");
        }
        if (StringUtils.isNotBlank(channelName)) {
            sqlBuilder.append("and channel_name like '%" + channelName + "%' ");
        }
        if (StringUtils.isNotBlank(auditStatus) && !"-1".equals(auditStatus)) {
            sqlBuilder.append("and audit_status='" + auditStatus + "' ");
        }
        if (isArrival != null) {
            sqlBuilder.append("and is_arrival=" + isArrival);
        }
        sqlBuilder.append(" order by total_order desc");
        return findPageWithSql(isPage, page, sqlBuilder.toString());
    }

    /**
     * 根据结算时间和是否收到款项，统计渠道结算
     *
     * @param settlementTime
     * @param isArrival
     * @return
     */
    public Map<String, Object> getFinanceChannelSettlementCount(String settlementTime, Boolean isArrival) {
        StringBuilder sqlBuilder = new StringBuilder("select COUNT (channel_id) AS channels,\n" +
                "\tSUM (channel_real_amount) AS amounts,\n" +
                "\tSUM (channel_settlement_amount) AS channel_settlement_amount,\n" +
                "\tSUM (channel_debit) AS channel_debit,\n" +
                "\tSUM (current_refund_amount) AS current_refund_amount,\n" +
                "\tSUM (refunded_amount) AS refunded_amount,\n" +
                "\tSUM (next_refund_amount) AS next_refund_amount,\n" +
                "\tSUM (no_order_debit_amount) AS no_order_debit_amount,\n" +
                "\tSUM (income_amount) AS income_amount,\n" +
                "\tSUM (fq_real_income) AS fq_real_income from finance_channel_settlement where 1=1 ");
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append("and settlement_time='").append(settlementTime).append("' ");
        }
        if (isArrival != null) {
            sqlBuilder.append("and is_arrival=").append(isArrival);
        }
        return findMapWithSql(sqlBuilder.toString());
    }

    /**
     * 根据结算月份统计渠道的实收金额
     *
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> selectChannelSettlementIncomeAmount(String settlementTime) {
        StringBuilder sqlBuilder = new StringBuilder("select count(id) as channels,sum(income_amount) as amounts from finance_channel_settlement where is_arrival=true and settlement_time='" + settlementTime + "'");
        return findListMapWithSql(sqlBuilder.toString());
    }

    /**
     * 更新渠道结算月份款项为已收到
     *
     * @param id
     */
    public void updateFinanceChannelSettlementArrival(Integer id) {
        executeUpdateWithSql("UPDATE finance_channel_settlement SET is_arrival=TRUE WHERE id=" + id);
    }

    /**
     * 更新渠道指定对账月份的核单状态
     *
     * @param channelId      渠道ID
     * @param settlementTime 对账月份
     * @param auditStatus    核单状态
     */
    public void updateFinanceChannelAuditStatus(Integer channelId, String settlementTime, String auditStatus) {
        executeUpdateWithSql("UPDATE finance_channel_settlement SET audit_status='" + auditStatus + "' WHERE channel_id='" + channelId + "' and settlement_time='" + settlementTime + "'");
    }

    /**
     * 根据主键ID查询渠道对账信息
     *
     * @param id 主键ID
     * @return
     */
    public FinanceChannelSettlement findFinanceChannelSettlementById(Integer id) {
        return findUnique("select fcs from FinanceChannelSettlement fcs where id=" + id);
    }

    /**
     * 根据渠道结算对象ID更新渠道结算的实收金额以及备注
     *
     * @param id           渠道结算对象ID
     * @param incomeAmount 实收金额
     * @param remarks      备注
     */
    public void updateIncomeAmount(Integer id, BigDecimal incomeAmount, String remarks) {
        executeUpdateWithSql("UPDATE finance_channel_settlement SET income_amount='" + incomeAmount + "',remarks='" + remarks + "' WHERE id = " + id);
    }

    /**
     * 批量删除统计信息中不包含的渠道数据
     *
     * @param settlementTime
     * @param deleteChannelIds
     */
    public void batchDeleteFinanceChannelSettlement(String settlementTime, String deleteChannelIds) {
        executeUpdateWithSql("DELETE from finance_channel_settlement WHERE settlement_time='" + settlementTime + "' and channel_id in (" + deleteChannelIds + ")");
    }

    /**
     * 统计渠道结算相关金额
     * @param settlementTime
     * @return
     */
    public Map<String, Object> statisticChannelAmount(String settlementTime) {
        String sql = "SELECT count(*) AS count,SUM(channel_settlement_amount) as camount,SUM(channel_debit) AS debit,SUM(channel_refund) AS refund,SUM(no_order_debit_amount) AS namount,SUM(channel_real_amount) as ramount, SUM(current_refund_amount) AS cur,SUM(next_refund_amount) AS next,SUM(refunded_amount) AS refunded,SUM(fq_temp) AS fq  from finance_channel_settlement   WHERE settlement_time=?";
        return findMapWithSql(sql, settlementTime);
    }
}
