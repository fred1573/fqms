/**
* @Title: InnArea.java
* @Package com.project.entity.area
* @Description: 
* @author Administrator
* @date 2014年4月3日 上午10:10:20
*/

/**
 * 
 */
package com.project.entity.area;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.project.entity.IdEntity;
import com.project.entity.inn.Inn;
import com.project.entity.inn.InnRegion;

/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "tomato_inn_area_region")
public class InnArea extends IdEntity{

	private static final long serialVersionUID = 1L;
	
    private InnRegion region;
	
    private Inn inn;
	
    @Column(length = 36)
    private String remark1;
    
    @Column(length = 36)
    private String remark2;
    
    private Date createtime;

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	@ManyToOne(cascade={CascadeType.REFRESH})
    @JoinColumn(name = "region_id")
	public InnRegion getRegion() {
		return region;
	}

	public void setRegion(InnRegion region) {
		this.region = region;
	}

	/** 所属客栈 */
	@ManyToOne(cascade={CascadeType.REFRESH})
	@Valid
	@JoinColumn(name = "inn_id")
	public Inn getInn() {
		return inn;
	}

	public void setInn(Inn inn) {
		this.inn = inn;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
