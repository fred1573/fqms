package com.project.cache;

import com.project.cache.abstractCache.AbstractEhCache;
import com.project.common.Constants;
import com.project.core.utils.spring.SpringContextHolder;
import com.project.dao.inn.InnRegionDao;

/**
 * 所有区域缓存
 * @author mowei
 *
 */
public class RegionCache extends AbstractEhCache {
	
	/**
	 * 根据缓存name获取相应的cache配置，如果获取失败，使用默认sysCache；如果不写构造方法，也使用默认的sysCache
	 */
	public RegionCache(){
		cache = cacheManager.getCache("sysConstantCache")==null?cacheManager.getCache("sysCache"):cacheManager.getCache("sysConstantCache");
	}

	public void loadCache() {
		InnRegionDao innRegionDao = SpringContextHolder.getBean("innRegionDao");
		put(Constants.CACHE_FLAG_ALL_REGION, innRegionDao.getAll());
	}

}
