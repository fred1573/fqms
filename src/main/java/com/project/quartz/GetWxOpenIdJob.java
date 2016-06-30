package com.project.quartz;

import com.project.core.utils.spring.SpringContextHolder;
import com.project.entity.finance.FinanceInnSettlementInfo;
import com.project.service.finance.FinanceOrderService;
import com.project.utils.CollectionsUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;


/**
 * Created by admin on 2015/12/11.
 */
public class GetWxOpenIdJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetWxOpenIdJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        FinanceOrderService financeOrderService = SpringContextHolder.getBean("financeOrderService");
        Long beginTime = System.currentTimeMillis();
        List<FinanceInnSettlementInfo> innSettlementInfoList = financeOrderService.getAllInnSettlementInfo();
        if (CollectionsUtil.isNotEmpty(innSettlementInfoList)) {
            for (FinanceInnSettlementInfo financeInnSettlementInfo : innSettlementInfoList) {
                financeOrderService.synchronizationInnInfo(financeInnSettlementInfo);
            }
        }
        Long endTime = System.currentTimeMillis();
        Long time = endTime - beginTime;
        LOGGER.info("同步客栈结算数据共耗时：" + time + "毫秒");
    }
}
