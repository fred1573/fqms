package com.project.quartz;

import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.google.common.collect.Lists;
import com.project.common.Constants;
import com.project.core.utils.spring.SpringContextHolder;
import com.project.entity.inn.InnFuncReport;
import com.project.entity.plug.InnPlugFunc;
import com.project.service.inn.InnFuncReportManager;
import com.project.service.inn.InnManager;
import com.project.service.plug.InnPlugFuncManager;
import com.project.service.wg.WgOtaInfoManager;
import com.project.utils.time.DateUtil;

public class ReportJob extends QuartzJobBean{

	//在quartz中不能使用openSessionView模式（只针对web请求时会产生作用）,故执行定时任务时无法使用hibernate懒加载模式，即无法使用annotation关系映射（@OneToMany或@ManyToOne等）
	protected void executeInternal(JobExecutionContext arg0) {
		InnFuncReportManager innFuncReportManager = SpringContextHolder.getBean("innFuncReportManager");
		InnManager innManager = SpringContextHolder.getBean("innManager");
		InnPlugFuncManager innPlugFuncManager = SpringContextHolder.getBean("innPlugFuncManager");
		WgOtaInfoManager wgOtaInfoManager = SpringContextHolder.getBean("wgOtaInfoManager");
		Date now = new Date();
		now = DateUtil.parse(DateUtil.format(now)+" 23:59:59", DateUtil.FORMAT_DATE_STR_SECOND);
		List<InnFuncReport> reports = Lists.newArrayList();
		//获取客栈总数
		int innAmount = innManager.getInnAmount();
		//获取账户总数
		int adminAmount = innManager.getAdminAmount(null);
		
		//获取插件开启状况
		List<InnPlugFunc> plugFuncs = innPlugFuncManager.getAll();
		reports.addAll(innFuncReportManager.getPlugFuncsAmount(plugFuncs, now, innAmount));
		//获取锁屏的使用情况
		reports.add(innFuncReportManager.getLockScreenAmount(now, innAmount));
		//获取房型排序的使用情况
		reports.add(innFuncReportManager.getRoomSortAmount(now, innAmount));
		//获取房态风格的使用情况
		reports.addAll(innFuncReportManager.getRoomStyleAmount(now, adminAmount));
		//获取通知的使用状况
		reports.add(innFuncReportManager.getLogAmount(now, innAmount, Constants.LOG_TYPE_ADVICE
				, Constants.REPORT_ITEM_TYPE_NOTICE));
		//获取OTA打通情况
		List<Integer> otaIds = wgOtaInfoManager.getOtaIds();
		reports.addAll(innFuncReportManager.getOtaAmount(now, innAmount, otaIds));
		//获取活跃用户数
		reports.add(innFuncReportManager.getActiveAmount(now, innAmount));
		//获取连锁运营情况
		reports.add(innFuncReportManager.getChainStoreAmount(now, innAmount));
		
		//保存每日统计的情况
		innFuncReportManager.save(reports);
		
	}

}
