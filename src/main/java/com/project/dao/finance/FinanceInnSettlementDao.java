package com.project.dao.finance;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceInnSettlement;
import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 客栈结算持久化操作对象
 * Created by 番茄桑 on 2015/9/15.
 */
@Component("financeInnSettlementDao")
public class FinanceInnSettlementDao extends HibernateDao<FinanceInnSettlement, Integer> {

    /**
     * 根据客栈ID分页查询客栈的结算历史记录
     *
     * @param innId 客栈ID
     * @return 客栈的结算历史记录
     */
    public Page<FinanceInnSettlement> selectFinanceInnSettlementListByInnId(Page<FinanceInnSettlement> page, Integer innId) {
        return findPageWithSql(page, "select * from finance_inn_settlement where bill_status = true and inn_id=" + innId + " order by settlement_time desc");
    }

    /**
     * 根据查询条件分页查询客栈结算对象
     *
     * @param status           客栈状态
     * @param page             分页对象
     * @param innName          客栈名称（支持模糊查询）
     * @param settlementTime   结算月份
     * @param confirmStatus    客栈确认状态
     * @param settlementStatus 结算状态
     * @return
     */
    public Page<FinanceInnSettlement> selectFinanceInnSettlementList(Page<FinanceInnSettlement> page, String innName, String settlementTime, String confirmStatus, String settlementStatus, Boolean isTagged, boolean isPage, String status, Boolean isMatch) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT t1.*,t2.* FROM finance_inn_settlement t1 INNER JOIN finance_inn_settlement_info t2 ON t1.inn_id = t2. ID ");
        if (StringUtils.isNotBlank(status)) {
            if (status.equals(FinanceInnSettlement.NORMAL_STATUS)) {
                sqlBuilder.append(" and t1.is_arrears='0' and t1.is_special=false and t1.settlement_status<>'2'");
            }
            if (status.equals(FinanceInnSettlement.SPECIAL_STATUS)) {
                sqlBuilder.append(" and t1.is_arrears='0'  and t1.is_special=TRUE and t1.settlement_status<>'2'");
            }
            if (status.equals(FinanceInnSettlement.DELAY_STATUS)) {
                sqlBuilder.append(" and t1.settlement_status='2'");
            }
        }
        if (StringUtils.isNotBlank(innName)) {
            sqlBuilder.append(" and t2.inn_name like '%" + innName + "%'");
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and t1.settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(confirmStatus)) {
            sqlBuilder.append(" and t1.confirm_status='" + confirmStatus + "'");
        }
        if (StringUtils.isNotBlank(settlementStatus)) {
            sqlBuilder.append(" and t1.settlement_status='" + settlementStatus + "'");
        }
        if (isTagged != null) {
            sqlBuilder.append(" and t1.is_tagged=" + isTagged);
        }
        if (isMatch != null) {
            sqlBuilder.append(" and t1.is_match=" + isMatch);
        }

        sqlBuilder.append(" order by t1.settlement_time desc");
        return findPageWithSql(isPage, page, sqlBuilder.toString());
    }

    /**
     * 获取出账导出客栈信息
     *
     * @param settlementTime
     * @param status
     * @return
     */
    public List<FinanceInnSettlement> findExportInn(String settlementTime, String status) {
        StringBuilder stringBuilder = new StringBuilder("select * from finance_inn_settlement t1 where 1=1");
        if (StringUtils.isNotBlank(settlementTime)) {
            stringBuilder.append(" and t1.settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(status) && status.equals(FinanceInnSettlement.NORMAL_STATUS)) {
            stringBuilder.append(" and t1.is_arrears='0' and t1.is_special=false and t1.settlement_status<>'2'");
        }
        if (status.equals(FinanceInnSettlement.SPECIAL_STATUS)) {
            stringBuilder.append(" and t1.is_arrears='0'  and t1.is_special=TRUE and t1.settlement_status<>'2'");
        }
        if (status.equals(FinanceInnSettlement.DELAY_STATUS)) {
            stringBuilder.append(" and t1.settlement_status='2'");
        }
        if (status.equals(FinanceInnSettlement.LEVEL_ARREARS_STATUS)) {
            stringBuilder.append(" and t1.is_arrears='1' and t1.settlement_status<>'2'");
        }
        if (status.equals(FinanceInnSettlement.PARTIAL_ARREARS_STATUS)) {
            stringBuilder.append(" and t1.is_arrears='2' and t1.settlement_status<>'2'");
        }
        if (status.equals(FinanceInnSettlement.ARREARS_STATUS)) {
            stringBuilder.append(" and t1.is_arrears='3' and t1.settlement_status<>'2'");
        }

        return findWithSql(stringBuilder.toString());
    }

    /**
     * 根据结算时间和是否结算，统计客栈结算
     *
     * @param status           客栈状态
     * @param settlementTime
     * @param settlementStatus
     * @return
     */
    public List<Map<String, Object>> selectFinanceInnSettlementCount(String settlementTime, String settlementStatus, String status) {
        StringBuilder sqlBuilder = new StringBuilder("select count(id) as innCount, COALESCE(sum(total_order),0) as orders,COALESCE(sum(channel_real_settlement),0) as channels,COALESCE(sum(fq_settlement_amount),0) as fqs,COALESCE(sum(inn_settlement_amount),0) as inns,COALESCE(sum(total_amount),0) as total,COALESCE(sum(channel_amount),0) as channelamount, COALESCE(sum(inn_payment),0) as innpayment,COALESCE(sum(refund_amount),0) as refund,COALESCE(sum(fq_replenishment),0) replenishment, COALESCE(sum(after_payment_amount),0) as after,COALESCE(sum(channel_settlement_amount),0) as add1, COALESCE(sum(channel_real_settlement),0) as add2 from finance_inn_settlement t1 where 1=1");
        if (StringUtils.isNotBlank(status)) {
            if (status.equals(FinanceInnSettlement.NORMAL_STATUS)) {
                sqlBuilder.append(" and t1.is_arrears='0' and t1.is_special=false and t1.settlement_status<>'2'");
            }
            if (status.equals(FinanceInnSettlement.SPECIAL_STATUS)) {
                sqlBuilder.append(" and t1.is_arrears='0' and t1.is_special=TRUE and t1.settlement_status<>'2'");
            }
            if (status.equals(FinanceInnSettlement.DELAY_STATUS)) {
                sqlBuilder.append(" and t1.settlement_status='2'");
            }
        }

        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (settlementStatus != null) {
            sqlBuilder.append(" and settlement_status='" + settlementStatus + "'");
        }
        return findListMapWithSql(sqlBuilder.toString());
    }

    /**
     * 统计未结算客栈
     *
     * @param settlementTime
     * @param status
     * @return
     */
    public List<Map<String, Object>> selectUnbalanceFinanceInnSettlementCount(String settlementTime, String status) {
        StringBuilder sqlBuilder = new StringBuilder("select count(id) as inncount, sum(total_order) as orders,sum(after_payment_amount) as inns from finance_inn_settlement t1 where 1=1");
        if (status.equals(FinanceInnSettlement.NORMAL_STATUS)) {
            sqlBuilder.append(" and t1.is_arrears='0' and t1.is_special=false and t1.settlement_status<>'2'");
        }
        if (status.equals(FinanceInnSettlement.SPECIAL_STATUS)) {
            sqlBuilder.append(" and t1.is_arrears='0' and t1.is_special=TRUE and t1.settlement_status<>'2'");
        }
        if (status.equals(FinanceInnSettlement.DELAY_STATUS)) {
            sqlBuilder.append(" and t1.settlement_status='2'");
        }

        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }

        return findListMapWithSql(sqlBuilder.toString());
    }

    /**
     * 查询指定账期未结算客栈数量
     *
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> selectUnConfirmInnCount(String settlementTime) {
        String sql = "select count(id) as total from finance_inn_settlement where settlement_time='" + settlementTime + "' and confirm_status='0' and settlement_status != '2'";
        return findListMapWithSql(sql);
    }

    /**
     * 更新客栈结算状态
     *
     * @param id
     */
    public void updateInnSettlement(Integer id, String settlementStatus) {
        executeUpdateWithSql("UPDATE finance_inn_settlement SET settlement_status='" + settlementStatus + "' WHERE id=" + id);
    }

    /**
     * 更新客栈结算的标注状态
     *
     * @param id       主键ID
     * @param isTagged 是否标注
     */
    public void updateFinanceInnSettlementTag(Integer id, Boolean isTagged) {
        executeUpdateWithSql("UPDATE finance_inn_settlement SET is_tagged=" + isTagged + " WHERE id=" + id);
    }

    /**
     * 根据客栈ID和结算月份，修改确认状态为已确认
     *
     * @param innId
     * @param settlementTime
     */
    public int updateFinanceInnSettlementConfirm(Integer innId, String settlementTime) {
        return executeUpdateWithSql("UPDATE finance_inn_settlement SET confirm_status='" + 1 + "' WHERE inn_id=" + innId + " and settlement_time='" + settlementTime + "'");
    }

    /**
     * 将指定结算月份的账单发送状态改为true
     *
     * @param settlementTime 结算月份
     * @return
     */
    public int updateFinanceInnSettlementBillStatus(String settlementTime) {
        return executeUpdateWithSql("UPDATE finance_inn_settlement SET bill_status=true WHERE settlement_time='" + settlementTime + "'");
    }

    /**
     * 根据客栈ID和结算时间查询结算对象是否存在
     *
     * @param innId
     * @param settlementTime
     * @return
     */
    public FinanceInnSettlement selectFinanceInnSettlement(Integer innId, String settlementTime) {
        List<FinanceInnSettlement> financeInnSettlementList = findWithSql("select * from finance_inn_settlement where inn_id=? and settlement_time=? ", innId, settlementTime);
        if (!CollectionsUtil.isEmpty(financeInnSettlementList)) {
            return financeInnSettlementList.get(0);
        }
        return null;
    }


    /**
     * 根据结算月份查询全部客栈结算记录
     *
     * @param settlementTime
     * @return
     */
    public List<FinanceInnSettlement> selectFinanceInnSettlementBySettlementTime(String settlementTime, boolean isArrears) {
        StringBuilder sql = new StringBuilder("select * from finance_inn_settlement where settlement_time='" + settlementTime + "'");
        if (isArrears) {
            sql.append(" and is_arrears NOT IN ('2','3')");
        }
        return findWithSql(sql.toString());
    }

    /**
     * '
     * 获取所有FinanceInnSettlement对象
     */
    public List<FinanceInnSettlement> selectAllFinanceInnSettlement() {
        return findWithSql("select * from finance_inn_settlement");
    }

    /**
     * 分组查询需要同步客栈结算基本信息的所有客栈ID集合
     *
     * @return
     */
    public List<Map<String, Object>> getInnIdList() {
        return findListMapWithSql("select inn_id as id from finance_inn_settlement GROUP BY inn_id");
    }

    /**
     * 批量删除统计信息中不包含的客栈数据
     *
     * @param settlementTime
     * @param deleteInnIds
     */
    public void batchDeleteFinanceInnSettlement(String settlementTime, String deleteInnIds) {
        executeUpdateWithSql("DELETE from finance_inn_settlement WHERE settlement_time='" + settlementTime + "' and inn_id in (" + deleteInnIds + ")");
    }


    /**
     * 根据主键ID查询客栈对账记录
     *
     * @param id
     * @return
     */
    public FinanceInnSettlement selectFinanceInnSettlementById(Integer id) {
        return findUnique("select fis from FinanceInnSettlement fis where id=" + id);
    }

    /**
     * 查询指定结算月份，未标记，未结算的客栈结算对象集合
     *
     * @param settlementTime   结算月份
     * @param settlementStatus 结算状态（0:未结算，1:已结算,2:纠纷延期）
     * @param isTagged         是否标注
     * @return
     */
    public List<FinanceInnSettlement> selectUnSettlementInn(String settlementTime, String settlementStatus, Boolean isTagged) {
        return findWithSql("SELECT * FROM finance_inn_settlement WHERE settlement_time = '" + settlementTime + "' AND settlement_status = '" + settlementStatus + "' AND is_tagged = " + isTagged);
    }

    /**
     * 批量结算指定月份的客栈出账
     *
     * @param settlementTime
     */
    public void batchUpdateInnSettlement(String settlementTime) {
        executeUpdateWithSql("UPDATE finance_inn_settlement SET settlement_status='1' WHERE settlement_status = '0' and settlement_time='" + settlementTime + "' and is_tagged=FALSE");
    }

    /**
     * 按账期更新客栈确认账单状态
     *
     * @param settlementTime
     */
    public void updateConfirmStatusWithSettlementTime(String settlementTime) {
        executeUpdateWithSql("update finance_inn_settlement SET confirm_status=2 WHERE settlement_time=? AND confirm_status='0' and settlement_status != '2'", settlementTime);
    }

    /**
     * 获取指定账期的FinanceInnSettlement对象
     *
     * @param settlementTime
     * @return
     */
    public List<Map<String, Object>> getInnSettlementAmount(String settlementTime) {
        return findListMapWithSql("select count(id) as totalorder,sum(inn_settlement_amount) as totalamount from finance_inn_settlement where settlement_time=? and is_arrears NOT IN ('2','3')", settlementTime);
    }

    /**
     * 根据客栈ID和账期查询客栈信息
     *
     * @param id
     * @param settlementTime
     * @return
     */
    public FinanceInnSettlement findFinanceInnSettlement(Integer id, String settlementTime) {
        String sql = "select * from finance_inn_settlement fis where inn_id=? and settlement_time=?";
        List<FinanceInnSettlement> withSql = findWithSql(sql, id, settlementTime);
        if (CollectionsUtil.isNotEmpty(withSql)) {
            return withSql.get(0);
        }
        return null;
    }

    /**
     *往期查询挂账客栈信息
     *
     * @param id
     * @param settlementTime
     * @return
     */
    public List<FinanceInnSettlement> findFinanceInnSettlementWithArrears(Integer id, String settlementTime) {
        String sql = "select * from finance_inn_settlement fis where inn_id=? and settlement_time<=? order by settlement_time desc";
        List<FinanceInnSettlement> withSql = findWithSql(sql, id, settlementTime);
        if (CollectionsUtil.isNotEmpty(withSql)) {
            return withSql;
        }
        return null;
    }
    /**
     *后期查询挂账客栈信息
     *
     * @param id
     * @param settlementTime
     * @return
     */
    public List<FinanceInnSettlement> findFinanceInnSettlementNextArrears(Integer id, String settlementTime) {
        String sql = "select * from finance_inn_settlement fis where inn_id=? and settlement_time>? order by settlement_time desc";
        List<FinanceInnSettlement> withSql = findWithSql(sql, id, settlementTime);
        if (CollectionsUtil.isNotEmpty(withSql)) {
            return withSql;
        }
        return null;
    }

    /**
     * 查找挂账客栈
     *
     * @return
     */
    public Page<FinanceInnSettlement> findArrearFinanceInnSettlement(String settlementTime, Page<FinanceInnSettlement> page, String arrearsStatus, String innName) {
        String sql = "SELECT * from finance_inn_settlement WHERE settlement_time=? and is_arrears=? and settlement_status <> '2'";
        if (StringUtils.isNotBlank(innName)) {
            sql += " and inn_name like '%" + innName + "%'";
        }
        return findPageWithSql(page, sql, settlementTime, arrearsStatus);
    }

    /**
     * 查找累计挂账客栈
     *
     * @return
     */
    public Page<FinanceInnSettlement> findTotalArrearFinanceInnSettlement(String settlementTime, Page<FinanceInnSettlement> page, String arrearsStatus, String innName) {
        String sql = "SELECT * from finance_inn_settlement WHERE settlement_time<=? and is_arrears=? and settlement_status <> '2'";
        if (StringUtils.isNotBlank(innName)) {
            sql += " and inn_name like '%" + innName + "%'";
        }
        sql += " order by settlement_time desc";
        return findPageWithSql(page, sql, settlementTime, arrearsStatus);
    }

    /**
     * 查找所有账期之前挂账客栈
     *
     * @return
     */
    public List<FinanceInnSettlement> findTotalArrearsWithSettlementTime(String settlementTime, String arrearsStatus, String innName) {
        String sql = "SELECT * from finance_inn_settlement WHERE settlement_time<=? and is_arrears in('"+FinanceInnSettlement.ARREARS_TAG+"','"+FinanceInnSettlement.PARTIAL_ARREARS_TAG+"' )and settlement_status <> '2'";
        if (StringUtils.isNotBlank(innName)) {
            sql += " and inn_name like '%" + innName + "%'";
        }
        sql += " order by settlement_time desc";
        return findWithSql(sql, settlementTime);
    }

    /**
     * 挂账客栈统计
     *
     * @param settlementTime
     * @param arrearsStatus
     * @return
     */
    public Map<String, Object> selectArrearFinanceInnSettlement(String settlementTime, String arrearsStatus) {
        StringBuilder stringBuilder = new StringBuilder("select count(*) as inns,sum(inn_settlement_amount) as innamount,sum(channel_settlement_amount) as add1,sum(channel_real_settlement) as add2,sum(fq_settlement_amount) as fqa,sum(inn_payment) as payment,sum(refund_amount) as refund,sum(fq_replenishment) as replenishment,sum(after_arrears_amount) as amount,sum(after_payment_amount) as after, sum(arrears_past) as past,sum(arrears_remaining) as remaining from finance_inn_settlement where settlement_status <> '2'");
        if (StringUtils.isNotBlank(settlementTime)) {
            stringBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(arrearsStatus)) {
            if (arrearsStatus.equals(FinanceInnSettlement.LEVEL_ARREARS_STATUS)) {
                stringBuilder.append(" and is_arrears='1'");
            }
            if (arrearsStatus.equals(FinanceInnSettlement.PARTIAL_ARREARS_STATUS)) {
                stringBuilder.append(" and is_arrears='2'");
            }
            if (arrearsStatus.equals(FinanceInnSettlement.ARREARS_STATUS)) {
                stringBuilder.append(" and is_arrears='3'");
            }
        }
        return findMapWithSql(stringBuilder.toString());
    }

    /**
     * 累计挂账客栈统计
     *
     * @param settlementTime
     * @param arrearsStatus
     * @return
     */
    public Map<String, Object> selectTotalArrearFinanceInnSettlement(String settlementTime, String arrearsStatus) {
        StringBuilder stringBuilder = new StringBuilder("select count(*) as inns,sum(inn_settlement_amount) as innamount,sum(channel_settlement_amount) as add1,sum(channel_real_settlement) as add2,sum(fq_settlement_amount) as fqa,sum(inn_payment) as payment,sum(refund_amount) as refund,sum(fq_replenishment) as replenishment,sum(after_arrears_amount) as amount,sum(after_payment_amount) as after, sum(arrears_past) as past,sum(arrears_remaining) as remaining from finance_inn_settlement where settlement_status <> '2'");
        if (StringUtils.isNotBlank(settlementTime)) {
            stringBuilder.append(" and settlement_time<='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(arrearsStatus)) {
            if (arrearsStatus.equals(FinanceInnSettlement.ARREARS_STATUS)) {
                stringBuilder.append(" and is_arrears='3'");
            }
        }
        return findMapWithSql(stringBuilder.toString());
    }

    /**
     * 客栈平账
     */
    public void FinanceLevelArrears(Integer innId, String settlementTime) {
        String sql = "UPDATE finance_inn_settlement SET is_arrears='1' WHERE  inn_id=? and settlement_time=?";
        executeUpdateWithSql(sql, innId, settlementTime);
    }

    /**
     * 统计客栈所有渠道实付金额
     *
     * @param innId
     * @param settlementTime
     * @return
     */
    public Map<String, Object> selectPayment(Integer innId, String settlementTime) {
        String sql = "select SUM(real_payment) as pay FROM finance_inn_channel_settlement WHERE inn_id=? and settlement_time=?";
        return findMapWithSql(sql, innId, settlementTime);
    }

    /**
     * 根据PMS客栈id分组统计查询客栈实际结算金额
     *
     * @param innId PMS客栈ID
     * @param isAll true:查询全部实际结算金额，false：只查询实际结算金额大于0的
     * @return
     */
    public Map<String, Object> selectUnSettlementInfo(Integer innId, boolean isAll) {
        StringBuilder sql = new StringBuilder("SELECT SUM (after_payment_amount) AS amount FROM finance_inn_settlement WHERE inn_id = ? AND settlement_status != '1' AND bill_status = true");
        if (!isAll) {
            sql.append(" AND after_payment_amount > 0");
        }
        return findMapWithSql(sql.toString(), innId);
    }

}
