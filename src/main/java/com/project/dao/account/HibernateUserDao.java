package com.project.dao.account;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.account.User;
import org.springframework.stereotype.Repository;

/**
 * @author yuneng.huang on 2016/6/14.
 */
@Repository
public class HibernateUserDao extends HibernateDao<User, Long> {


    public User findUserByUserCode(String userCode) {
        return this.findUniqueWithSql("select * from tomato_sys_user where sys_user_code=? and status!='LOCKED'", userCode);
    }
}
