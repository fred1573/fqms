package com.project.core.security;

import com.project.common.Constants;
import com.project.entity.log.SystemLog;
import com.project.service.account.AccountService;
import com.project.service.log.SystemLogManager;
import com.project.utils.CookiesUtil;
import com.tomato.framework.log.util.UserInfoContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class CustomerLoginSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {

	private static Logger logger = LoggerFactory.getLogger(CustomerLoginSuccessHandler.class);
	
	private RequestCache requestCache;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private SystemLogManager systemLogManager;
	
	public CustomerLoginSuccessHandler(){  
		this.requestCache = new HttpSessionRequestCache();  
	} 
	
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		//将登录用户名放入cookie
		CookiesUtil.addCookie(response, "userName", authentication.getName(), 0);
		
		//记录用户登录时间
		accountService.updateLastLendedTime(authentication.getName());
		//插入系统日志记录
		SystemLog s = new SystemLog("用户登录", new Date(), authentication.getName(), Constants.USER_TYPE_MEMBER, Constants.LOG_TYPE_LOGIN);
		systemLogManager.saveSystemLog(s);
		logger.info("------保存登陆相关信息成功！");

		//设置当前登陆用户
		UserInfoContext.setUserInfo(authentication.getName());

		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest == null)
			super.onAuthenticationSuccess(request, response, authentication);
		else if (isAlwaysUseDefaultTargetUrl()
				|| (StringUtils.hasText(request.getParameter(getTargetUrlParameter())))) {
			requestCache.removeRequest(request, response);
			super.onAuthenticationSuccess(request, response, authentication);
		} else {
			clearAuthenticationAttributes(request);
			String targetUrl = savedRequest.getRedirectUrl();
			getRedirectStrategy().sendRedirect(request, response, targetUrl);
		}
	}

}
