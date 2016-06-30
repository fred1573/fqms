package com.project.entity.inn;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;


/**
 * 客栈-管理员
 * 
 * @author 陈亚超
 * 
 */
@Entity
@Table(name = "tomato_inn_admin")
public class InnAdmin implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "seqGenerator", sequenceName = "seq_inn_admin_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenerator")
	private Integer id;

	/** 用户名称 */
	@Column(length = 32)
	private String name;
	
	/** 原管理员账号为手机号，现在允许中文、英文、数字，最长允许20个中文汉字，唯一登陆账户 */
	@Column(length = 20)
	@Length(min=11, max=14, message = "请输入正确的账号")
	@Pattern(regexp = "^(13|14|15|18)[0-9]{9}$", message = "请输入正确的手机号码")
	private String mobile;
	
	/** 管理员邮箱 */
	@Column(length = 64)
	//@NotBlank(message="Email必填")
	private String email;

	/** 管理员密码 */
	@Column(length = 64)
	private String password;
	
	/** 管理员QQ */
	@Column(length = 20)
	private String qq;

	/** 管理员密码Salt */
	@Column(length = 8)

	private String salt;

	/** 管理员上次登录IP */
	@Column(length = 32)
	private String lastLoginedIp;

	/** 管理员上次登录时间 */
	@Column(length = 32)
	private String lastLoginedAt;

	/** 管理员创建时间 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	/** 会员状态(1：待审核、2：已审核、3：已拒绝、4：已删除) */
	private Integer status;
	
	@Column(length = 200)
	private String rmk;
	
	/** 父ID */
	private Integer parentId;
	
	/** 是否被绑定 */
	private String hasBound;

    /**
     * 房态样式类型
     *  "1": 默认样式
     *  "2"：xy 样式
     *  "3": excel样式
     *
     */
	/** 该账户的手机token（ios） */
	private String token;

	/** 所属客栈 */
	@Valid
	@ManyToOne
	@JoinColumn(name = "inn_id")
	private Inn inn;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getLastLoginedIp() {
		return lastLoginedIp;
	}
	public void setLastLoginedIp(String lastLoginedIp) {
		this.lastLoginedIp = lastLoginedIp;
	}
	public String getLastLoginedAt() {
		return lastLoginedAt;
	}
	public void setLastLoginedAt(String lastLoginedAt) {
		this.lastLoginedAt = lastLoginedAt;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getRmk() {
		return rmk;
	}
	public void setRmk(String rmk) {
		this.rmk = rmk;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getHasBound() {
		return hasBound;
	}
	public void setHasBound(String hasBound) {
		this.hasBound = hasBound;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Inn getInn() {
		return inn;
	}
	public void setInn(Inn inn) {
		this.inn = inn;
	}
	
}
