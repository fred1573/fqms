/**
* @Title: InAreaPage.java
* @Package com.project.entity.area
* @Description: 
* @author Administrator
* @date 2014年4月4日 下午4:16:56
*/

/**
 * 
 */
package com.project.entity.area;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Administrator
 *
 */
@Entity
public class InAreaPage {

	private Integer id;
	private String name;
	private String contact;
	private int status;
	private Date regdate;
	
	@Id
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
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getRegdate() {
		return regdate;
	}
	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}
	@Override
	public String toString() {
		return "InAreaPage [id=" + id + ", name=" + name + ", contact="
				+ contact + ", status=" + status + ", regdate=" + regdate + "]";
	}
	
}
