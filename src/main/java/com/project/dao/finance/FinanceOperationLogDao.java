package com.project.dao.finance;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.finance.FinanceOperationLog;
import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 财务对账操作日志持久化操作对象
 * Created by 番茄桑 on 2015/9/23.
 */
@Component("financeOperationLogDao")
public class FinanceOperationLogDao extends HibernateDao<FinanceOperationLog, Integer> {

    /**
     * 根据查询条件分页查询操作记录
     *
     * @param page           分页对象
     * @param keyWord        搜索关键字（匹配客栈名称和渠道名称）
     * @param settlementTime 结算月份
     * @param startDate      操作开始时间
     * @param endDate        操作结束时间
     * @param operateType    操作类型
     * @return
     */
    public Page<FinanceOperationLog> findFinanceOperationLogList(Page<FinanceOperationLog> page, String keyWord, String settlementTime, String startDate, String endDate, String operateType) {
        StringBuilder sqlBuilder = new StringBuilder("select * from finance_operation_log where operate_type in ('1','2','3','4','8','9','10','201')");
        if (StringUtils.isNotBlank(keyWord)) {
            sqlBuilder.append(" and (inn_name like '%" + keyWord + "%' or channel_name like '%" + keyWord + "%' or operate_content like '%" + keyWord + "%')");
        }
        if (StringUtils.isNotBlank(settlementTime)) {
            sqlBuilder.append(" and settlement_time='" + settlementTime + "'");
        }
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            sqlBuilder.append(" and (operate_time between '" + startDate + " 00:00:00' and '" + endDate + " 23:59:59')");
        }
        if (StringUtils.isNotBlank(operateType)) {
            sqlBuilder.append(" and operate_type='" + operateType + "'");
        }
        sqlBuilder.append(" order by operate_time desc");
        return findPageWithSql(page, sqlBuilder.toString());
    }

    /**
     * 根据分页对象、客栈名称、开始日期、结束日期和操作类型分页查询代销操作记录
     *
     * @param page        分页对相同
     * @param innName     客栈名称（支持模糊查询）
     * @param startDate   搜索的开始时间
     * @param endDate     搜索的结束时间
     * @param operateType 操作类型
     * @return 符合搜索条件的查询结果
     */
    public Page<FinanceOperationLog> selectProxySaleOperationLogList(Page<FinanceOperationLog> page, String innName, String startDate, String endDate, String operateType) {
        StringBuilder sqlBuilder = new StringBuilder("select * from finance_operation_log where operate_type like '1__'");
        if (StringUtils.isNotBlank(innName)) {
            sqlBuilder.append(" and (operate_object like '%").append(innName).append("%')");
        }
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            sqlBuilder.append(" and (operate_time between '").append(startDate).append(" 00:00:00' and '").append(endDate).append(" 23:59:59')");
        }
        if (StringUtils.isNotBlank(operateType)) {
            sqlBuilder.append(" and operate_type='").append(operateType).append("'");
        }
        sqlBuilder.append(" order by operate_time desc");
        return findPageWithSql(page, sqlBuilder.toString());
    }

    /**
     * 查询账期是否发送账单
     * @return
     */
    public FinanceOperationLog findFinanceOperationLogWithSettlementTime(String settlementTime,String operateType){
        List<FinanceOperationLog> list=findWithSql("SELECT * FROM finance_operation_log WHERE settlement_time=? and operate_type=? ORDER BY settlement_time DESC",settlementTime,operateType);
        if(CollectionsUtil.isNotEmpty(list)){
            return list.get(0);
        }
        return  null;
    }
}
