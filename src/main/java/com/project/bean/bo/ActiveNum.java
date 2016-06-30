package com.project.bean.bo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ActiveNum implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	private int loginNum;
    private int bookNum;
    private int checkNum;
    
	public int getLoginNum() {
		return loginNum;
	}

	public void setLoginNum(int loginNum) {
		this.loginNum = loginNum;
	}

	public int getBookNum() {
		return bookNum;
	}

	public void setBookNum(int bookNum) {
		this.bookNum = bookNum;
	}

	public int getCheckNum() {
		return checkNum;
	}

	public void setCheckNum(int checkNum) {
		this.checkNum = checkNum;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
