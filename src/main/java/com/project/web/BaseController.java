package com.project.web;

import com.project.entity.account.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.project.core.orm.Page;


/**
 * 
 * @author mowei
 */
@Controller
public class BaseController {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 初始化分页参数
	 * @param page
	 * @param pageSize
	 */
	protected <T> void initPage(Page<T> page,int pageSize){
		page.setPageSize(pageSize);
		if (!page.isOrderBySetted()) {//设置默认排序方式
			page.setOrderBy("id");
			page.setOrder(Page.ASC);
		}
	}

	private Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	protected User getCurrentUser() {
		return (User) getAuthentication().getPrincipal();
	}
	
}