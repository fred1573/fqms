package com.project.bean.vo;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CityVo {
	
	@Id
    private Integer id;
	
	private String name;
	
	private String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
