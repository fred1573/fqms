package com.project.quartz;

/**
 * Created by admin on 2016/5/20.
 */

import com.project.core.utils.spring.SpringContextHolder;
import com.project.entity.operation.OperationActivity;
import com.project.service.operation.OperationActivityService;
import com.project.utils.CollectionsUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 运营活动结束定时任务
 */
public class OperationActivityJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationActivityJob.class);


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("活动截止日期定时任务开始");
        OperationActivityService operationActivityService = SpringContextHolder.getBean("operationActivityService");
        List<OperationActivity> activities = operationActivityService.findActivities();
        if (CollectionsUtil.isNotEmpty(activities)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            for (OperationActivity operationActivity : activities) {
                String dateLine = operationActivity.getDateLine();
                try {
                    Date date = format.parse(dateLine);
                    if (date.before(new Date())) {
                        operationActivityService.finishActivityJob(operationActivity.getId());
                    }
                } catch (Exception e) {
                    LOGGER.error("活动结束定时任务" + operationActivity.getActivityName() + "出现异常");
                    e.printStackTrace();
                }
            }
        }
        LOGGER.info("活动截止日期定时任务结束");
    }
}
