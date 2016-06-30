package com.project.dao.common;

import java.util.List;

import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.common.SysDictionary;

/**
 * @author 
 * mowei
 */
@Component
public class SysDictionaryDao extends HibernateDao<SysDictionary, Long> {
	
	public List<SysDictionary> findByPropertyOrderby(String string, String isuseFlagOpen, String orderby) {
		String hql = "from SysDictionary sd where sd."+string+"=? order by sd."+orderby;
		return this.find(hql, isuseFlagOpen);
	}
	
}
