package com.project.quartz;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class CronJob extends QuartzJobBean{

	private static Logger logger = LoggerFactory.getLogger(CronJob.class);
	private static int count = 0;
	
	//在quartz中不能使用openSessionView模式（只针对web请求时会产生作用）,故执行定时任务时无法使用hibernate懒加载模式，即无法使用annotation关系映射（@OneToMany或@ManyToOne等）
	protected void executeInternal(JobExecutionContext arg0) {
//		UserManager userManager = SpringContextHolder.getBean("userManager");
//		SysDictionaryManager sysDictionaryManager = SpringContextHolder.getBean("sysDictionaryManager");
		
		logger.info("CronJob!" + arg0.getFireInstanceId() + "--"
				+ arg0.getFireTime() + "---" + arg0.getNextFireTime() + "---"
				+ count++ + "---");
		
//		sysDictionaryManager.findByUserCode();
//		User u = userManager.findByUserCode("root");
//		System.out.println("--"+u.toString());
		
//		System.out.println("CronJob!" + arg0.getFireInstanceId() + "--"
//				+ arg0.getFireTime() + "---" + arg0.getNextFireTime() + "---"
//				+ count++ + "---" + sysDictionaryManager.getSysDictionary(200l));
	}

}
