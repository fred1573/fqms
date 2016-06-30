package com.project.service.account;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.project.common.Constants;
import com.project.dao.account.HibernateUserDao;
import com.project.dao.account.RoleDao;
import com.project.dao.account.UserDao;
import com.project.entity.account.Role;
import com.project.entity.account.User;
import com.project.enumeration.Status;
import com.project.service.CurrentUserHolder;
import com.project.utils.encode.PassWordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 安全相关实体的管理类, 包括用户,角色,资源与授权类.
 *
 * @author
 */
@Service
@Transactional("mybatisTransactionManager")
public class AccountService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private HibernateUserDao hibernateUserDao;
    @Resource
    private RoleDao roleDao;
    @Resource
    private CurrentUserHolder currentUserHolder;

    /**
     * 根据id获取用户信息
     *
     * @param id 用户记录id
     * @return null 如果数据库中不存在该id的用户记录
     */
    public User getUser(Long id) {
        return userDao.selectById(id);
    }


    /**
     * 保存用户
     *
     * @param user 用户对象
     */
    public void save(User user) {
        Assert.notNull(user, "user不能为空");
        user.checkProperty();
        Assert.hasText(user.getPassword(), "password不能为空");
        if (user.getPassword().length() < Constants.DEFAULT_PWD_LENGTH) {
            throw new RuntimeException("密码长度至少6位");
        }
        User oldUser = userDao.selectBySysUserCode(user.getSysUserCode());
        if (oldUser != null) {
            throw new RuntimeException("用户名已经存在");
        }
        user.setSysUserPwd(PassWordUtil.getShaPwd(user.getSysUserPwd(), user.getSysUserCode()));
        user.setCreateTime(new Date());
        User currentUser = currentUserHolder.getUser();
        user.setCreateUserCode(currentUser.getSysUserCode());
        userDao.insert(user);
        userDao.insertRole(user.getId(), user.getRoleList());
    }

    /**
     * 更新用记
     *
     * @param user 用户对象
     */
    public void update(User user) {
        Assert.notNull(user, "user不能为空");
        user.checkProperty();
        if (StringUtils.hasText(user.getPassword())&&user.getPassword().length() < Constants.DEFAULT_PWD_LENGTH) {
            throw new RuntimeException("密码长度至少6位");
        }
        Assert.notNull(user.getId(), "id不能为空");
        User persistentUser = userDao.selectById(user.getId());
        if (persistentUser == null) {
            throw new RuntimeException("用户不存在");
        }
        String sysUserCode = user.getSysUserCode();
        if (!persistentUser.getSysUserCode().equals(sysUserCode)) {
            User user1 = userDao.selectBySysUserCode(user.getSysUserCode());
            if (user1 != null) {
                throw new RuntimeException("用户名已经存在");
            }
        }
        persistentUser.setSysUserName(user.getSysUserName());
        persistentUser.setSysUserCode(sysUserCode);
        persistentUser.setRoleList(user.getRoleList());
        persistentUser.setUpdateTime(new Date());
        if (StringUtils.hasText(user.getSysUserPwd())) {
            persistentUser.setSysUserPwd(PassWordUtil.getShaPwd(user.getSysUserPwd(), user.getSysUserCode()));
        }
        User currentUser = currentUserHolder.getUser();
        persistentUser.setUpdateUserCode(currentUser.getSysUserCode());
        userDao.update(persistentUser);
        userDao.deleteRoleById(user.getId());
        userDao.insertRole(persistentUser.getId(), persistentUser.getRoleList());
    }

    /**
     * 更新用户密码
     *
     * @param user 用户对象
     */
    public void updatePwd(User user) {
        Assert.notNull(user, "user不能为空");
        Assert.hasText(user.getPassword(), "password不能为空");
        Assert.notNull(user.getId(), "id不能为空");
        user.setSysUserPwd(PassWordUtil.getShaPwd(user.getSysUserPwd(), user.getSysUserCode()));
        userDao.updatePwd(user);
    }


    /**
     * 根据用户登录名更新用户最后登录时间
     *
     * @param userCode 用户登录名
     */
    public void updateLastLendedTime(String userCode) {
        userDao.updateLastLendedTime(userCode);
    }

    /**
     * 根据用户登录名查找用户
     *
     * @param userCode 登录名
     * @return null 当不存在该用户名时
     */
    public User findUserByUserCode(String userCode) {
        return hibernateUserDao.findUniqueBy("sysUserCode", userCode);
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param pageBounds 分页对象
     * @param userName   用户名，可选，用户名不为空时模糊匹配用户名和code
     * @param roleId     角色id,必填
     * @return empty, 没用满足条件的用户
     */
    public PageList<User> findByPage(PageBounds pageBounds, String userName, Long roleId) {
        Assert.notNull(roleId, "roleId不能为空");
        return (PageList<User>) userDao.selectByPage(pageBounds, userName, roleId);
    }

    /**
     * 逻辑删除用户
     *
     * @param user 用户对象
     */
    public void remove(User user) {
        Assert.notNull(user.getId(), "id不能为空");
        userDao.updateStatus(user.getId(), Status.LOCKED);
    }

    /**
     * 禁用用户
     *
     * @param user 用户对象
     */
    public void disable(User user) {
        Assert.notNull(user.getId(), "id不能为空");
        userDao.updateStatus(user.getId(), Status.DISABLE);
    }

    /**
     * 启用用户
     *
     * @param user 用户对象
     */
    public void enabled(User user) {
        Assert.notNull(user.getId(), "id不能为空");
        user = userDao.selectById(user.getId());
        userDao.updateStatus(user.getId(), Status.ENABLED);
        for (Role role : user.getRoleList()) {
            roleDao.updateStatus(role.getId(),Status.ENABLED);
        }
    }
}
