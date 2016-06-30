package com.project.service.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.core.orm.Page;
import com.project.core.orm.PropertyFilter;
import com.project.dao.common.SysDictionaryDao;
import com.project.dao.log.SystemLogDao;
import com.project.entity.common.SysDictionary;

/**
 * 
 * @author
 * mowei
 */
//Spring Bean的标识.
@Component
//默认将类中的所有函数纳入事务管理.
@Transactional
public class SysDictionaryManager {

	private static Logger logger = LoggerFactory.getLogger(SysDictionaryManager.class);

	@Autowired
	private SysDictionaryDao sysDictionaryDao;
	@Autowired
	private SystemLogDao systemLogDao;

	/**
	 * 获取所有系统字典数据
	 * @return
	 */
	public List<SysDictionary> getAllSysDictionary(){
		return sysDictionaryDao.getAll("conseq", true);
	}
	
	/**
	 * 通过id获取sysDictionary对象
	 */
	@Transactional(readOnly = true)
	public SysDictionary getSysDictionary(Long id) {
		return sysDictionaryDao.get(id);
	}

	/**
	 * 保存sysDictionary
	 */
	public void saveSysDictionary(SysDictionary entity) {
		sysDictionaryDao.save(entity);
	}

	/**
	 * 删除sysDictionary
	 */
	public void deleteSysDictionary(Long id) {
		sysDictionaryDao.delete(id);
	}
	
	/**
	 * 使用属性过滤条件查询sysDictionary列表
	 */
	@Transactional(readOnly = true)
	public Page<SysDictionary> searchSysDictionary(final Page<SysDictionary> page, final List<PropertyFilter> filters) {
		return sysDictionaryDao.findPage(page, filters);
	}

	/**
	 * 获取有效的字典列表
	 * @param string
	 * @param isuseFlagOpen
	 * @return
	 */
	public List<SysDictionary> findByProperty(String string, String isuseFlagOpen) {
		return sysDictionaryDao.findBy(string,isuseFlagOpen);
	}
	
	public List<SysDictionary> findByPropertyOrderby(String string, String isuseFlagOpen, String orderby) {
		return this.sysDictionaryDao.findByPropertyOrderby(string, isuseFlagOpen, orderby);
	}
	
}
