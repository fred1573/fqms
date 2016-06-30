package com.project.cache;

import com.project.cache.abstractCache.AbstractEhCache;
import com.project.common.Constants;
import com.project.core.utils.spring.SpringContextHolder;
import com.project.dao.account.AuthorityDao;

/**
 * 所有权限缓存
 * @author momo
 *
 */
public class AuthorityCache extends AbstractEhCache {
	
	/**
	 * 根据缓存name获取相应的cache配置，如果获取失败，使用默认sysCache；如果不写构造方法，也使用默认的sysCache
	 */
	public AuthorityCache(){
		cache = cacheManager.getCache("sysConstantCache")==null?cacheManager.getCache("sysCache"):cacheManager.getCache("sysConstantCache");
	}

	public void loadCache() {
		AuthorityDao authorityDao = SpringContextHolder.getBean("authorityDao");
		put(Constants.CACHE_FLAG_ALL_AUTHORITY, authorityDao.selectAllCreateTimeAsc());
	}

}
