package com.project.service.common;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzService {
	private static Logger logger = LoggerFactory.getLogger(QuartzService.class);
	private final static String JOB_NAME = "_defaultJobName_";
	private final static String TRIGGER_NAME = "_defaultTriggerName_";
	private final static String JOB_GROUP_NAME = "_defaultJobGroup_";
	private final static String TRIGGER_GROUP_NAME = "_defaultTriggerGroup_";
	private static QuartzService instance = null;
	private static SchedulerFactory sf = new StdSchedulerFactory();
	private static Scheduler scheduler;

	static {
		try {
			scheduler = sf.getScheduler();
		} catch (SchedulerException e) {
			logger.error("初始化任务调度对象失败！", e);
		}
	}

	public static QuartzService getInstance() {
		if (instance == null) {
			instance = new QuartzService();
		}
		return instance;
	}
	
	//---------------------------------------添加指定时间运行的作业调度------------------------------------------------//
	/**
	 * 新增立即执行任务
	 * @param name
	 * @param job
	 * @param time
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public void addJob(String name, Job job, JobDataMap map) throws SchedulerException {
		this.addJob(JOB_NAME+name, JOB_GROUP_NAME+name, TRIGGER_NAME+name, TRIGGER_GROUP_NAME+name, job, map);
	}

	/**
	 * 新增立即执行任务
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param job
	 * @param time
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Job job, JobDataMap map)
			throws SchedulerException {
		// 任务
		JobDetail jobDetail = null;
		if(map!=null){
			jobDetail = JobBuilder.newJob(job.getClass())// 任务执行类
				.withIdentity(jobName, jobGroupName)// 任务名，任务组
				.usingJobData(map)
				.build();
		}else{
			jobDetail = JobBuilder.newJob(job.getClass())// 任务执行类
				.withIdentity(jobName, jobGroupName)// 任务名，任务组
				.build();
		}
		// 触发器
		SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
				.withIdentity(triggerName, triggerGroupName)
				.startNow()
				.build();
		scheduler.scheduleJob(jobDetail, simpleTrigger);
		// 启动
		start();
	}
	
	/**
	 * 新增定时任务
	 * @param name
	 * @param job
	 * @param time
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public void addJob(String name, Job job, Date time, JobDataMap map) throws SchedulerException {
		this.addJob(JOB_NAME+name, JOB_GROUP_NAME+name, TRIGGER_NAME+name, TRIGGER_GROUP_NAME+name, job, time, map);
	}

	/**
	 * 新增定时任务
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param job
	 * @param time
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Job job, Date time, JobDataMap map)
			throws SchedulerException {
		// 任务
		JobDetail jobDetail = null;
		if(map!=null){
			jobDetail = JobBuilder.newJob(job.getClass())// 任务执行类
				.withIdentity(jobName, jobGroupName)// 任务名，任务组
				.usingJobData(map)
				.build();
		}else{
			jobDetail = JobBuilder.newJob(job.getClass())// 任务执行类
				.withIdentity(jobName, jobGroupName)// 任务名，任务组
				.build();
		}
		// 触发器
		SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
				.withIdentity(triggerName, triggerGroupName)
				.startAt(time)
				.build();
		scheduler.scheduleJob(jobDetail, simpleTrigger);
		// 启动
		start();
	}

	/**
	 * 修改定时任务
	 * @param name
	 * @param time
	 * @throws SchedulerException
	 */
	public void modifyJob(String name, Date time, JobDataMap map) throws SchedulerException {
		this.modifyJob(JOB_NAME+name, JOB_GROUP_NAME+name, TRIGGER_NAME+name, TRIGGER_GROUP_NAME+name, time, map);
	}
	
	/**
	 * 修改定时任务
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param cronExpression
	 * @throws SchedulerException
	 */
	public void modifyJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Date time, JobDataMap map)
			throws SchedulerException {
		JobKey jk = new JobKey(jobName, jobGroupName);
		// 任务
		JobDetail jobDetail = scheduler.getJobDetail(jk);
		removeJob(jobName,jobGroupName,triggerName,triggerGroupName);
		//只有在需要修改的定时任务未完成时
		if(jobDetail != null){
			// 新任务
			if(map!=null){
				jobDetail = JobBuilder.newJob(jobDetail.getJobClass())// 任务执行类
					.withIdentity(jobName, jobGroupName)// 任务名，任务组
					.usingJobData(map)
					.build();
			}else{
				jobDetail = JobBuilder.newJob(jobDetail.getJobClass())// 任务执行类
					.withIdentity(jobName, jobGroupName)// 任务名，任务组
					.build();
			}
			// 新触发器
			SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
					.withIdentity(triggerName, triggerGroupName)
					.startAt(time)
					.build();
			scheduler.scheduleJob(jobDetail, simpleTrigger);
			// 启动
			start();
		}
	}

	//---------------------------------------添加表达式形式的作业调度------------------------------------------------//
	/**
	 * 新增表达式任务
	 * @param name
	 * @param job
	 * @param cronExpression
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public void addJob(String name, Job job, String cronExpression, JobDataMap map) throws SchedulerException {
		this.addJob(JOB_NAME+name, JOB_GROUP_NAME+name, TRIGGER_NAME+name, TRIGGER_GROUP_NAME+name, job, cronExpression, map);
	}

	/**
	 * 新增表达式任务
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param job
	 * @param cronExpression
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Job job, String cronExpression, JobDataMap map)
			throws SchedulerException {
		// 任务
		JobDetail jobDetail = null;
		if(map!=null){
			jobDetail = JobBuilder.newJob(job.getClass())// 任务执行类
				.withIdentity(jobName, jobGroupName)// 任务名，任务组
				.usingJobData(map)
				.build();
		}else{
			jobDetail = JobBuilder.newJob(job.getClass())// 任务执行类
				.withIdentity(jobName, jobGroupName)// 任务名，任务组
				.build();
		}
		// 触发器
		CronTrigger cronTrigger = TriggerBuilder.newTrigger()
				.withIdentity(triggerName, triggerGroupName)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
		scheduler.scheduleJob(jobDetail, cronTrigger);
		// 启动
		start();
	}

	/**
	 * 修改表达式任务
	 * @param name
	 * @param cronExpression
	 * @throws SchedulerException
	 */
	public void modifyJob(String name, String cronExpression, JobDataMap map) throws SchedulerException {
		this.modifyJob(JOB_NAME+name, JOB_GROUP_NAME+name, TRIGGER_NAME+name, TRIGGER_GROUP_NAME+name, cronExpression, map);
	}
	
	/**
	 * 修改表达式任务
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param cronExpression
	 * @throws SchedulerException
	 */
	public void modifyJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, String cronExpression, JobDataMap map)
			throws SchedulerException {
		JobKey jk = new JobKey(jobName, jobGroupName);
		// 任务
		JobDetail jobDetail = scheduler.getJobDetail(jk);
		removeJob(jobName,jobGroupName,triggerName,triggerGroupName);
		//只有在需要修改的定时任务未完成时
		if(jobDetail != null){
			// 新任务
			jobDetail = JobBuilder.newJob(jobDetail.getJobClass())// 任务执行类
					.withIdentity(jobName, jobGroupName)// 任务名，任务组
					.usingJobData(map)
					.build();
			// 新触发器
			CronTrigger cronTrigger = TriggerBuilder.newTrigger()
					.withIdentity(triggerName, triggerGroupName)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
	
			scheduler.scheduleJob(jobDetail, cronTrigger);
			// 启动
			start();
		}
	}

	//---------------------------------------删除作业调度------------------------------------------------//
	public void removeJob(String name) throws SchedulerException {
		this.removeJob(JOB_NAME+name, JOB_GROUP_NAME+name, TRIGGER_NAME+name, TRIGGER_GROUP_NAME+name);
	}

	public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) throws SchedulerException {
		TriggerKey tk = new TriggerKey(triggerName, triggerGroupName);
		scheduler.pauseTrigger(tk);// 停止触发器
		scheduler.unscheduleJob(tk);// 移除触发器
		JobKey jk = new JobKey(jobName, jobGroupName);
		scheduler.deleteJob(jk);// 删除任务
	}
	
	public void removeAll() throws SchedulerException {
		Collection<Scheduler> list = sf.getAllSchedulers();
		Iterator<Scheduler> it = list.iterator();
		while (it.hasNext()) {
			Scheduler sc = it.next();
			if (sc != null) {
				sc.clear();
			}
		}
	}

	public void start() throws SchedulerException {
		if(!scheduler.isShutdown())
			scheduler.start();
	}

	public void stop() throws SchedulerException {
		scheduler.shutdown(true);
	}
	
	/*//系统启动时,从数据库中读取提醒数据，set到作业调度程序中
	public void startJobScheduler() throws SchedulerException {
		// 开启任务调度
		this.start();
		List<InnRemind> irs = innRemindDao.finder.where().eq("status", Constants.REMIND_STATUS_NORMAL).findList();
		this.toJobSchedulerFromInnReminds(null,irs);
	}
	
	//将提醒加入作业调度中
	public void toJobSchedulerFromInnReminds(String mobile,List<InnRemind> irs) {
		if(ListUtil.isNotEmpty(irs)){	
			for(InnRemind ir : irs){
				if(!"".equals(ir.title) && ir.remindTime!=null){
					JobDataMap map = new JobDataMap();
					map.put("innRemind", ir);
					map.put("mobile", mobile);
					try {
						this.removeJob(Constants.PUSH_TYPE_REMIND+ir.id.toString());
						this.addJob(Constants.PUSH_TYPE_REMIND+ir.id.toString(), new RemindJob(), ir.remindTime, map);
					} catch (SchedulerException e) {
						Logger.error(ir.id.toString()+"加入任务调度失败！", e);
					}
				}
			}
		}
	}*/
	
}
