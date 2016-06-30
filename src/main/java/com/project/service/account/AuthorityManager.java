package com.project.service.account;

import com.project.dao.account.AuthorityDao;
import com.project.dao.log.SystemLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 安全相关实体的管理类
 * 
 * @author mowei
 */
@Service
@Transactional("mybatisTransactionManager")
public class AuthorityManager {

	private static Logger logger = LoggerFactory.getLogger(AuthorityManager.class);

	@Autowired
	private AuthorityDao authorityDao;
	@Autowired
	private SystemLogDao systemLogDao;

	
}
