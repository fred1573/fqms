package com.project.service.account;

import com.project.dao.account.HibernateUserDao;
import com.project.entity.account.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * 实现SpringSecurity的UserDetailsService接口,实现获取用户Detail信息的回调函数.
 *
 * @author
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	private static Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	private AccountService accountService;
	@Autowired
	private HibernateUserDao hibernateUserDao;

	/**
	 * 获取用户Details信息的回调函数.
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

		logger.info("-----用户: "+username+"准备登陆");

		User user = hibernateUserDao.findUserByUserCode(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户" + username + " 不存在");
		}
		return user;
	}


	
}
