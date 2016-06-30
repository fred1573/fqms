package com.project.dao.plug;

import java.util.List;

import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.plug.InnPlugFunc;

/**
 * @author 
 * X
 */
@Component
public class InnPlugFuncDao extends HibernateDao<InnPlugFunc, Long> {
	
	public List<InnPlugFunc> getAll(){
		return super.getAll();
	}
	
}
