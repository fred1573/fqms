package com.project.cache.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.cache.abstractCache.IDataCache;

public class LoadCacheDataManager {
	private static Map<String, IDataCache> caches = new HashMap<String, IDataCache>();

	public static void put(IDataCache cache) {
		caches.put(cache.getClass().getSimpleName(), cache);
	}

	public static IDataCache get(String name) {
		return ((IDataCache) caches.get(name));
	}

	public static void remove(String name) {
		caches.remove(name);
	}

	public static void clear(String name) {
		caches.clear();
	}

	public void setCaches(List<IDataCache> caches) {
		for (IDataCache dataCache : caches)
			put(dataCache);
	}

	public static Collection<IDataCache> getDataCaches() {
		return caches.values();
	}
	
}