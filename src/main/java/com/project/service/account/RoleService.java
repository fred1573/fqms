package com.project.service.account;

import com.project.dao.account.RoleDao;
import com.project.dao.account.UserDao;
import com.project.entity.account.Authority;
import com.project.entity.account.Role;
import com.project.entity.account.User;
import com.project.enumeration.Status;
import com.project.service.CurrentUserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yuneng.huang on 2016/5/16.
 */
@Service
@Transactional("mybatisTransactionManager")
public class RoleService {

    @Resource
    private RoleDao roleDao;
    @Resource
    private CurrentUserHolder currentUserHolder;
    @Resource
    private UserDao userDao;

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    /**
     * 获取所有角色
     * @return 不会返回null,当没有找到任何角色时，集合size为零
     */
    public List<Role> findAll() {
        return roleDao.selectAll();
    }

    /**
     * 根据记录id查找角色
     * @param id 角色记录id
     * @return null 没有该id的记录
     */
    public Role findById(Long id) {
        return roleDao.selectById(id);
    }

    /**
     * 保存一个角色同时设置角色的权限集合，权限集合不能为empty,至少选择一权限
     * @param role 角色对象
     */
    public void save(Role role) {
        checkRole(role);
        Role parentRole = roleDao.selectById(role.getParentId());
        role.setParent(parentRole);
        checkParentRole(role);
        checkRoleName(role.getSysRoleName());
        Assert.notEmpty(role.getAuthorityList(),"至少选择一权限");
        User user = currentUserHolder.getUser();
        role.setPath(parentRole.getChildPath());
        role.setLevel(parentRole.getChildLevel());
        role.setCreateUserCode(user.getSysUserCode());
        role.setCreateTime(new Date());
        roleDao.insert(role);
        List<Long> authorityIds = new ArrayList<>();
        for (Authority authority : role.getAuthorityList()) {
            authorityIds.add(authority.getId());
        }
        roleDao.insertRoleAuthorityRel(role.getId(),authorityIds);
    }


    private void checkRole(Role role) {
        Assert.notNull(role, "role不能为空");
        Assert.notNull(role.getParentId(),"parentId不能为空");
        Assert.hasText(role.getSysRoleName(),"sysRoleName不能为空");
    }

    private void checkParentRole(Role role) {
        Assert.notNull(role.getParentId(), "父部门不能为空");
        User user = currentUserHolder.getUser();
        Role parent = role.getParent();
        if (!user.hasRole(parent)) {
            throw new RuntimeException("你没有在部门"+parent.getSysRoleName()+"下添加子部门的权限");
        }
        if (parent.getAllParentId().contains(String.valueOf(role.getId()))) {
            throw new RuntimeException("不能将子部门修改为当前部门的父部门");
        }
    }

    private void checkRoleName(String roleName) {
        if (isExistRoleName(roleName)) {
            throw new RuntimeException("部门名已经存在");
        }
    }

    /**
     * 判断角色名是否已经存在
     * @param roleName 角色名
     * @return true已经存在该角色名
     */
    public boolean isExistRoleName(String roleName) {
        Role role = roleDao.selectByRoleName(roleName);
        return (role!=null);
    }

    /**
     * 更新角色
     * @param role
     */
    public void update(Role role) {
        checkRole(role);
        Role parentRole = roleDao.selectById(role.getParentId());
        role.setParent(parentRole);
        checkParentRole(role);
        Assert.notNull(role.getId(),"id不能为空");
        Role oldRole = roleDao.selectById(role.getId());
        if (oldRole == null) {
            throw new RuntimeException("部门不存在");
        }
        String subPath = "";
        int increaseLevel = parentRole.getChildLevel()-oldRole.getLevel();
        if (increaseLevel >= 0) {
            subPath = StringUtils.delete(parentRole.getChildPath(), oldRole.getPath());
        } else {
            subPath = StringUtils.delete(oldRole.getPath(),parentRole.getChildPath());
        }
        String sysRoleName = role.getSysRoleName();
        if (!oldRole.getSysRoleName().equals(sysRoleName)) {
            checkRoleName(sysRoleName);
        }
        oldRole.setParentId(role.getParentId());
        oldRole.setSysRoleName(role.getSysRoleName());
        oldRole.setRmk(role.getRmk());
        User user = currentUserHolder.getUser();
        oldRole.setUpdateUserCode(user.getSysUserCode());
        oldRole.setUpdateTime(new Date());
        oldRole.setLevel(parentRole.getChildLevel());
        oldRole.setPath(parentRole.getChildPath());
        roleDao.update(oldRole);
        roleDao.updateDescendantPathAndLevel(oldRole.getId(),subPath,increaseLevel);
    }

    /**
     * 更新角色下的权限，角色下的权限集合不能为empty,至少选择一权限
     * @param roleId 角色id
     * @param authorityIds 权限id集合
     */
    public void updateAuthority(Long roleId,List<Long> authorityIds) {
        Assert.notNull(roleId, "roleId不能为空");
        Assert.notEmpty(authorityIds,"至少选择一权限");
        Role role = new Role();
        role.setId(roleId);
        clearAuthority(role);
        roleDao.insertRoleAuthorityRel(roleId,authorityIds);
    }

    /**
     * 清空角色下的所有权限
     * @param role 角色对象
     */
    private void clearAuthority(Role role) {
        Assert.notNull(role.getId(),"id不能为空");
        roleDao.deleteRoleAuthorityRelByRoleId(role.getId());
    }

    /**
     * 禁用角色
     * @param role
     */
    public void disable(Role role) {
        Assert.notNull(role.getId(), "id不能为空");
        roleDao.updateStatus(role.getId(),Status.DISABLE);
        roleDao.updateStatusByParentId(role.getId(),Status.DISABLE);
        userDao.updateStatusByRoleId(role.getId(),Status.DISABLE);
    }

    /**
     * 删除一个角色，并清空角色下的权限
     * @param role 角色对象
     */
    public void delete(Role role) {
        Assert.notNull(role.getId(), "id不能为空");
        int count = roleDao.selectUserCountByRoleId(role.getId());
        if (count != 0) {
            throw new RuntimeException("不能删除已授权给用户的部门");
        }
        List<Role> childNodes = roleDao.selectByParentId(role.getId());
        if (!CollectionUtils.isEmpty(childNodes)) {
            throw new RuntimeException("该部门下还有子部门不能删除");
        }
        roleDao.delete(role.getId());
        clearAuthority(role);
    }

    /**
     * 根据父角色id获得所有子角色
     * @param parentId 父角色id
     * @return 不会返回null,当角色没有子角色时，集合size为零
     */
    public List<Role> findChildren(Long parentId) {
        return roleDao.selectByParentId(parentId);
    }

    /**
     * 启用角色
     * @param role
     */
    public void enabled(Role role) {
        Assert.notNull(role.getId(), "id不能为空");
        roleDao.updateStatus(role.getId(),Status.ENABLED);
        roleDao.updateStatusByParentId(role.getId(),Status.ENABLED);
    }

    /**
     * 根据父角色id获得所有后代
     * @param id 父角色id
     * @return 不会返回null,当角色没有后代时，集合size为零
     */
    public List<Role> findDescendant(Long id) {
        return roleDao.selectDescendantById(id);
    }
}
