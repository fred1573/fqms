package com.project.cache.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.project.cache.abstractCache.IDataCache;
import com.project.cache.manager.LoadCacheDataManager;
import com.project.core.utils.spring.SpringContextHolder;

/**
 * 系统启动提前加载的缓存项
 * @author mowei
 *
 */
public class CacheListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {

	}

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			LoadCacheDataManager manager = SpringContextHolder.getBean("sysCacheManager");
			for (IDataCache dataCache : manager.getDataCaches()) {
				dataCache.loadCache();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
