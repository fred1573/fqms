package com.project.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.springframework.util.Assert;

import ch.qos.logback.core.db.dialect.H2Dialect;

import com.project.core.utils.reflection.ReflectionUtil;
import com.project.entity.IdEntity;

/**
 * 用于项目的Hibernate工具类.
 * 
 * @author
 */
public class HibernateUtils {

	/**
	 * 根据对象ID集合, 整理合并集合.
	 * 
	 * 页面发送变更后的子对象id列表时,在Hibernate中删除整个原来的子对象集合再根据页面id列表创建一个全新的集合这种看似最简单的做法是不行的.
	 * 因此采用如此的整合算法：在源集合中删除id不在目标集合中的对象,根据目标集合中的id创建对象并添加到源集合中.
	 * 因为新建对象只有ID被赋值, 因此本函数不适合于cascade-save-or-update自动持久化子对象的设置.
	 * 
	 * @param srcObjects 源集合,元素为对象.
	 * @param checkedIds  目标集合,元素为ID.
	 * @param clazz  集合中对象的类型,必须为IdEntity子类
	 */
	public static <T extends IdEntity> void mergeByCheckedIdsOfLong(final Collection<T> srcObjects,
			final Collection<Long> checkedIds, final Class<T> clazz) {

		//参数校验
		Assert.notNull(srcObjects, "scrObjects不能为空");
		Assert.notNull(clazz, "clazz不能为空");

		//目标集合为空, 删除源集合中所有对象后直接返回.
		if (checkedIds == null) {
			srcObjects.clear();
			return;
		}

		//遍历源对象集合,如果其id不在目标ID集合中的对象删除.
		//同时,在目标集合中删除已在源集合中的id,使得目标集合中剩下的id均为源集合中没有的id.
		Iterator<T> srcIterator = srcObjects.iterator();
		try {

			while (srcIterator.hasNext()) {
				T element = srcIterator.next();
				Long id = element.getId();

				if (!checkedIds.contains(id)) {
					srcIterator.remove();
				} else {
					checkedIds.remove(id);
				}
			}

			//ID集合目前剩余的id均不在源集合中,创建对象,为id属性赋值并添加到源集合中.
			for (Long id : checkedIds) {
				T element = clazz.newInstance();
				element.setId(id);
				srcObjects.add(element);
			}
		} catch (Exception e) {
			throw ReflectionUtil.convertReflectionExceptionToUnchecked(e);
		}
	}
	
	public static <T extends IdEntity> void mergeByCheckedIdsOfString(final Collection<T> srcObjects,
			final Collection<String> checkedIds, final Class<T> clazz) {

		//参数校验
		Assert.notNull(srcObjects, "scrObjects不能为空");
		Assert.notNull(clazz, "clazz不能为空");

		//目标集合为空, 删除源集合中所有对象后直接返回.
		if (checkedIds == null) {
			srcObjects.clear();
			return;
		}

		//遍历源对象集合,如果其id不在目标ID集合中的对象删除.
		//同时,在目标集合中删除已在源集合中的id,使得目标集合中剩下的id均为源集合中没有的id.
		Iterator<T> srcIterator = srcObjects.iterator();
		try {

			while (srcIterator.hasNext()) {
				T element = srcIterator.next();
				String id = element.getId().toString();

				if (!checkedIds.contains(id)) {
					srcIterator.remove();
				} else {
					checkedIds.remove(id);
				}
			}

			//ID集合目前剩余的id均不在源集合中,创建对象,为id属性赋值并添加到源集合中.
			for (String id : checkedIds) {
				T element = clazz.newInstance();
				element.setId(Long.parseLong(id));
				srcObjects.add(element);
			}
		} catch (Exception e) {
			throw ReflectionUtil.convertReflectionExceptionToUnchecked(e);
		}
	}
	
	/**
	 * Initialize the lazy property value.
	 * 
	 * eg.
	 * Hibernates.initLazyProperty(user.getGroups());
	 */
	public static void initLazyProperty(Object proxyedPropertyValue) {
		Hibernate.initialize(proxyedPropertyValue);
	}

	/**
	 * 从DataSoure中取出connection, 根据connection的metadata中的jdbcUrl判断Dialect类型.
	 * 仅支持Oracle, H2, MySql，如需更多数据库类型，请仿照此类自行编写。
	 */
	public static String getDialect(DataSource dataSource) {
		String jdbcUrl = getJdbcUrlFromDataSource(dataSource);

		// 根据jdbc url判断dialect
		if (StringUtils.contains(jdbcUrl, ":h2:")) {
			return H2Dialect.class.getName();
		} else if (StringUtils.contains(jdbcUrl, ":mysql:")) {
			return MySQL5InnoDBDialect.class.getName();
		} else if (StringUtils.contains(jdbcUrl, ":oracle:")) {
			return Oracle10gDialect.class.getName();
		} else {
			throw new IllegalArgumentException("Unknown Database of " + jdbcUrl);
		}
	}

	private static String getJdbcUrlFromDataSource(DataSource dataSource) {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			if (connection == null) {
				throw new IllegalStateException("Connection returned by DataSource [" + dataSource + "] was null");
			}
			return connection.getMetaData().getURL();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get database url", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
}
