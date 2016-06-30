package com.project.bean.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class AjaxBase {

	private int status;
	private String message = "";

	public AjaxBase(int status, String message){
		this.status = status;
		this.message = message;
	}

	public AjaxBase(int status){
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
