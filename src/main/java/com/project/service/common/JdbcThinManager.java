package com.project.service.common;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.dao.common.JdbcThinDao;

@Component
@Transactional
public class JdbcThinManager {
	
	private static Logger logger = LoggerFactory.getLogger(JdbcThinManager.class);

	@Autowired
	private JdbcThinDao jdbcThinDao;

	public Boolean WhetherConnectSuccess(String theUser,String thePw){
		return jdbcThinDao.WhetherConnectSuccess(theUser, thePw);
	}
	
	public List<Map<String, Object>> findUserBySql(Object... values){
		String sql = "select * from TOMATO_SYS_USER where id = ? ";
		return jdbcThinDao.executeQuery(sql, values);
	}
	
}
