package com.project.service;

import com.project.entity.account.User;

/**
 * @author yuneng.huang on 2016/5/19.
 */
public interface CurrentUserHolder {

    /**
     * 获取当前用户
     * @return null 没用当前用户，可能是用户尚未登录
     */
    User getUser();
}
