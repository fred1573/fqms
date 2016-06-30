package com.project.web;

import com.project.entity.account.User;
import com.project.service.CurrentUserHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author yuneng.huang on 2016/5/19.
 */
@Component
public class SecurityCurrentUserHolder implements CurrentUserHolder{

    @Override
    public User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
