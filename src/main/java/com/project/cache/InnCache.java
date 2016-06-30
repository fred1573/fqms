package com.project.cache;

import com.project.cache.abstractCache.AbstractEhCache;
import com.project.common.Constants;
import com.project.core.utils.spring.SpringContextHolder;
import com.project.dao.inn.InnDao;

/**
 * 所有客栈缓存
 * @author mowei
 *
 */
public class InnCache extends AbstractEhCache {
	
	/**
	 * 根据缓存name获取相应的cache配置，如果获取失败，使用默认sysCache；如果不写构造方法，也使用默认的sysCache
	 */
	public InnCache(){
		cache = cacheManager.getCache("innCache")==null?cacheManager.getCache("sysCache"):cacheManager.getCache("innCache");
	}

	public void loadCache() {
		InnDao innDao = SpringContextHolder.getBean("innDao");
		put(Constants.CACHE_FLAG_ALL_INN, innDao.getAll("registeredAt", false));
	}

}
