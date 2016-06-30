package com.project.quartz;

import com.project.core.utils.spring.SpringContextHolder;
import com.project.entity.finance.FinanceParentOrder;
import com.project.service.finance.FinanceOrderService;
import com.project.service.finance.PeriodService;
import com.project.utils.FinanceHelper;
import com.project.utils.time.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by admin on 2015/12/22.
 */
public class GrabFinanceOrderForNowMonthJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrabFinanceOrderForNowMonthJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        FinanceHelper financeHelper = SpringContextHolder.getBean("financeHelper");
        FinanceOrderService financeOrderService = SpringContextHolder.getBean("financeOrderService");
        PeriodService periodService = SpringContextHolder.getBean("periodService");
        // 查询最近的账期
        String recentlySettlementTime = periodService.getRecentlySettlementTime();
        if(StringUtils.isBlank(recentlySettlementTime)) {
            LOGGER.error("获取最近账期失败，抓取当月订单定时任务异常终止");
            return;
        }
        if(recentlySettlementTime.indexOf("至") < -1) {
            LOGGER.error("最近账期格式错误，抓取当月订单定时任务异常终止");
            return;
        }
        String beginDate = getBeginDate(recentlySettlementTime);
        LOGGER.info("开始同步【" + beginDate + "】订单定时任务");
        List<FinanceParentOrder> financeOrderFromOMS = financeHelper.getCheckOutForNowMonthFinanceOrderFromOMS(beginDate);
        if (!CollectionUtils.isEmpty(financeOrderFromOMS)) {
            for (FinanceParentOrder financeParentOrder : financeOrderFromOMS) {
                financeOrderService.createFinanceOrder(financeParentOrder);
            }
        }
        LOGGER.info("【" + beginDate + "】订单抓取定时任务执行完成");
    }

    private String getBeginDate(String recentlySettlementTime) {
        String[] split = recentlySettlementTime.split("至");
        // 获取同步订单的开始时间
        return DateUtil.format(DateUtil.addDay(DateUtil.parse(split[1]), 1));
    }

}

