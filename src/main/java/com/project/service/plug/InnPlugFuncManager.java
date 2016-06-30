package com.project.service.plug;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.dao.plug.InnPlugFuncDao;
import com.project.entity.plug.InnPlugFunc;

/**
 * 
 * @author
 */
//Spring Bean的标识.
@Component
//默认将类中的所有函数纳入事务管理.
@Transactional
public class InnPlugFuncManager {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(InnPlugFuncManager.class);

	@Autowired
	private InnPlugFuncDao innPlugFuncDao;
	
	@Cacheable(value="innPlugFuncCache", key="_inn_plug_func_")
	public List<InnPlugFunc> getAll(){
		return innPlugFuncDao.getAll();
	}


}
