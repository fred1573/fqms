package com.project.dao.account;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.project.core.orm.Page;
import com.project.core.orm.PropertyFilter;
import com.project.entity.account.Role;
import com.project.entity.account.User;
import com.project.enumeration.Status;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户对象的DAO类.
 *
 * @author yuneng.huang on 2016/5/16
 */
public interface UserDao {

	/**
	 * 根据id查询用户
	 * @param id 用户记录id
	 * @return null 如果数据库中不存在该id的用户记录
     */
	User selectById(Long id);

	/**
	 * 插入一条新的用户记录
	 * @param user 用户对象
     */
	void insert(User user);

	/**
	 * 更新一条新的用户记录
	 * @param user 用户对象
     */
	void update(User user);

	/**
	 * 根据用户id删除一条用户记录
	 * @param id 用户记录id
     */
	void deleteById(long id);

	/**
	 * 根据sysUserCode查询用户
	 * @param sysUserCode
     * @return null 如果数据库中不存在该sysUserCode的用户记录
     */
	User selectBySysUserCode(String sysUserCode);

	/**
	 * 根据条件分页查询用户列表
	 * @param pageBounds 分页对象
	 * @param userName 用户名，可选，用户名不为空时模糊匹配用户名和code
	 * @param roleId 角色id,必填
     * @return empty,没用满足条件的用户
     */
	List<User> selectByPage(@Param("pageBounds") PageBounds pageBounds, @Param("userName") String userName, @Param("roleId") Long roleId);


	void updatePwd(User user);

	/**
	 * 根据userCode更新用户最后登录时间
	 * @param userCode 用户code
     */
	void updateLastLendedTime(String userCode);

	void deleteRoleById(Long id);

	void insertRole(@Param("id") Long id,@Param("roleList") List<Role> roleList);

	void updateStatus(@Param("id") Long id, @Param("status") Status status);

	void updateStatusByRoleId(@Param("roleId") Long roleId, @Param("status")Status status);
}