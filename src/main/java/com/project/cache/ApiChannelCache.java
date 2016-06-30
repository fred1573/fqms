package com.project.cache;

import com.project.cache.abstractCache.AbstractEhCache;
import com.project.common.Constants;
import com.project.core.utils.spring.SpringContextHolder;
import com.project.dao.api.ApiChannelDao;

/**
 * 所有第三方渠道缓存
 * @author X
 *
 */
public class ApiChannelCache extends AbstractEhCache {
	
	/**
	 * 根据缓存name获取相应的cache配置，如果获取失败，使用默认sysCache；如果不写构造方法，也使用默认的sysCache
	 */
	public ApiChannelCache(){
		cache = cacheManager.getCache("sysApiChannelCache")==null?cacheManager.getCache("sysCache"):cacheManager.getCache("sysApiChannelCache");
	}

	public void loadCache() {
		ApiChannelDao apiChannelDao = SpringContextHolder.getBean("apiChannelDao");
		put(Constants.CACHE_FLAG_ALL_CHANNEL, apiChannelDao.getAll("createdAt",true));
	}

}
