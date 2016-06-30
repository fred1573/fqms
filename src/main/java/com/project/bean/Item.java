package com.project.bean;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Item implements Serializable
{
	private static final long serialVersionUID = 7806948604825072915L;
	
	private String name;
    private String value;
    private String status;
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getValue() {
    	return value.trim();
    }
    
    public void setValue(String value) {
    	this.value = value;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
