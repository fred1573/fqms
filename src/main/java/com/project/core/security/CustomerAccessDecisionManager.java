package com.project.core.security;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class CustomerAccessDecisionManager implements AccessDecisionManager {

	private static Logger log = LoggerFactory.getLogger(CustomerAccessDecisionManager.class);
	
	@Override
	public void decide(Authentication a, Object o, Collection<ConfigAttribute> c) throws AccessDeniedException,
			InsufficientAuthenticationException {
		if (c == null || c.size()==0) {
			return;
		}
		// 所请求的url拥有的权限(一个url对多个权限)
		Iterator<ConfigAttribute> iterator = c.iterator();
		while (iterator.hasNext()) {
			ConfigAttribute configAttribute = iterator.next();
			// 访问所请求url所需要的权限
			String needPermission = configAttribute.getAttribute();
//			log.info("---------所需权限为： " + needPermission);
			// 用户所拥有的权限authentication
			for (GrantedAuthority ga : a.getAuthorities()) {
				if (needPermission.equals(ga.getAuthority())) {
					return;
				}
			}
		}
		// 没有权限
		throw new AccessDeniedException("您没有权限访问！ ");
	}

	@Override
	public boolean supports(ConfigAttribute arg0) {
		return true;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}
