package com.project.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class CustomerLoginFailureHandler implements AuthenticationFailureHandler{
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException ae)
			throws IOException, ServletException {
		String contentType = "application/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setHeader("Cache-Control", "no-cache");
		String paramStr = "{\"loginFlag\":\"false\"}";
		response.getWriter().write(paramStr);
	    response.getWriter().flush();
	    response.getWriter().close();
	}

}
