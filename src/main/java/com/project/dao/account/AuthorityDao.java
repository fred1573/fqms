package com.project.dao.account;

import com.project.entity.account.Authority;

import java.util.List;

/**
 * 授权对象的泛型DAO.
 * 
 * @author yuneng.huang on 2016/5/16
 */
public interface AuthorityDao {

	List<Authority> selectAllCreateTimeAsc();

}
