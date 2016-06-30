package com.project.cache.abstractCache;

public abstract interface IDataCache {

	/**
	 * 通过key值获取缓存对象
	 * @param paramObject
	 * @return
	 */
	public abstract Object get(Object paramObject);
	
	/**
	 * 通过key值获取缓存对象，如果没有获取到，则从数据库中重新获取
	 * @param paramObject
	 * @return
	 */
	public abstract Object getOrElse(Object paramObject);

	public abstract void put(Object paramObject1, Object paramObject2);

	public abstract void remove(Object paramObject);

	public abstract void clear();

	public abstract void loadCache();
}