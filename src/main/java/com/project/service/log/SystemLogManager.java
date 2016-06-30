package com.project.service.log;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.core.orm.Page;
import com.project.core.orm.PropertyFilter;
import com.project.dao.log.SystemLogDao;
import com.project.entity.log.SystemLog;

/**
 * 
 * @author
 */
//Spring Bean的标识.
@Component
//默认将类中的所有函数纳入事务管理.
@Transactional
public class SystemLogManager {

	private static Logger logger = LoggerFactory.getLogger(SystemLogManager.class);

	@Autowired
	private SystemLogDao systemLogDao;

	/**
	 * 通过id获取systemLog对象
	 */
	@Transactional(readOnly = true)
	public SystemLog getSystemLog(Long id) {
		return systemLogDao.get(id);
	}

	/**
	 * 保存systemLog
	 */
	public void saveSystemLog(SystemLog entity) {
		systemLogDao.save(entity);
	}

	/**
	 * 删除systemLog
	 */
	public void deleteSystemLog(Long id) {
		systemLogDao.delete(id);
	}

	/**
	 * 使用属性过滤条件查询systemLog列表
	 */
	@Transactional(readOnly = true)
	public Page<SystemLog> searchSystemLog(final Page<SystemLog> page, final List<PropertyFilter> filters) {
		return systemLogDao.findPage(page, filters);
	}

}
