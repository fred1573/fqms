package com.project.cache;

import java.util.ArrayList;
import java.util.List;

import com.project.cache.abstractCache.AbstractEhCache;

/**
 * test缓存使用方法
 * @author mowei
 *
 */
public class TestCache extends AbstractEhCache {
	
	
	/**
	 * 根据缓存name获取相应的cache配置，如果获取失败，使用默认sysCache；如果不写构造方法，也使用默认的sysCache
	 */
	public TestCache(){
		cache = cacheManager.getCache("testCache")==null?cacheManager.getCache("sysCache"):cacheManager.getCache("testCache");
	}

	public void loadCache() {
		List testList = new ArrayList();
		put("testKey", testList);
	}

}
