package com.project.connect;

import java.io.Serializable;

/**
 * 返回结果对象
 */
public class Result implements Serializable {

	private static final long serialVersionUID = -7187376961503956606L;
	
	private String status; //返回标示（0：失败，1：成功）
	private Object data; //返回对象

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}