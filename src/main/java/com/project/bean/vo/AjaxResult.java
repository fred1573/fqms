package com.project.bean.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class AjaxResult {

	private int status;
	private Object result;
	private String message;

	public AjaxResult(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public AjaxResult(int status,Object result){
		this.status = status;
		this.result = result;
	}

	public AjaxResult(int status, Object result, String message) {
		this.status = status;
		this.result = result;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
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
