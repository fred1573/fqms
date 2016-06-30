package com.project.connect;

import java.io.Serializable;
import java.util.HashMap;

import com.project.utils.ResourceBundleUtil;

public class Message implements Serializable {
	private static final long serialVersionUID = 5699005874415961446L;
	
	// URL连接地址
	private String link = ResourceBundleUtil.getString("link");
	// 调用命令
	private String command;
	// 参数
	private HashMap param = new HashMap();

	public Message() {
	}
	
	public Message(String command) {
		this.command = command;
	}
	
	public Message(String command, String link) {
		this.command = command;
		this.link  = ResourceBundleUtil.getString(link);
	}

	public HashMap getParam() {
		return param;
	}

	public void setAttribute(String key, Object value) {
		param.put(key, value);
	}

	public String getCommand() {
		return command;
	}

	public String getLink() {
		return link;
	}
	
}
