package com.project.dao.account;

import com.project.entity.account.Role;
import com.project.enumeration.Status;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色对象的泛型DAO.
 * 
 * @author yuneng.huang on 2016/5/16
 */
public interface RoleDao  {

	/**
	 * 查询所有角色
	 * @return 不会返回null,当没有找到任何角色时，集合size为零
	 */
	List<Role> selectAll();

	/**
	 * 根据记录id查询角色
	 * @param id 角色记录id
	 * @return null 没有该id的记录
     */
	Role selectById(Long id);

	/**
	 * 插入一条新的角色记录
	 * @param role 角色对象
     */
	void insert(Role role);

	/**
	 * 根据角色名查询角色
	 * @param roleName 角色名
	 * @return null 没有该角色名的记录
     */
	Role selectByRoleName(String roleName);


	/**
	 * 插入角色和权限关系
	 * @param roleId 角色id
	 * @param authorityIds 权限id集合
     */
	void insertRoleAuthorityRel(@Param("roleId") Long roleId,@Param("authorityIds") List<Long> authorityIds);

	void deleteRoleAuthorityRelByRoleId(Long id);

	void update(Role role);

	void delete(Long id);

	List<Role> selectByParentId(Long parentId);

	int selectUserCountByRoleId(Long id);

	void updateStatus(@Param("id") Long id, @Param("status")Status status);

	void updateStatusByParentId(@Param("parentId") Long parentId, @Param("status")Status status);

	/**
	 * 根据父角色id获得所有后代
	 * @param id 父角色id
	 * @return 不会返回null,当角色没有后代时，集合size为零
	 */
	List<Role> selectDescendantById(@Param("id")Long id);

	void updateDescendantPathAndLevel(@Param("parentId")Long parentId,@Param("subPath") String subPath, @Param("increaseLevel") int increaseLevel);
}
