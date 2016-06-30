package com.project.utils;

import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemcachedUtil {
	
	private static Logger logger = LoggerFactory.getLogger(MemcachedUtil.class);

	private static MemcachedClient memcachedClient = null;
	
	private static int connectionPoolSize = 50;

	private static long connectTimeout = 60;
	
	public static long expireTime = 1800;
	
	static{
		getMemcachedClient();
	}
	
	private static MemcachedClient getMemcachedClient(){
		try {
			MemcachedClientBuilder builder = new XMemcachedClientBuilder(ResourceBundleUtil.getString("memcached.host"));
			// 设置连接池大小，即客户端个数
			builder.setConnectionPoolSize(connectionPoolSize);
			// 宕机报警
			builder.setFailureMode(true); 
			// 使用二进制文件
			builder.setCommandFactory(new BinaryCommandFactory());
			builder.setConnectTimeout(connectTimeout);
			memcachedClient = builder.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return memcachedClient; 
	}
	
	public static synchronized MemcachedClient getClientInstance() {
		if(memcachedClient == null){ 
			memcachedClient = getMemcachedClient();
		}
		return memcachedClient;
	}
	
	/**
	 * Get方法, 转换结果类型并屏蔽异常, 仅返回Null.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		try {
			return (T) memcachedClient.get(key);
		} catch (TimeoutException | InterruptedException
				| MemcachedException e) {
			handleException(e, key);
			return null;
		}
	}
	
	/**
	 * 异步Set方法, 不考虑执行结果.
	 */
	public static void setWithNoReply(String key, int expiredTime, Object value) {
		try {
			memcachedClient.setWithNoReply(key, expiredTime, value);
		} catch (InterruptedException | MemcachedException e) {
			handleException(e, key);
		}
	}
	
	/**
	 * 安全的Set方法, 保证在connectTimeout秒内返回执行结果, 否则返回false并取消操作.
	 */
	public static boolean set(String key, int expiredTime, Object value) {
		try {
			return memcachedClient.set(key, expiredTime, value);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			handleException(e, key);
			return false;
		}
	}
	
	/**
	 * 异步replace方法, 不考虑执行结果.
	 */
	public static void replaceWithNoReply(String key, int expiredTime, Object value) {
		try {
			memcachedClient.replaceWithNoReply(key, expiredTime, value);
		} catch (InterruptedException | MemcachedException e) {
			handleException(e, key);
		}
	}
	
	/**
	 * 安全的replace方法, 保证在connectTimeout秒内返回执行结果, 否则返回false并取消操作.
	 */
	public static boolean replace(String key, int expiredTime, Object value) {
		try {
			return memcachedClient.replace(key, expiredTime, value);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			handleException(e, key);
			return false;
		}
	}

	/**
	 * 异步 Delete方法, 不考虑执行结果.
	 */
	public static void deleteWithNoReply(String key) {
		try {
			memcachedClient.deleteWithNoReply(key);
		} catch (InterruptedException | MemcachedException e) {
			handleException(e, key);
		}
	}
	
	/**
	 * 安全的Delete方法, 保证在connectTimeout秒内返回执行结果, 否则返回false并取消操作.
	 */
	public static boolean delete(String key) {
		try {
			return memcachedClient.delete(key);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			handleException(e, key);
			return false;
		}
	}
	
	/**
	 * 异步 Incr方法, 不考虑执行结果.
	 */
	public static void incrWithNoReply(String key, long by) {
		try {
			memcachedClient.incrWithNoReply(key, by);
		} catch (InterruptedException | MemcachedException e) {
			handleException(e, key);
		}
	}
	
	/**
	 * Incr方法, 若key不存在返回-1.
	 */
	public static long incr(String key, long by, long defaultValue) {
		try {
			return memcachedClient.incr(key, by, defaultValue);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			handleException(e, key);
			return -1;
		}
	}

	/**
	 * Decr方法, 不考虑执行结果.
	 */
	public static void decrWithNoReply(String key, long by) {
		try {
			memcachedClient.decrWithNoReply(key, by);
		} catch (InterruptedException | MemcachedException e) {
			handleException(e, key);
		}
	}
	
	/**
	 * 异步Incr方法, 若key不存在返回-1.
	 */
	public static long decr(String key, long by, long defaultValue) {
		try {
			return memcachedClient.decr(key, by, defaultValue);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			handleException(e, key);
			return -1;
		}
	}
	
	/**
	 * 异步 flushAll方法, 不考虑执行结果.
	 */
	public static void flushAllNoReply() {
		try {
			memcachedClient.flushAllWithNoReply();
		} catch (InterruptedException | MemcachedException e) {
			handleException(e, "no key");
		}
	}
	
	/**
	 * 安全的flushAll方法
	 */
	public static void flushAll() {
		try {
			memcachedClient.flushAll();
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			handleException(e, "no key");
		}
	}
	

	private static void handleException(Exception e, String key) {
		logger.warn("xmemcached client receive an exception with key:" + key, e);
	}

	public static void setConnectionPoolSize(int connectionPoolSize) {
		MemcachedUtil.connectionPoolSize = connectionPoolSize;
	}

	public static void setConnectTimeout(long connectTimeout) {
		MemcachedUtil.connectTimeout = connectTimeout;
	}
	
}
