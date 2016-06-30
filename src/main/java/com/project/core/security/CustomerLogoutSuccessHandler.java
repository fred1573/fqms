package com.project.core.security;

import com.project.utils.CookiesUtil;
import com.tomato.framework.log.util.UserInfoContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomerLogoutSuccessHandler implements LogoutSuccessHandler {

	private String url = "";
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) 
			throws IOException, ServletException {
		//清空username的cookie
		CookiesUtil.deleteCookieByName("userName",request,response);
		//清空currentUser
		UserInfoContext.release();
		response.sendRedirect(request.getContextPath()+"/login.action");
    }
    
}
