package com.project.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TestListener implements ServletContextListener{

	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	/*服务器启动时获取任务信息启动job*/
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		
	
	}

}
