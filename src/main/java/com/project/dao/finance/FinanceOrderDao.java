package com.project.dao.finance;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceParentOrder;
import com.project.entity.finance.FinanceSpecialOrder;
import com.project.utils.CollectionsUtil;
import com.project.utils.FinanceHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单持久化操作对象
 * Created by 番茄桑 on 2015/8/31.
 */
@Component("financeOrderDao")
public class FinanceOrderDao extends HibernateDao<FinanceParentOrder, Integer> {

    /**
     * 根据结算月份查询需要结算的客栈ID集合
     *
     * @param settlementTime
     * @return
     */
    public List<Integer> getSettlementInnId(String settlementTime) {
        return find("select fpo.innId from FinanceParentOrder fpo  WHERE fpo.status = '" + FinanceParentOrder.STATUS_ACCEPTED + "' and fpo.settlementTime = '" + settlementTime + "' GROUP BY innId");
    }

    /**
     * 根据结算账期和渠道查询需要结算的客栈ID集合
     *
     * @param settlementTime
     * @return
     */
    public List<Integer> getSettlementInnId(String settlementTime, Integer channelId) {
        return find("select distinct fpo.innId from FinanceParentOrder fpo  WHERE fpo.status = '" + FinanceParentOrder.STATUS_ACCEPTED + "' and fpo.channelId =" + channelId + " and fpo.settlementTime = '" + settlementTime + "' GROUP BY innId");
    }

    /**
     * 根据渠道ID、结算月份和核单状态查询数据库中满足条件的订单状态集合
     *
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @param auditStatus    核单状态
     * @return 满足条件的订单状态集合
     */

    public String selectExistOrderStatus(Integer channelId, String settlementTime, String auditStatus) {
        return find("SELECT fqo.status FROM FinanceParentOrder fqo WHERE fqo.channelId = '" + channelId + "' AND fqo.settlementTime = '" + settlementTime + "' AND fqo.auditStatus = '" + auditStatus + "' GROUP BY status").toString();
    }

    /**
     * 根据客栈ID和结算月份查询订单
     *
     * @param innId          客栈ID
     * @param settlementTime 结算月份
     * @return
     */
    public List<FinanceParentOrder> findFinanceParentOrderByInnId(Integer innId, String settlementTime) {
        return findWithSql("select * from finance_parent_order where status='" + FinanceParentOrder.STATUS_ACCEPTED + "' and inn_id='" + innId + "' and settlement_time='" + settlementTime + "'");
    }

    /**
     * 获取所有单价
     *
     * @param beginTime
     * @param endTime
     * @param channelId
     * @param isAcceptedOnly
     * @return
     */
    public List<BigDecimal> getAllOrderPrices(String beginTime, String endTime, Integer channelId, Boolean isAcceptedOnly) {
        String hql = "SELECT fo.bookPrice FROM FinanceOrder fo, FinanceParentOrder fpo where fo.financeParentOrder=fpo and fpo.orderTime BETWEEN '" + beginTime + "' AND '" + endTime + "'";
        if (channelId != null) {
            hql += " AND fpo.channelId='" + channelId + "'";
        }
        if (isAcceptedOnly) {
            hql += " AND fpo.status='" + FinanceParentOrder.STATUS_ACCEPTED + "'";
        }
        hql += " order by fo.bookPrice asc";

        return find(hql);
    }

    /**
     * 根据下单时间（区间）、渠道ID、
     *
     * @param beginDate      查询开始日期
     * @param endDate        查询结束日期
     * @param channelId      渠道ID
     * @param isAcceptedOnly 是否只统计已接受订单（即status=1）
     * @return
     */
    public List<Map<String, Object>> selectOrderAmountList(String beginDate, String endDate, Integer channelId, boolean isAcceptedOnly) {
        StringBuilder sql = new StringBuilder("SELECT to_char(order_time, 'yyyy-MM-dd') as days,COUNT(ID) as orders, SUM(total_amount) as amounts, SUM(room_nights) as nights FROM finance_parent_order WHERE 1=1");
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            sql.append(" AND order_time BETWEEN '");
            sql.append(beginDate);
            sql.append("' AND '");
            sql.append(endDate);
            sql.append(" 23:59:59'");
        }
        if (channelId != null) {
            sql.append(" AND channel_id='");
            sql.append(channelId);
            sql.append("'");
        }
        if (isAcceptedOnly) {
            sql.append(" AND status='" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        }
        sql.append(" GROUP BY to_char(order_time, 'yyyy-MM-dd')");
        return findListMapWithSql(sql.toString());
    }

    /**
     * 根据下单时间（区间）、渠道ID、查询订单总额，订单数，间夜数，状态
     *
     * @param beginDate
     * @param endDate
     * @param channelId
     * @return
     */
    public List<Map<String, Object>> selectOrderAmountListByStatus(String beginDate, String endDate, Integer channelId) {
        StringBuilder sql = new StringBuilder("SELECT status AS status,(CASE WHEN status = '0' THEN '未处理' WHEN status = '" + FinanceParentOrder.STATUS_ACCEPTED + "' THEN '已接受' WHEN status = '2' THEN '已拒绝' WHEN status = '3' THEN '已取消' WHEN status = '4' THEN '验证失败' WHEN status = '5' THEN '未分房' ELSE '已拒绝' END) as zt,COUNT(id) AS orders ,SUM(total_amount) AS amounts,SUM(room_nights) AS nights from finance_parent_order WHERE 1=1 ");
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            sql.append(" AND order_time BETWEEN '");
            sql.append(beginDate);
            sql.append("' AND '");
            sql.append(endDate);
            sql.append(" 23:59:59'");
        }
        if (channelId != null) {
            sql.append(" AND channel_id='");
            sql.append(channelId);
            sql.append("'");
        }
        sql.append("GROUP BY status");

        return findListMapWithSql(sql.toString());
    }

    /**
     * 根据时间段获取订单数，总金额，间夜数
     *
     * @param beginDate
     * @param endDate
     * @param channelId
     * @return
     */
    public List<Map<String, Object>> selectOrderAmountListByTime(String beginDate, String endDate, Integer channelId) {
        StringBuilder sql = new StringBuilder("SELECT date_part('hour', order_time) times,COUNT(ID) as orders, SUM(total_amount) as amounts, SUM(room_nights) as nights FROM finance_parent_order WHERE 1=1");
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            sql.append(" AND order_time BETWEEN '");
            sql.append(beginDate);
            sql.append("' AND '");
            sql.append(endDate);
            sql.append(" 23:59:59'");
        }
        if (channelId != null) {
            sql.append(" AND channel_id='");
            sql.append(channelId);
            sql.append("'");
        }
        sql.append(" GROUP BY date_part('hour', order_time) ORDER BY orders DESC");
        return findListMapWithSql(sql.toString());
    }

    /**
     * 查询订单总金额，总订单数，总间夜数
     *
     * @param beginDate      查询开始日期
     * @param endDate        查询结束日期
     * @param channelId      渠道ID
     * @param isAcceptedOnly 是否只统计已接受订单（即status=1）
     * @return
     */
    public Map<String, Object> selectOrderAmount(String beginDate, String endDate, Integer channelId, boolean isAcceptedOnly) {
        StringBuilder sql = new StringBuilder("SELECT COUNT (ID) AS orders,SUM (room_nights) AS room_nights,SUM (total_amount) AS total_amount, SUM(rooms) As rooms, SUM(nights) AS nights,SUM(reservation_days) as reservation_days,SUM(stay_days) as stay_days FROM finance_parent_order where 1=1");
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            sql.append(" AND order_time BETWEEN '");
            sql.append(beginDate);
            sql.append("' AND '");
            sql.append(endDate);
            sql.append(" 23:59:59'");
        }
        if (channelId != null) {
            sql.append(" AND channel_id='");
            sql.append(channelId);
            sql.append("'");
        }
        if (isAcceptedOnly) {
            sql.append(" AND status='" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        }
        return findMapWithSql(sql.toString());
    }

    /**
     * 按照过滤条件统计已售客栈数量
     *
     * @param beginDate      查询开始日期
     * @param endDate        查询结束日期
     * @param channelId      渠道ID
     * @param isAcceptedOnly 是否只统计已接受订单（即status=1）
     * @return
     */
    public int selectSoldInnCount(String beginDate, String endDate, Integer channelId, boolean isAcceptedOnly) {
        StringBuilder sql = new StringBuilder("SELECT inn_id FROM finance_parent_order where 1=1");
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            sql.append(" AND order_time BETWEEN '");
            sql.append(beginDate);
            sql.append("' AND '");
            sql.append(endDate);
            sql.append(" 23:59:59'");
        }
        if (channelId != null) {
            sql.append(" AND channel_id='");
            sql.append(channelId);
            sql.append("'");
        }
        if (isAcceptedOnly) {
            sql.append(" AND status='" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        }
        sql.append(" GROUP BY inn_id");
        List<Map<String, Object>> mapList = findListMapWithSql(sql.toString());
        if (CollectionsUtil.isNotEmpty(mapList)) {
            return mapList.size();
        }
        return 0;
    }

    /**
     * 查询全部满足条件的订单总数量
     *
     * @param beginDate 查询开始日期
     * @param endDate   查询结束日期
     * @param channelId 渠道ID
     * @return
     */
    public int selectAllOrderAmount(String beginDate, String endDate, Integer channelId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM finance_parent_order where 1=1");
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            sql.append(" AND order_time BETWEEN '");
            sql.append(beginDate);
            sql.append("' AND '");
            sql.append(endDate);
            sql.append(" 23:59:59'");
        }
        if (channelId != null) {
            sql.append(" AND channel_id='");
            sql.append(channelId);
            sql.append("'");
        }
        List<Map<String, Object>> mapList = findListMapWithSql(sql.toString());
        if (CollectionsUtil.isNotEmpty(mapList)) {
            return mapList.size();
        }
        return 0;
    }

    /**
     * 获取客栈订单排行
     *
     * @param beginDate      查询开始日期
     * @param endDate        查询结束日期
     * @param channelId      渠道ID
     * @param isAcceptedOnly 是否只统计已接受订单（即status=1）
     * @param order          排序条件
     * @param pageSize       排行榜容量
     * @return
     */
    public List<Map<String, Object>> selectInnRank(String beginDate, String endDate, Integer channelId, boolean isAcceptedOnly, String order, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT inn_name as inn_name,COUNT(ID) as orders, SUM(total_amount) as total_amount, SUM(room_nights) as room_nights FROM finance_parent_order WHERE 1=1");
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            sql.append(" AND order_time BETWEEN '");
            sql.append(beginDate);
            sql.append("' AND '");
            sql.append(endDate);
            sql.append(" 23:59:59'");
        }
        if (channelId != null) {
            sql.append(" AND channel_id='");
            sql.append(channelId);
            sql.append("'");
        }
        if (isAcceptedOnly) {
            sql.append(" AND status='" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        }
        sql.append(" GROUP BY inn_id,inn_name");
        sql.append(" ORDER BY ");
        sql.append(order);
        sql.append(" DESC LIMIT ");
        sql.append(pageSize);
        return findListMapWithSql(sql.toString());
    }

    /**
     * select count(*) from User u ").uniqueResult()).intValue();
     * 根据结算月份查询需要结算的渠道ID集合
     *
     * @param settlementTime 结算月份
     * @return
     */
    public List<Short> getSettlementChannelId(String settlementTime) {
        return find("select fpo.channelId from FinanceParentOrder fpo  WHERE fpo.status ='" + FinanceParentOrder.STATUS_ACCEPTED + "' and fpo.settlementTime = '" + settlementTime + "' GROUP BY channelId");
    }


    /**
     * 根据渠道ID和结算月份查询订单
     *
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @return
     */
    public List<FinanceParentOrder> findFinanceParentOrderByChannelId(Short channelId, String settlementTime) {
        String sql = "select * from finance_parent_order fpo where fpo.status ='" + FinanceParentOrder.STATUS_ACCEPTED + "' and fpo.channel_id='" + channelId + "' and fpo.settlement_time='" + settlementTime + "'";
        return findWithSql(sql);
    }

    /**
     * 用于性能优化
     * 根据分销商ID和结算账期查询账单，只查询状态为1（已接受已分房）的
     *
     * @param channelId
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> selectReconciliationOrderMap(Integer channelId, String settlementTime) {
        StringBuilder sqlBuild = new StringBuilder("SELECT channel_order_no AS channel_order_no, total_amount AS total_amount, channel_settlement_amount AS channel_settlement_amount");
        sqlBuild.append(" FROM finance_parent_order");
        sqlBuild.append(" WHERE status = '" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        sqlBuild.append(" AND channel_id = '" + channelId + "'");
        sqlBuild.append(" AND settlement_time = '" + settlementTime + "'");
        return findListMapWithSql(sqlBuild.toString());
    }

    /**
     * 根据结算月份查询渠道结算的汇总数据
     *
     * @param settlementTime 结算时间
     * @return 渠道商名称、渠道ID、订单总数、订单总金额、渠道结算金额、客栈结算金额
     */
    public List<Map<String, Object>> getFinanceChannelSettlement(String settlementTime, boolean filterStatus) {
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("SELECT ");
        // 渠道ID
        sqlBuild.append("t1.status AS status,");
        // 渠道ID
        sqlBuild.append("t1.channel_id AS ID,");
        // 渠道名称
        sqlBuild.append("t1.channel_name AS NAME,");
        // 订单总个数
        sqlBuild.append("COUNT (t1. ID) AS total,");
        // 客栈订单总金额
        sqlBuild.append("SUM (t1.inn_amount) AS orders,");
        // 分销商订单总金额
        sqlBuild.append("SUM (t1.total_amount) AS channel,");
        // 分销商结算总金额
        sqlBuild.append("SUM (t1.channel_settlement_amount) AS amount,");
        // 客栈结算金额
        sqlBuild.append("SUM (t1.inn_settlement_amount) AS inn,");
        // 分销商扣赔付金额(赔付)
        sqlBuild.append("COALESCE(SUM (t2.channel_debit),0.00) AS channel_debit,");
        // 分销商扣退款金额(退款)
        sqlBuild.append("COALESCE(SUM (t2.channel_refund),0.00) AS channel_refund,");
        // 客栈赔付金额(赔付)
        sqlBuild.append("COALESCE(SUM (t2.inn_payment),0.00) AS inn_payment,");
        // 客栈客栈退款金额(退款)
        sqlBuild.append("COALESCE(SUM (t2.inn_refund),0.00) AS inn_refund,");
        // 番茄补款金额(补款)
        sqlBuild.append("COALESCE(SUM (t2.fq_replenishment),0.00) AS fq_replenishment,");
        // 番茄退佣金收入(退款)
        sqlBuild.append("COALESCE(SUM (t2.fq_refund_commission),0.00) AS total_fq_refund_commission,");
        // 番茄结算金额
        sqlBuild.append("COALESCE(SUM (t1.fq_settlement_amount),0.00) AS fq_settlement_amount");
        sqlBuild.append(" FROM finance_parent_order t1 LEFT JOIN finance_special_order t2 ON t1. ID = t2.order_id");
        sqlBuild.append(" WHERE 1=1");
        if (filterStatus) {
            sqlBuild.append(" AND status IN ('" + FinanceParentOrder.STATUS_ACCEPTED + "',").append(FinanceHelper.getSpecialOrderStatus()).append(")");
        }
        sqlBuild.append(" AND settlement_time = '").append(settlementTime).append("'");
        sqlBuild.append(" GROUP BY status,channel_id,channel_name");
        return findListMapWithSql(sqlBuild.toString());
    }

    /**
     * 统计番茄暂收金额
     *
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> getFqTemp(String settlementTime) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("SELECT SUM(fq_temp) AS fq,channel_name AS name,channel_id AS id from finance_parent_order WHERE 1=1");
        stringBuilder.append(" AND status IN ('" + FinanceParentOrder.STATUS_ACCEPTED + "',").append(FinanceHelper.getSpecialOrderStatus()).append(")");
        stringBuilder.append(" AND settlement_time = '").append(settlementTime).append("'");
        stringBuilder.append(" GROUP BY status,channel_id,channel_name");
        return findListMapWithSql(stringBuilder.toString());
    }

    /**
     * 获取番茄正常收入
     *
     * @return
     */
    public Map<String, Object> findRealChannelSettlement(String settlementTime, Integer innId) {
        String sql = "select COALESCE(sum(t1.fq_settlement_amount),0) - COALESCE(sum(t1.fq_refund_commission_amount),0) as real from finance_inn_channel_settlement t1 where t1.settlement_time=? and channel_id=?";
        return findMapWithSql(sql, settlementTime, innId);
    }

    /**
     * 根据分销商ID、账期、账单状态，统计账单总数、分销商订单总额、分销商结算金额、 客栈订单总额为、客栈结算金额
     *
     * @param channelId      分销商ID
     * @param settlementTime 账期
     * @param status         账单状态 为空时默认查询非特殊单
     * @return
     */
    public List<Map<String, Object>> selectTotalChannelSettlement(Integer channelId, String settlementTime, Integer status) {
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("SELECT ");
        // 订单总个数
        sqlBuild.append("COUNT (t1. ID) AS total,");
        // 客栈订单总金额
        sqlBuild.append("SUM (t1.inn_amount) AS orders,");
        // 分销商订单总金额
        sqlBuild.append("SUM (t1.total_amount) AS channel,");
        // 分销商结算金额
        sqlBuild.append("SUM (t1.channel_settlement_amount) AS amount,");
        // 客栈结算金额
        sqlBuild.append("SUM (t1.inn_settlement_amount) AS inn,");
        // 客栈赔付金额(赔付)
        sqlBuild.append("SUM (t2.inn_payment) AS inn_payment,");
        // 客栈赔付番茄承担(赔付)
        sqlBuild.append("SUM (t2.fq_bear) AS fq_bear,");
        // 客栈赔付番茄收入(赔付)
        sqlBuild.append("SUM (fq_income) AS fq_income,");
        // 分销商扣赔付金额(赔付)
        sqlBuild.append("SUM (channel_debit) AS channel_debit,");
        // 分销商扣赔付总额=客栈赔付总额+客栈赔付番茄承担总额-客栈赔付番茄收入金额
        sqlBuild.append("SUM (t2.inn_payment) + SUM (t2.fq_bear) - SUM (fq_income) AS total_channel_debit,");
        // 分销商扣退款金额(退款)
        sqlBuild.append("SUM (t2.channel_refund) AS channel_refund,");
        // 客栈退款金额(退款)
        sqlBuild.append("SUM (t2.inn_refund) AS inn_refund,");
        // 番茄退佣金收入(退款)
        sqlBuild.append("SUM (t2.fq_refund_commission) AS fq_refund_commission,");
        // 番茄退往来款(退款)
        sqlBuild.append("SUM (t2.fq_refund_contacts) AS fq_refund_contacts,");
        // 番茄补款金额(补款)
        sqlBuild.append("SUM (t2.fq_replenishment) AS fq_replenishment");
        sqlBuild.append(" FROM finance_parent_order t1 LEFT JOIN finance_special_order t2 ON t1. ID = t2.order_id");
        sqlBuild.append(" WHERE channel_id = '" + channelId + "'");
        if (status == null) {
            sqlBuild.append(" AND status = '" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        } else {
            sqlBuild.append(" AND status = '" + status + "'");
        }
        sqlBuild.append(" AND settlement_time = '" + settlementTime + "'");
        return findListMapWithSql(sqlBuild.toString());
    }

    /**
     * 根据结算月份查询客栈结算的明细
     *
     * @param settlementTime 结算月份
     * @return
     */
    public List<Map<String, Object>> getFinanceInnSettlement(String settlementTime) {
        String sql = "SELECT t1.inn_id AS ID,COUNT (t1.ID) AS total,COUNT (t2.id) as special,SUM (t1.inn_amount) AS orders,SUM (t1.total_amount) AS channel,SUM (t1.channel_settlement_amount) AS channels,SUM (t1.fq_settlement_amount) AS fqs,SUM (t1.inn_settlement_amount) AS inns,SUM (t1.room_nights) AS rns,COALESCE(SUM (t2.inn_payment),0.00) AS ip,COALESCE(SUM (t2.inn_refund),0.00) AS ir,COALESCE(SUM (t2.fq_replenishment),0.00) AS fr FROM finance_parent_order t1 LEFT  JOIN finance_special_order t2 ON t1.id=t2.order_id WHERE  t1.status in ('" + FinanceParentOrder.STATUS_ACCEPTED + "'," + FinanceHelper.getSpecialOrderStatus() + ") AND t1.settlement_time = '" + settlementTime + "' GROUP BY t1.inn_id";
        return findListMapWithSql(sql);
    }


    /**
     * 统计分销商扣款金额
     *
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> statisticChannelRealSettlement(String settlementTime) {
        StringBuilder stringBuilder = new StringBuilder("SELECT  t1.inn_id as id,t1.channel_id as channel_id,COALESCE(SUM(t2.channel_refund),0)+COALESCE(SUM(t2.channel_debit),0)AS channel FROM finance_parent_order t1 LEFT JOIN finance_special_order t2 ON t2.order_id=t1.id WHERE t1.settlement_time=? ");

        stringBuilder.append(" and t1.status in ('" + FinanceParentOrder.STATUS_ACCEPTED + "'," + FinanceHelper.getSpecialOrderStatus() + ")");
        stringBuilder.append(" GROUP BY inn_id,channel_id");
        return findListMapWithSql(stringBuilder.toString(), settlementTime);
    }

    /**
     * 统计客栈扣款金额
     *
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> staticticRealSettlement(String settlementTime) {
        StringBuilder stringBuilder = new StringBuilder("SELECT  t1.inn_id as id,COALESCE(SUM(t2.channel_refund),0)+COALESCE(SUM(t2.channel_debit),0)AS channel FROM finance_parent_order t1 LEFT JOIN finance_special_order t2 ON t2.order_id=t1.id WHERE t1.settlement_time=? ");
        stringBuilder.append(" and t1.status in ('" + FinanceParentOrder.STATUS_ACCEPTED + "'," + FinanceHelper.getSpecialOrderStatus() + ")");
        stringBuilder.append(" GROUP BY t1.inn_id");
        return findListMapWithSql(stringBuilder.toString(), settlementTime);
    }

    /**
     * 根据结算月份和渠道查询客栈结算的明细
     *
     * @param settlementTime 结算月份
     * @return
     */
    public List<Map<String, Object>> getFinanceInnChannelSettlement(String settlementTime) {
        String sql = "SELECT t1.inn_id AS ID,t1.channel_id AS channel_id,t1.channel_name as name,COUNT (t1.ID) AS total,count(t2.id) as special,SUM (t1.inn_amount) AS orders,SUM (t1.total_amount) AS channel,SUM (t1.channel_settlement_amount) AS channels,SUM (t1.fq_settlement_amount) AS fqs,SUM (t1.inn_settlement_amount) AS inns,SUM (t1.room_nights) AS rns,COALESCE(SUM (t2.inn_payment),0.00) AS ip,COALESCE(SUM (t2.inn_refund),0.00) AS ir,COALESCE(SUM (t2.fq_replenishment),0.00) AS fr,COALESCE(SUM (t2.fq_bear),0.00) AS fb ,COALESCE(SUM (t2.fq_income),0.00) AS fi,COALESCE(SUM (t2.fq_refund_commission),0.00) AS frc,SUM (t1.fq_settlement_amount)-COALESCE(SUM (t2.fq_refund_commission),0.00) as fqreal ,sum(fq_temp) as temp FROM finance_parent_order t1 LEFT  JOIN finance_special_order t2 ON t1.id=t2.order_id WHERE t1.status in ('" + FinanceParentOrder.STATUS_ACCEPTED + "'," + FinanceHelper.getSpecialOrderStatus() + ") AND t1.settlement_time = '" + settlementTime + "' GROUP BY t1.inn_id ,t1.channel_id,t1.channel_name";
        return findListMapWithSql(sql);
    }

    /**
     * 获取正常订单的Id
     *
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> getNormalOrderId(String settlementTime) {
        String sql = "SELECT id AS id FROM finance_parent_order WHERE settlement_time=? and status not in (" + FinanceHelper.getSpecialOrderStatus() + ")";
        return findListMapWithSql(sql, settlementTime);
    }

    public List<Map<String, Object>> getFqRefundContractsAmount(String settlementTime) {
        StringBuilder sql = new StringBuilder("SELECT t1.inn_id as inn_id, t1.channel_id as channel_id, t2.contacts_status as contacts_status, SUM(t2.fq_refund_contacts) as amount FROM finance_parent_order t1 LEFT JOIN finance_special_order t2 ON t1.id = t2.order_id");
        sql.append(" WHERE t1.status IN('" + FinanceParentOrder.STATUS_ACCEPTED + "'," + FinanceHelper.getSpecialOrderStatus() + ")");
        sql.append(" AND t1.settlement_time = '" + settlementTime + "'");
        sql.append(" AND t2.fq_refund_contacts IS NOT NULL GROUP BY t1.inn_id, t1.channel_id, t2.contacts_status");
        return findListMapWithSql(sql.toString());
    }

    public List<Map<String, Object>> getFqRefundContractsAmount(String settlementTime, Integer channelId, Integer innId) {
        StringBuilder sql = new StringBuilder("select t2.contacts_status, SUM(t2.fq_refund_contacts) from finance_parent_order t1 left join finance_special_order t2 on t1.\"id\"=t2.order_id")
                .append(" where t1.status in('" + FinanceParentOrder.STATUS_ACCEPTED + "'," + FinanceHelper.getSpecialOrderStatus() + ") and t1.settlement_time = '").append(settlementTime).append("'");
        if (innId != null) {
            sql.append(" AND t1.inn_id=").append(innId);
        }
        sql.append(" and t1.channel_id=").append(channelId).append(" GROUP BY t2.contacts_status");
        return findListMapWithSql(sql.toString());
    }

   /* public BigDecimal getFqNormalIncome(String settlementTime, Integer channelId, Integer innId) {
        String sql = "select COALESCE(sum(t1.fq_settlement_amount),0) - COALESCE(sum(t1.fq_refund_commission_amount),0) from finance_inn_channel_settlement t1 where t1.settlement_time='" + settlementTime + "' and t1.channel_id=" + channelId + " and t1.inn_id=" + innId;
        return (BigDecimal) getSession().createSQLQuery(sql).uniqueResult();
    }*/

    /**
     * 根据父订单ID查询父订单详情
     *
     * @param id 父订单ID
     * @return 父订单对象
     */
    public FinanceParentOrder findById(String id) {
        return findUniqueWithSql("select * from finance_parent_order where id=?", id);
    }

    /**
     * 根据查询条件分页查询订单明细
     *
     * @param page           分页对象
     * @param innId          客栈ID
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @param channelOrderNo 渠道订单号
     * @param auditStatus    核单状态
     * @param isArrival      是否收到渠道款项
     * @param priceStrategy  价格策略
     * @param isPage         是否分页
     * @return
     */
    public Page<FinanceParentOrder> selectFinanceParentOrderPage(Page<FinanceParentOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String auditStatus, Boolean isArrival, Short priceStrategy, boolean isPage) {
        StringBuilder sqlBuilder = new StringBuilder("select * from finance_parent_order where status='" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        if (innId != null) {
            sqlBuilder.append(" and inn_id=" + innId);
        }
        if (channelId != null) {
            sqlBuilder.append(" and channel_id=" + channelId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(channelOrderNo)) {
            sqlBuilder.append(" and channel_order_no like '%" + channelOrderNo + "%'");
        }
        if (StringUtils.isNotBlank(auditStatus)) {
            sqlBuilder.append(" and audit_status='" + auditStatus + "'");
        }
        if (isArrival != null) {
            sqlBuilder.append(" and is_arrival=" + isArrival);
        }
        if (priceStrategy != null) {
            sqlBuilder.append(" and price_strategy='" + priceStrategy + "'");
        }
        return findPageWithSql(isPage, page, sqlBuilder.toString());
    }

    /**
     * 根据查询条件分页查询订单明细
     *
     * @param page           分页对象
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @param auditStatus    核单状态
     * @param priceStrategy  价格策略
     * @param keyWord        搜索关键字
     * @param orderStatus    订单状态
     * @return
     */
    public Page<FinanceParentOrder> selectFinanceParentOrder(Page<FinanceParentOrder> page, Integer channelId, String settlementTime, String auditStatus, Short priceStrategy, Short isBalance, String keyWord, Integer orderStatus) {
        StringBuilder sqlBuilder = new StringBuilder("select * from finance_parent_order where 1=1");
        if (channelId != null) {
            sqlBuilder.append(" and channel_id=" + channelId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(auditStatus)) {
            sqlBuilder.append(" and audit_status='" + auditStatus + "'");
        }
        if (priceStrategy != null) {
            sqlBuilder.append(" and price_strategy='" + priceStrategy + "'");
        }
        if (isBalance != null) {
            sqlBuilder.append(" and settlement_status='" + isBalance + "'");
        }
        if (orderStatus == null) {
            sqlBuilder.append(" AND status NOT IN (" + FinanceHelper.getSpecialOrderStatus() + ")");
        } else {
            sqlBuilder.append(" AND status = '" + orderStatus + "'");
        }
        if (StringUtils.isNotBlank(keyWord)) {
            sqlBuilder.append(" and(inn_name like '%" + keyWord + "%' or channel_order_no like '%" + keyWord + "%'");
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(keyWord);
            // 如果谁纯数字查询客栈ID
            if (isNum.matches() && keyWord.length() < 10) {
                sqlBuilder.append(" or inn_id = '" + keyWord + "'");
            }
            sqlBuilder.append(")");
        }
        sqlBuilder.append(" order by order_time desc");
        return findPageWithSql(page, sqlBuilder.toString());
    }


    /**
     * 统计查询渠道订单总数和总金额
     *
     * @return
     */
    public List<Map<String, Object>> selectChannelOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String auditStatus, Short priceStrategy, Boolean isArrival) {
        StringBuilder sqlBuilder = new StringBuilder("select count(id) as orders,sum(total_amount) as total_amounts, sum(channel_settlement_amount) as amounts, sum(inn_settlement_amount) as inn_settlement_amounts, (sum(channel_settlement_amount) - sum(inn_settlement_amount)) as income_amounts from finance_parent_order where status = '" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        sqlBuilder.append(" and inn_id=" + innId);

        if (channelId != null) {
            sqlBuilder.append(" and channel_id=" + channelId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(channelOrderNo)) {
            sqlBuilder.append(" and channel_order_no like '%" + channelOrderNo + "%'");
        }
        if (StringUtils.isNotBlank(auditStatus)) {
            sqlBuilder.append(" and audit_status='" + auditStatus + "'");
        }
        if (priceStrategy != null) {
            sqlBuilder.append(" and price_strategy='" + priceStrategy + "'");
        }
        if (isArrival != null) {
            sqlBuilder.append(" and is_arrival=" + isArrival);
        }
        return findListMapWithSql(sqlBuilder.toString());
    }

    public List<Map<String, Object>> selectInnOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String auditStatus, Short priceStrategy, Boolean isArrival) {
        StringBuilder sqlBuilder = new StringBuilder("select count(id) as orders,sum(inn_amount) as ia,sum(total_amount) as ta,sum(channel_settlement_amount) as channels,sum(fq_settlement_amount) as fqs,sum(inn_settlement_amount) as inns from finance_parent_order where status = '" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        if (innId != null) {
            sqlBuilder.append(" and inn_id=" + innId);
        }
        if (channelId != null) {
            sqlBuilder.append(" and channel_id=" + channelId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(channelOrderNo)) {
            sqlBuilder.append(" and (channel_order_no like '%" + channelOrderNo + "%' or order_no like '%" + channelOrderNo + "%')");
        }
        if (StringUtils.isNotBlank(auditStatus)) {
            sqlBuilder.append(" and audit_status='" + auditStatus + "'");
        }
        if (priceStrategy != null) {
            sqlBuilder.append(" and price_strategy='" + priceStrategy + "'");
        }
        if (isArrival != null) {
            sqlBuilder.append(" and is_arrival=" + isArrival);
        }
        return findListMapWithSql(sqlBuilder.toString());
    }

    /**
     * 根据渠道ID和结算时间，将订单的是否收到款项更新为true
     *
     * @param channelId
     * @param settlementTime
     */
    public void updateFinanceParentOrderArrival(Integer channelId, String settlementTime) {
        executeUpdateWithSql("update finance_parent_order set is_arrival=true where channel_id=" + channelId + " and settlement_time='" + settlementTime + "'");
    }

    /**
     * 校验客栈ID与appKey是否匹配，过滤非法请求
     *
     * @param innId  PMS客栈ID
     * @param appKey
     * @return
     */
    public List<Map<String, Object>> getInnAppKey(Integer innId, String appKey) {
        String sql = "SELECT id as id FROM tomato_inn where id='" + innId + "' and app_key='" + appKey + "'";
        return findListMapWithSql(sql);
    }

    /**
     * 修改订单的核单状态
     *
     * @param channelId          分销商ID
     * @param channelOrderNoList 分销商订单号集合
     * @param auditStatus        核单状态
     */
    public void updateFinanceParentOrderAuditStatus(Integer channelId, String channelOrderNoList, String auditStatus) {
        executeUpdateWithSql("update finance_parent_order set audit_status=" + auditStatus + " where channel_id='" + channelId + "' and channel_order_no in (" + channelOrderNoList + ")");
    }

    /**
     * 定时任务根据ID更新订单状态
     *
     * @param id
     * @param status
     */
    public void updateStatusById(int id, short status) {
        executeUpdateWithSql("update finance_parent_order set status=" + status + " where id=" + id);
    }

    /**
     * 根据渠道ID和结算月份查询订单
     *
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @return
     */
    public List<FinanceParentOrder> findFinanceParentOrderWithChannelId(Integer channelId, String settlementTime, Integer innId) {
        return find("select * from FinanceParentOrder fpo where fpo.status='" + FinanceParentOrder.STATUS_ACCEPTED + "' and fpo.channelId=" + channelId + " and  fpo.innId=" + innId + " and fpo.settlementTime='" + settlementTime + "'");
    }

    /**
     * 查询特殊结算下的正常订单
     *
     * @return
     */
    public Page<FinanceParentOrder> findSpecialInnNormalOrder(Page<FinanceParentOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo) {
        StringBuilder sqlBuilder = new StringBuilder("select * from finance_parent_order where status='" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        if (innId != null) {
            sqlBuilder.append(" and inn_id=" + innId);
        }
        if (channelId != null) {
            sqlBuilder.append(" and channel_id=" + channelId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(channelOrderNo)) {
            sqlBuilder.append(" and (channel_order_no like '%" + channelOrderNo + "%' or order_no like '% " + channelOrderNo + "%')");
        }

        return findPageWithSql(page, sqlBuilder.toString());
    }

    /**
     * 统计特殊结算的正常订单
     *
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    public List<Map<String, Object>> selectSpecialInnNormalOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo) {
        StringBuilder sqlBuilder = new StringBuilder("select count(id) as orders,sum(inn_amount) as ia,sum(total_amount) as ta,sum(channel_settlement_amount) as channels,sum(fq_settlement_amount) as fqs,sum(inn_settlement_amount) as inns from finance_parent_order where status = '" + FinanceParentOrder.STATUS_ACCEPTED + "'");
        if (innId != null) {
            sqlBuilder.append(" and inn_id=" + innId);
        }
        if (channelId != null) {
            sqlBuilder.append(" and channel_id=" + channelId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(channelOrderNo)) {
            sqlBuilder.append(" and (channel_order_no like '%" + channelOrderNo + "%' or order_no like '%" + channelOrderNo + "%')");
        }

        return findListMapWithSql(sqlBuilder.toString());
    }

    /**
     * 统计特殊结算下的赔付订单
     *
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    public List<Map<String, Object>> selectSpecialInnRecoveryOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT (t2. ID) as orders,SUM (t2.channel_debit) AS recovery,SUM (t2.inn_payment) AS innpay,SUM (t2.fq_bear) AS bear,SUM (t2.fq_income) AS income FROM finance_parent_order t1 RIGHT JOIN finance_special_order t2 ON t1. ID = t2.order_id where t1.status='" + FinanceSpecialOrder.DEBIT_STATUS + "'");
        if (innId != null) {
            sqlBuilder.append(" and inn_id=" + innId);
        }
        if (channelId != null) {
            sqlBuilder.append(" and channel_id=" + channelId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(channelOrderNo)) {
            sqlBuilder.append(" and (channel_order_no like '%" + channelOrderNo + "%' or order_no like '%" + channelOrderNo + "%')");
        }
        return findListMapWithSql(sqlBuilder.toString());
    }


    /**
     * 统计特殊结算下的退款订单
     *
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    public List<Map<String, Object>> selectSpecialInnRefundOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String contactsStatus) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT (t2. ID) as orders,SUM (t2.channel_refund) AS refund,SUM (t2.inn_refund) AS innrefund,SUM (t2.fq_refund_commission) AS frc,SUM (t2.fq_refund_contacts) AS contacts FROM finance_parent_order t1 RIGHT JOIN finance_special_order t2 ON t1. ID = t2.order_id where t1.status='" + FinanceSpecialOrder.REFUND_STATUS + "'");
        if (innId != null) {
            sqlBuilder.append(" and inn_id=" + innId);
        }
        if (channelId != null) {
            sqlBuilder.append(" and channel_id=" + channelId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(channelOrderNo)) {
            sqlBuilder.append(" and (channel_order_no like '%" + channelOrderNo + "%' or order_no like '%" + channelOrderNo + "%')");
        }
        if (StringUtils.isNotBlank(contactsStatus)) {
            sqlBuilder.append(" and t2.contacts_status='" + contactsStatus + "'");
        }
        return findListMapWithSql(sqlBuilder.toString());
    }

    /**
     * 统计特殊结算下的补款订单
     *
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    public List<Map<String, Object>> selectSpecialInnReplenishmentOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT (t2. ID) as orders,SUM (t1.channel_settlement_amount) AS channels,SUM (t2.fq_replenishment) AS replenishment,SUM (t1.inn_settlement_amount) AS inns FROM finance_parent_order t1 RIGHT JOIN finance_special_order t2 ON t1. ID = t2.order_id where t1.status='" + FinanceSpecialOrder.REPLENISHMENT_STATUS + "'");
        if (innId != null) {
            sqlBuilder.append(" and inn_id=" + innId);
        }
        if (channelId != null) {
            sqlBuilder.append(" and channel_id=" + channelId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(channelOrderNo)) {
            sqlBuilder.append(" and (channel_order_no like '%" + channelOrderNo + "%' or order_no like '%" + channelOrderNo + "%')");
        }

        return findListMapWithSql(sqlBuilder.toString());
    }

    /**
     * 根据ID查询由该账单复制的账单集合
     *
     * @param id
     * @return
     */
    public List<FinanceParentOrder> selectFinanceParentOrderListById(String id) {
        return findWithSql("SELECT * FROM finance_parent_order WHERE ID LIKE '%" + id + "_%'");
    }

    /**
     * 一键结算更新订单状态
     */
    public void updateOrderWithBalance(String settlementTime) {
        String sql = "UPDATE finance_parent_order SET settlement_status = '" + FinanceParentOrder.STATUS_ACCEPTED + "' FROM finance_inn_settlement WHERE finance_parent_order.inn_id = finance_inn_settlement.inn_id AND finance_parent_order.settlement_time =? AND finance_inn_settlement.is_tagged=FALSE AND finance_inn_settlement.settlement_status='0'";
        executeUpdateWithSql(sql, settlementTime);
    }

    /**
     * 单个客栈结算更新订单状态
     */
    public void updateOrderWithBalance(String settlementTime,Integer innId,String settlementStatus) {
        String sql = "UPDATE finance_parent_order SET settlement_status =?  FROM finance_inn_settlement WHERE finance_parent_order.inn_id = finance_inn_settlement.inn_id AND finance_parent_order.settlement_time =? AND finance_inn_settlement.is_tagged=FALSE AND finance_inn_settlement.settlement_status='0' and finance_parent_order.inn_id=?";
        executeUpdateWithSql(sql,settlementStatus, settlementTime,innId);
    }

    /**
     * 统计指定渠道下的正常订单金额
     *
     * @return
     */
    public Map<String, Object> statisticChannelSettlementOrder(Integer innId, String settlementTime, Integer channelId) {
        String sql = "select SUM(channel_settlement_amount) as channel FROM finance_parent_order  WHERE inn_id=? and settlement_time=? and channel_id=? and status='" + FinanceParentOrder.STATUS_ACCEPTED + "'";
        return findMapWithSql(sql, innId, settlementTime, channelId);
    }

    /**
     * 统计正常订单相关金额
     *
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> statisticAmount(String settlementTime) {
        String sql = "select inn_id as id ,SUM(channel_settlement_amount) as channel,SUM(inn_settlement_amount) as inn FROM finance_parent_order  WHERE settlement_time=? and status='" + FinanceParentOrder.STATUS_ACCEPTED + "' GROUP BY inn_id";
        return findListMapWithSql(sql, settlementTime);
    }

    /**
     * 统计渠道正常订单相关金额
     *
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> statisticChannelAmount(String settlementTime) {
        String sql = "select inn_id as id ,channel_id as channelid,SUM(channel_settlement_amount) as channel,SUM(inn_settlement_amount) as inn FROM finance_parent_order  WHERE settlement_time=? and status='" + FinanceParentOrder.STATUS_ACCEPTED + "' GROUP BY inn_id,channel_id";
        return findListMapWithSql(sql, settlementTime);
    }

    /**
     * 分页查询已接受和补款状态的账单
     *
     * @param page
     * @param innId          PMS客栈ID
     * @param priceStrategy  价格模式
     * @param settlementTime 结算账期
     * @return
     */
    public Page<FinanceParentOrder> selectApiParentOrderList(Page<FinanceParentOrder> page, Integer innId, Short priceStrategy, String settlementTime, boolean isPage) {
        StringBuilder sqlBuilder = new StringBuilder("select * from finance_parent_order where status in ('" + FinanceParentOrder.STATUS_ACCEPTED + "','" + FinanceSpecialOrder.REPLENISHMENT_STATUS + "')");
        if (innId != null) {
            sqlBuilder.append(" and inn_id=" + innId);
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (priceStrategy != null) {
            String priceStrategyStr = "'1'";
            if (priceStrategy == 2) {
                priceStrategyStr = "'2','3'";
            }
            sqlBuilder.append(" and price_strategy in (" + priceStrategyStr + ")");
        }
        sqlBuilder.append(" order by order_time");
        return findPageWithSql(isPage, page, sqlBuilder.toString());
    }

    /**
     * 根据账单ID批量重置订单的账期、产生周期
     * @param orderIdListString
     * @param settlementTime
     */
    public void updateFinanceParentOrderSettlementTime(String orderIdListString, String settlementTime) {
        String sql = "update finance_parent_order set settlement_status='0',produce_time='" + settlementTime + "',settlement_time='" + settlementTime + "' where id in (" + orderIdListString + ")";
        executeUpdateWithSql(sql);
    }
}
