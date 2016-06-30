package com.project.quartz;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.project.core.utils.spring.SpringContextHolder;
import com.project.dao.account.UserDao;
import com.project.dao.common.JdbcThinDao;
import com.project.entity.account.Role;
import com.project.entity.account.User;
import com.project.service.account.AccountService;
import com.project.service.common.JdbcThinManager;

/**
 * 被Spring的Quartz JobDetailBean定时执行的Job类, 支持持久化到数据库实现Quartz集群.
 * 
 * 因为需要被持久化, 不能有用XXManager等不能被持久化的成员变量, 
 * 只能在每次调度时从QuartzJobBean注入的applicationContext中动态取出.
 */
public class TimeJob extends QuartzJobBean {

	private static Logger logger = LoggerFactory.getLogger(TimeJob.class);
	private static int count = 0;
	
	/**
	 * 定时打印当前用户数到日志.
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0) {
		AccountService accountService = SpringContextHolder.getBean("accountManager");
		User u = accountService.findUserByUserCode("root");
		UserDao userDao = SpringContextHolder.getBean("userDao");
		u = userDao.selectBySysUserCode("root");
		u = userDao.selectById(1L);
		JdbcThinManager jdbcThinManager= SpringContextHolder.getBean("jdbcThinManager");
		List<Map<String, Object>> map = jdbcThinManager.findUserBySql(1);
		JdbcThinDao jdbcThinDao = SpringContextHolder.getBean("jdbcThinDao");
		map = jdbcThinDao.executeQuery("select * from TOMATO_SYS_USER where id = ? ", 1);
		logger.debug("TimeJob!" + arg0.getFireInstanceId() + "--"
				+ arg0.getFireTime() + "---" + arg0.getNextFireTime() + "---"
				+ count++ + map);
	}
}
