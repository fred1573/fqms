package com.project.quartz;


import java.util.Date;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.project.core.utils.spring.SpringContextHolder;
import com.project.service.inn.InnManager;
import com.project.utils.time.DateUtil;

public class RegionReportJob extends QuartzJobBean{

	//在quartz中不能使用openSessionView模式（只针对web请求时会产生作用）,故执行定时任务时无法使用hibernate懒加载模式，即无法使用annotation关系映射（@OneToMany或@ManyToOne等）
	protected void executeInternal(JobExecutionContext arg0) {
		InnManager innManager = SpringContextHolder.getBean("InnManager");
		Date now = new Date();
		innManager.saveRegionReports(DateUtil.format(now));
	}

}
