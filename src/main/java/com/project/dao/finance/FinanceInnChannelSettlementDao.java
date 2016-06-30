package com.project.dao.finance;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceInnChannelSettlement;
import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/1/13.
 */
@Component("financeInnChannelSettlementDao")
public class FinanceInnChannelSettlementDao extends HibernateDao<FinanceInnChannelSettlement, Integer> {
    /**
     * 根据结算月份和渠道查询全部客栈结算记录
     *
     * @param settlementTime
     * @return
     */
    public List<FinanceInnChannelSettlement> selectFinanceInnChannelSettlementBySettlementTime(String settlementTime) {
        return findWithSql("select * from finance_inn_channel_settlement where settlement_time='" + settlementTime + "'");
    }

    /**
     * 批量删除统计信息中不包含的客栈数据
     *
     * @param ids 主键ID集合
     */
    public void batchDeleteFinanceInnChannelSettlement(String ids) {
        executeUpdateWithSql("DELETE from finance_inn_channel_settlement WHERE id in (" + ids + ")");
    }

    /**
     * 根据客栈ID,结算时间,渠道查询结算对象是否存在
     *
     * @param innId
     * @param settlementTime
     * @return
     */
    public FinanceInnChannelSettlement selectFinanceInnChannelSettlement(Integer innId, String settlementTime, Integer channelId) {
        List<FinanceInnChannelSettlement> financeInnSettlementList = findWithSql("select * from finance_inn_channel_settlement where inn_id='" + innId + "'and channel_id=" + channelId + " and settlement_time='" + settlementTime + "'");
        if (!CollectionsUtil.isEmpty(financeInnSettlementList)) {
            return financeInnSettlementList.get(0);
        }
        return null;
    }

    /**
     * 根据账期和渠道查询客栈总数，总金额等
     *
     * @param settlementTime
     * @param channelId
     * @return
     */
    public List<Map<String, Object>> findInnChannelSettlementStatus(String settlementTime, Integer channelId) {
        String sql = "select COUNT(ID) AS ids,SUM(total_order) as orders,SUM(total_amount) as amounts,SUM(real_payment) as inns from finance_inn_channel_settlement WHERE settlement_time='" + settlementTime + "' AND channel_id=" + channelId;
        return findListMapWithSql(sql);
    }


    /**
     * 一键填充客栈实付金额
     *
     * @param settlementTime
     * @param channel
     */
    public void fillRealPay(String settlementTime, Integer channel) {
        String sql = "UPDATE finance_inn_channel_settlement set real_payment=inn_settlement_amount,is_match=true where settlement_time=? and channel_id=? and real_payment IS NULL";
        executeUpdateWithSql(sql, settlementTime, channel);
    }

    /**
     * 一键填充客栈所有渠道实付金额
     *
     * @param settlementTime
     * @param innId
     */
    public void fillInnChannelRealPay(String settlementTime, Integer innId) {

        String sql = "UPDATE finance_inn_channel_settlement\n" +
                "SET real_payment = COALESCE(inn_settlement_amount,0.0)-COALESCE(inn_payment,0.0)-COALESCE(refund_amount,0.0)+COALESCE(fq_replenishment,0.0),\n" +
                " is_match = TRUE\n" +
                "WHERE\n" +
                "\tsettlement_time =?" +
                " AND inn_id =?\n";
        executeUpdateWithSql(sql, settlementTime, innId);
    }

    /**
     * 设置客栈实付金额
     *
     * @param realPayment
     * @param paymentRemark
     * @param id
     */
    public void updateRealPay(BigDecimal realPayment, String paymentRemark, Integer id, boolean isMatch) {
        String sql = "UPDATE finance_inn_channel_settlement SET real_payment=?, payment_remark=?,is_match=? where id=?";
        executeUpdateWithSql(sql, realPayment, paymentRemark, isMatch, id);
    }

    /**
     * 根据ID查找客栈
     *
     * @param id
     * @return
     */
    public FinanceInnChannelSettlement findFinanceInnChannelSettlementById(Integer id) {
        return findUniqueWithSql("SELECT * from finance_inn_channel_settlement fics WHERE fics.id=?", id);
    }

    /**
     * 根据渠道和账期，统计客栈
     *
     * @param settlementTime
     * @param channelId
     * @return
     */
    public List<Map<String, Object>> selectFinanceInnChannelSettlementCount(String settlementTime, Integer channelId) {
        StringBuilder sqlBuilder = new StringBuilder("select count(id) as innCount, sum(total_order) as orders,sum(channel_settlement_amount) as channels,sum(fq_settlement_amount) as fqs,sum(inn_settlement_amount) as inns,sum(total_amount) as total,sum(real_payment) as pay ,sum(channel_amount) as channelamount from finance_inn_channel_settlement where 1=1");
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (channelId != null) {
            sqlBuilder.append(" and channel_id='" + channelId + "'");
        }
        return findListMapWithSql(sqlBuilder.toString());
    }


    /**
     * 按条件查询
     *
     * @param page
     * @param settlementTime
     * @param channelId
     * @param innName
     * @return
     */
    public Page<FinanceInnChannelSettlement> financeInnChannelSettlementWithRequire(Page<FinanceInnChannelSettlement> page, String settlementTime, Integer channelId, String innName, Boolean isMatch) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT t1.*,t2.* FROM finance_inn_channel_settlement t1 INNER JOIN finance_inn_settlement_info t2 ON t1.inn_id = t2. ID where 1=1");
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and t1.settlement_time='" + settlementTime + "' ");
        }
        if (StringUtils.isNotBlank(innName)) {
            sqlBuilder.append(" and t2.inn_name like '%" + innName + "%' ");
        }

        if (channelId != null) {
            sqlBuilder.append(" and t1.channel_id=" + channelId);
        }
        if (isMatch != null) {
            sqlBuilder.append(" and t1.is_match=" + isMatch);
        }
        sqlBuilder.append(" order by total_order desc, inn_id asc");
        return findPageWithSql(page, sqlBuilder.toString());
    }

    /**
     * 根据客栈ID、账期和账实是否相符查询实付金额总和
     *
     * @param innId
     * @param settlementTime
     * @param isMatch
     * @return
     */
    public List<Map<String, Object>> selectTotalPayment(Integer innId, String settlementTime, Boolean isMatch) {
        StringBuilder sqlBuilder = new StringBuilder("select sum(real_payment) as payment from finance_inn_channel_settlement where 1=1");
        if (innId != null) {
            sqlBuilder.append(" and inn_id = '" + innId + "'");
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time = '" + settlementTime + "'");
        }
        if (isMatch != null) {
            sqlBuilder.append(" and is_match = " + isMatch);
        }
        return findListMapWithSql(sqlBuilder.toString());
    }

    /**
     * 按渠道统计客栈订单信息
     *
     * @return
     */
    public List<FinanceInnChannelSettlement> selectChannelOrder(Integer innId, String settlementTime, Boolean isMatch) {
        StringBuilder stringBuilder = new StringBuilder("SELECT * FROM finance_inn_channel_settlement WHERE inn_id=? and settlement_time=?");
        if (null != isMatch) {
            stringBuilder.append(" and is_match=" + isMatch);
        }
        return findWithSql(stringBuilder.toString(), innId, settlementTime);
    }

    /**
     * 统计客栈订单信息
     *
     * @param innId
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> statisticChannelOrderTotal(Integer innId, String settlementTime) {
        String sql = "SELECT COUNT(*) as count ,SUM(total_order) AS to,SUM(total_amount) AS ta,SUM(channel_amount) AS ca,SUM(channel_real_settlement_amount) AS csa,SUM(fq_settlement_amount) AS fsa,SUM(inn_real_settlement)AS isa,SUM(real_payment) AS rp FROM finance_inn_channel_settlement WHERE inn_id =? AND settlement_time=?";
        return findListMapWithSql(sql, innId, settlementTime);
    }

    /**
     * 统计挂账下的渠道结算信息
     *
     * @return
     */
    public Map<String, Object> statisticArrearsChannel(String settlementTime, Integer innId, Boolean isMatch) {
        StringBuilder stringBuilder = new StringBuilder("SELECT SUM(total_order) as orders,SUM(channel_settlement_amount) AS channel,SUM(inn_settlement_amount) as inns FROM finance_inn_channel_settlement WHERE settlement_time=? AND inn_id=?");
        if (null != isMatch) {
            stringBuilder.append(" and is_match=" + isMatch);
        }
        return findMapWithSql(stringBuilder.toString(), settlementTime, innId);
    }

    /**
     * 根据客栈Id和结算时间查询渠道客栈信息
     *
     * @param id
     * @param settlementTime
     * @return
     */
    public List<FinanceInnChannelSettlement> findFinanceInnChannelSettlement(Integer id, String settlementTime) {
        String sql = "select * from finance_inn_channel_settlement where inn_id=? and settlement_time=?";
        return findWithSql(sql, id, settlementTime);
    }

    /**
     * 进账统计渠道客栈结算金额
     *
     * @return
     */
    public Map<String, Object> statisticChannelInnAmount(Integer channelId, String settlementTime) {
        String sql = "SELECT COUNT(id) AS inncount,SUM(channel_settlement_amount) AS channel ,SUM(inn_settlement_amount) AS inns ,SUM(inn_payment) as payment,SUM(fq_bear_amount) AS bear,SUM(fq_income_amount) AS income,SUM(refund_amount) AS refund,SUM(fq_refund_commission_amount) AS fqrefund,SUM(cur_fq_refund_contracts_amount) AS currefund,SUM(aft_fq_refund_contracts_amount)as aftrefund,SUM(channel_real_settlement_amount) as real,sum(fq_normal_income) AS normal FROM finance_inn_channel_settlement WHERE channel_id=? and settlement_time=?";
        return findMapWithSql(sql, channelId, settlementTime);
    }

    /**
     * 按渠道获取客栈信息
     */
    public List<FinanceInnChannelSettlement> findFinanceInnChannelSettlements(Integer channelId, String settlementTime) {
        String sql = "SELECT * from finance_inn_channel_settlement WHERE  channel_id=? and settlement_time=?";
        return findWithSql(sql, channelId, settlementTime);
    }

    /**
     * 按账期删除记录
     *
     * @param settlementTime
     */
    public void deleteWithSettlementTime(String settlementTime) {
        String sql = "DELETE from finance_inn_channel_settlement where settlement_time=?";
        executeUpdateWithSql(sql, settlementTime);
    }

    /**
     * 根据渠道Id获取渠道名称
     *
     * @return
     */
    public FinanceInnChannelSettlement findChannelNameWithChannelId(Integer channelId, String settlementTime) {
        String sql = "SELECT * FROM finance_inn_channel_settlement WHERE channel_id=? and settlement_time=?";
        List<FinanceInnChannelSettlement> list = findWithSql(sql, channelId, settlementTime);
        if (CollectionsUtil.isNotEmpty(list)) {
            return findWithSql(sql, channelId, settlementTime).get(0);
        } else {
            return null;
        }
    }

    /**
     * 按渠道获取补款明细
     *
     * @return
     */
    public List<Map<String, Object>> getRefundDetail(String settlementTime) {
        String sql = "SELECT t.channel_id as id, SUM(t.cur_fq_refund_contracts_amount) AS cur,SUM(t.aft_fq_refund_contracts_amount) AS next,SUM(t.refund_amount+t.fq_refund_commission_amount) AS refunded from finance_inn_channel_settlement t WHERE settlement_time=? GROUP BY t.channel_id";
        return findListMapWithSql(sql, settlementTime);
    }

    /**
     * 指定渠道下的客栈暂收金额详情
     *
     * @param channelId
     * @param settlementTime
     * @return
     */
    public Page<Map<String,Object>> findFqTempInn(Page<Map<String,Object>> page, Integer channelId, String settlementTime) {
        String sql = "SELECT sum(t1.total_amount)AS total,count(t1.id) AS count,SUM(t1.channel_settlement_amount)AS channel ,SUM(t1.fq_temp) AS temp,t2.inn_name AS innname,t2.region_name as region FROM finance_parent_order t1 LEFT JOIN finance_inn_settlement_info t2 ON t1.inn_id=t2.id WHERE fq_temp>0 AND settlement_time=? AND channel_id=? GROUP BY t1.inn_id,t2.inn_name,t2.region_name";
        return findListMapPageWithSql(page,sql,settlementTime,channelId);
    }


    /**
     * 指定渠道下的客栈暂收金额详情
     *
     * @param channelId
     * @param settlementTime
     * @return
     */
    public List<Map<String,Object>> exportFqTempInn(Integer channelId, String settlementTime) {
        String sql = "SELECT sum(t1.total_amount)AS total,count(t1.id) AS count,SUM(t1.channel_settlement_amount)AS channel ,SUM(t1.fq_temp) AS temp,t2.inn_name AS innname,t2.region_name as region FROM finance_parent_order t1 LEFT JOIN finance_inn_settlement_info t2 ON t1.inn_id=t2.id WHERE fq_temp>0 AND settlement_time=? AND channel_id=? GROUP BY t1.inn_id,t2.inn_name,t2.region_name";
        return findListMapWithSql(sql,settlementTime , channelId);
    }

}
