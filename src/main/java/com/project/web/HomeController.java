package com.project.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


/**
 * 
 * @author mowei
 */
@Controller
public class HomeController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request) {
		Object attribute = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		if (attribute instanceof AuthenticationException) {
			AuthenticationException exception = (AuthenticationException) attribute;
			logger.error(exception.getMessage(),exception);
		}
		return "login";
	}
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String list(){
		return "home";
	}
	
}