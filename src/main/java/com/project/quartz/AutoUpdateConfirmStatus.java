package com.project.quartz;

import com.project.core.utils.spring.SpringContextHolder;
import com.project.service.finance.PeriodService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by admin on 2016/1/7.
 */
public class AutoUpdateConfirmStatus extends QuartzJobBean {

    private final static Logger LOGGER= LoggerFactory.getLogger(AutoUpdateConfirmStatus.class);

    /**
     * 定时自动更新客栈确认账单已发送状态
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        PeriodService periodService= SpringContextHolder.getBean("periodService");
        //获取最近账期
        LOGGER.info("系统自动确认账单定时任务开始执行");
        periodService.autoUpdateConfirmStatus();
        LOGGER.info("系统自动确认账单定时任务执行结束");
    }
}
