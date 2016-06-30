package com.project.bean.proxysale;

public class ChannelStatus {
	
	
	public  static final  String  SELECT = "1";
	public  static final  String  EDIT = "2";
	public  static final  String  DISABLED = "3";
	
	private String name;
	
	private String status ;
	
	public ChannelStatus(String name,String status) {
		this.name = name;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
