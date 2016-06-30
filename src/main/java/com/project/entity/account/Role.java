package com.project.entity.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.project.entity.IdEntity;
import com.project.enumeration.Status;
import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 角色.
 * 
 * 注释见{@link User}.
 * 
 * @author
 */
@Entity
@Table(name = "TOMATO_SYS_ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//解决懒加载和循环引用造成Jckson无法转换
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class Role extends IdEntity{

	private static final String DELIMITER = "/";

	private String sysRoleName;
	private Long parentId;
	private Status status=Status.ENABLED;
	private String rmk;
	private String createUserCode;		//创建人
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
	private Date createTime;			//创建时间
	private String updateUserCode;		//修改人
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
	private Date updateTime;			//修改时间
	private List<Authority> authorityList = Lists.newArrayList();

	private Role parent;
	private List<Role> children ;

	private String path;
	private int level;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	@Column(nullable = false, unique = true)
	public String getSysRoleName() {
		return sysRoleName;
	}

	@Column(nullable = false, unique = true)
	public void setSysRoleName(String sysRoleName) {
		this.sysRoleName = sysRoleName;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getRmk() {
		return rmk;
	}

	public void setRmk(String rmk) {
		this.rmk = rmk;
	}

	public String getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(String createUserCode) {
		this.createUserCode = createUserCode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUserCode() {
		return updateUserCode;
	}

	public void setUpdateUserCode(String updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	@ManyToMany
	@JoinTable(name = "TOMATO_SYS_ROLE_AUTHORITY", joinColumns = { @JoinColumn(name = "SYS_ROLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "SYS_AUTHORITY_ID") })
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("id")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Basic(fetch = FetchType.EAGER)
	public List<Authority> getAuthorityList() {
		return authorityList;
	}

	public void setAuthorityList(List<Authority> authorityList) {
		this.authorityList = authorityList;
	}
	
	@Transient
	@JsonIgnore
	public String getAuthNames() {
		return CollectionsUtil.extractToString(authorityList, "sysAuthorityName", ", ");
	}

	@Transient
	@JsonIgnore
	@SuppressWarnings("unchecked")
	public List<Long> getAuthIds() {
		return CollectionsUtil.extractToList(authorityList, "id");
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * 获得所有的父id
	 * @return
     */
	@Transient
	public List<String> getAllParentId() {
		LinkedList<String> parentIds = new LinkedList<>();
		if (StringUtils.hasText(this.path)) {
			String[] stringArray = StringUtils.tokenizeToStringArray(this.path, DELIMITER,true,true);
			Collections.addAll(parentIds, stringArray);
		}
		return Collections.unmodifiableList(parentIds);
	}

	@Transient
	public boolean isRoot() {
		return (this.parentId == null || this.path == null);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Transient
	public Role getParent() {
		return parent;
	}

	public void setParent(Role parent) {
		this.parent = parent;
	}

	@Transient
	public String getChildPath() {
		return this.path + this.id+ DELIMITER;
	}

	@Transient
	public int getChildLevel() {
		return this.level + 1;
	}

}
