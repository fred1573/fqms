package com.project.dao.wg;

import java.util.List;

import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.wg.WgOtaInfo;

/**
 * @author 
 * X
 */
@Component
public class WgOtaInfoDao extends HibernateDao<WgOtaInfo, Long> {
	
	public List<WgOtaInfo> getAll(){
		return super.getAll();
	}
	
}
