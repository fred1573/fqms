package com.project.cache.abstractCache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public abstract class AbstractEhCache implements IDataCache {
	protected static CacheManager cacheManager = CacheManager.create();
	protected Cache cache;

	public AbstractEhCache() {
		this.cache = cacheManager.getCache("sysCache");
	}

	public void clear() {
		this.cache.removeAll();
	}

	public void put(Object key, Object value) {
		this.cache.put(new Element(key, value));
	}

	public void remove(Object key) {
		this.cache.remove(key);
	}

	public Object get(Object key) {
		Element element = this.cache.get(key);
		if (element == null) {
			return null;
		}
		return element.getObjectValue();
	}
	
	public Object getOrElse(Object key) {
		Element element = this.cache.get(key);
		if (element == null) {
			loadCache();
			element = this.cache.get(key);
			if (element == null) {
				return null;
			}
		}
		return element.getObjectValue();
	}
	
}