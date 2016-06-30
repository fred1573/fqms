package com.project.core.utils.springsecurity;

import java.util.Collection;

import com.project.entity.account.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.common.Constants;

public class AuthorityUtil {
	
	/**
	 * 获取角色
	 */
	public static String getUserRole() {
		String userRole = "";
		User u = SpringSecurityUtil.getCurrentUser();
		if(u!=null){
			for(GrantedAuthority as :u.getAuthorities()){
				userRole = as.getAuthority();
				break;
			}
		}else{
			userRole = Constants.PORTAL_USER_ROLE_ANONYMOUS;
		}
		return userRole;
	}
	
	/**
	 * 获取角色code
	 */
	public static String getUserRoleCode() {
		String userRole = "";
		User u = SpringSecurityUtil.getCurrentUser();
		if(u!=null){
			for(GrantedAuthority as :u.getAuthorities()){
				userRole = as.getAuthority();
				break;
			}
		}else{
			userRole = Constants.PORTAL_USER_ROLE_ANONYMOUS;
		}
		if(Constants.PORTAL_USER_ROLE_MEMBER.equals(userRole))
			return Constants.USER_TYPE_MEMBER;
		else if(Constants.PORTAL_USER_ROLE_USER.equals(userRole))
			return Constants.USER_TYPE_SYS;
		else
			return null;
	}
	
	/**
	 * 获取角色
	 */
	@SuppressWarnings("unchecked")
	public static String getUserRole(UserDetails userDetails) {
		Collection<GrantedAuthority> authSet = (Collection<GrantedAuthority>) userDetails.getAuthorities();
		String userRole = "";
		for(GrantedAuthority as :authSet){
			userRole = as.getAuthority();
			break;
		}
		return userRole;
	}
	
}
