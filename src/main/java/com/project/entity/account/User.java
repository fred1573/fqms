package com.project.entity.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.project.common.Constants;
import com.project.entity.IdEntity;
import com.project.entity.inn.InnRegion;
import com.project.enumeration.Status;
import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户.
 * <p>
 * 使用JPA annotation定义ORM关系.
 * 使用Hibernate annotation定义JPA 1.0未覆盖的部分.
 *
 * @author mowei
 */
//解决懒加载和循环引用造成Jckson无法转换
@Entity
//表名与类名不相同时重新定义表名.
@Table(name = "TOMATO_SYS_USER")
//默认的缓存策略.
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//解决懒加载和循环引用造成Jckson无法转换
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class User extends IdEntity implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String sysUserCode;
    @JsonIgnore
    private String sysUserPwd;//为简化演示使用明文保存的密码
    private String sysUserName;
    private String sex;//性别:0、女 1、男
    private String phone;
    private String mobile;
    private String email;
    private Status status = Status.ENABLED;//是否启用。DISABLE(禁用)，ENABLED(启用)
    private String createUserCode;        //创建人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date createTime;            //创建时间
    private String updateUserCode;        //修改人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date updateTime;            //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date lastLendedTime;
    private String rmk;
    @JsonIgnore
    private String token;
    @JsonIgnore
    private List<Role> roleList = Lists.newArrayList();//有序的关联对象集合
    private List<InnRegion> regionList = Lists.newArrayList();//有序的关联对象集合

    //字段非空且唯一, 用于提醒Entity使用者及生成DDL.
    public String getSysUserCode() {
        return sysUserCode;
    }

    @Column(nullable = false, unique = true)
    public void setSysUserCode(String sysUserCode) {
        this.sysUserCode = sysUserCode;
    }

    public String getSysUserPwd() {
        return sysUserPwd;
    }

    public void setSysUserPwd(String sysUserPwd) {
        this.sysUserPwd = sysUserPwd;
    }

    @NotBlank(message = "用户姓名必须填写")
    public String getSysUserName() {
        return sysUserName;
    }

    public void setSysUserName(String sysUserName) {
        this.sysUserName = sysUserName;
    }

    //	@NotBlank(message="用户性别必选")
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    //	@NotBlank(message="手机号码必须填写")
//	@Pattern(regexp="^(13|14|15|18)[0-9]{9}$",message="请输入正确的手机号码")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    //	@NotBlank(message="用户邮箱必须填写")
//	@Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Enumerated(EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(String updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getLastLendedTime() {
        return lastLendedTime;
    }

    public void setLastLendedTime(Date lastLendedTime) {
        this.lastLendedTime = lastLendedTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    //多对多定义
    @ManyToMany
    //中间表定义,表名采用默认命名规则
    @JoinTable(name = "TOMATO_SYS_USER_ROLE", joinColumns = {@JoinColumn(name = "SYS_USER_ID")}, inverseJoinColumns = {@JoinColumn(name = "SYS_ROLE_ID")})
    //Fecth策略定义
    @Fetch(FetchMode.SUBSELECT)
    //集合按id排序.
    @OrderBy("id")
    //集合中对象id的缓存.
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Basic(fetch = FetchType.EAGER)
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    /**
     * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
     */
    //非持久化属性.
    @Transient
    @JsonIgnore
    public String getRoleNames() {
        return CollectionsUtil.extractToString(roleList, "sysRoleName", ", ");
    }

    /**
     * 用户拥有的角色id字符串, 多个角色id用','分隔.
     */
    //非持久化属性.
    @Transient
    @JsonIgnore
    public String getRoleIds() {
        return CollectionsUtil.extractToString(roleList, "id", ",");
    }

    //多对多定义
    @ManyToMany
    //中间表定义,表名采用默认命名规则
    @JoinTable(name = "TOMATO_SYS_USER_REGION", joinColumns = {@JoinColumn(name = "SYS_USER_ID")}, inverseJoinColumns = {@JoinColumn(name = "INN_REGION_ID")})
    //Fecth策略定义
    @Fetch(FetchMode.SUBSELECT)
    //集合按id排序.
    @OrderBy("id")
    //集合中对象id的缓存.
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Basic(fetch = FetchType.EAGER)
    public List<InnRegion> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<InnRegion> regionList) {
        this.regionList = regionList;
    }

    @Transient
    @JsonIgnore
    public String getRegionNames() {
        return CollectionsUtil.extractToString(regionList, "name", ", ");
    }

    /**
     * 用户拥有的角色id字符串, 多个角色id用','分隔.
     */
    @Transient
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public List<Long> getRegionIds() {
        return CollectionsUtil.extractToList(regionList, "id");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Transient
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authSet = Sets.newHashSet();
        for (Role role : this.getRoleList()) {
            if (Status.ENABLED.equals(role.getStatus())) {
                for (Authority authority : role.getAuthorityList()) {
                    if (Status.ENABLED.equals(authority.getStatus()))
                        authSet.add(new SimpleGrantedAuthority(authority.getPrefixedName()));
                }
            }
        }
        authSet.add(new SimpleGrantedAuthority("ROLE_ROOT"));
        return authSet;
    }

    @Override
    @Transient
    @JsonIgnore
    public String getPassword() {
        return this.sysUserPwd;
    }

    @Override
    @Transient
    @JsonIgnore
    public String getUsername() {
        return this.sysUserCode;
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return (!Status.LOCKED.equals(this.status));
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEnabled() {
        return (Status.ENABLED.equals(this.status));
    }

    /**
     * 获得用户角色的根角色
     *
     * @return null 用户没有任何角色
     */
    @Transient
    @JsonIgnore
    public Role getRootRole() {
        Role root = null;
        if (!CollectionUtils.isEmpty(this.roleList)) {
            root = roleList.get(0);
            for (int i = 1; i < roleList.size(); i++) {
                Role role = roleList.get(i);
                if (role.getLevel() < root.getLevel()) {
                    root = role;
                }
            }
        }
        if (root != null) {
            root.setParentId(null);
        }
        return root;
    }

    /**
     * 查看用户是否有指定角色的权限
     *
     * @param role 检查的角色
     * @return true 用户有指定角色的权限
     */
    public boolean hasRole(Role role) {
        if (role == null || role.getId() == null) {
            return false;
        }
        Role rootRole = getRootRole();
        if (rootRole.isRoot()) {
            return true;
        }
        return role.getAllParentId().contains(String.valueOf(rootRole.getId()));
    }


    /**
     * 用户基本属性检查
     */
    public void checkProperty() {
        Assert.hasText(getSysUserName(), "sysUserName不能为空");
        Assert.hasText(getSysUserCode(), "sysUserCode不能为空");
        Assert.notEmpty(getRoleList(), "roleList不能为空");
    }
}