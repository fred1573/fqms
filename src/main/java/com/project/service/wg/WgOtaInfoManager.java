package com.project.service.wg;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.project.dao.wg.WgOtaInfoDao;
import com.project.entity.wg.WgOtaInfo;

/**
 * 
 * @author
 */
//Spring Bean的标识.
@Component
//默认将类中的所有函数纳入事务管理.
@Transactional
public class WgOtaInfoManager {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(WgOtaInfoManager.class);

	@Autowired
	private WgOtaInfoDao wgOtaInfoDao;
	
	
	public List<WgOtaInfo> getAll(){
		return wgOtaInfoDao.getAll();
	}
	
	public List<Integer> getOtaIds(){
		List<Integer> otaIds = Lists.newArrayList();
		List<WgOtaInfo> infos = getAll();
		for (WgOtaInfo i : infos) {
			otaIds.add(i.getId());
		}
		return otaIds;
	}


}
