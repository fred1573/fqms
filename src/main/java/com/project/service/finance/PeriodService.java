package com.project.service.finance;

import com.project.core.orm.Page;
import com.project.entity.finance.FinanceAccountPeriod;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 账期业务逻辑层
 * Created by sam on 2015/12/30.
 */
@Component
@Transactional
public interface PeriodService {
    /**
     * 查询结算周期列表
     * @return
     */
    List<FinanceAccountPeriod> findAllFinanceAccountPeriod();

    /**
     * 按页查询结算周期列表
     *
     * @return
     */
    Page<FinanceAccountPeriod> selectProxysalePeriod(Page<FinanceAccountPeriod> page);

    /**
     * 按账期抓取订单任务
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    void updateFinanceOrderByPeriod(String beginDate, String endDate);

    /**
     * 根据开始、结束日期同步订单所有原始属性
     * @param beginDate
     * @param endDate
     */
    void updateFinanceOrder(String beginDate, String endDate);

    /**
     * 获取最近账期
     * @return
     */
    String getRecentlySettlementTime();

    /**
     * 获取最后账单推送操作时间
     * @param settlementTime
     * @return
     */
    Date getOperateTime(String settlementTime);

    /**
     * 按账期更新客栈确认状态
     * @param recentlySettlementTime
     */
    void updateConfirmStatusWithSettlementTime(String recentlySettlementTime);

    /**
     * 系统自动更新确认状态
     */
    void autoUpdateConfirmStatus();

    /**
     * 同步客栈结算表中所有数据到客栈结算进本信息表中
     */
    void updateInnInfo();
}
