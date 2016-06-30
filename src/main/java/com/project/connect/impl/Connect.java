package com.project.connect.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.common.Constants;
import com.project.connect.IConnect;
import com.project.connect.Message;
import com.project.connect.Result;

public class Connect implements IConnect {
	
	private Logger logger = LoggerFactory.getLogger(Connect.class);
	
	public Result interactive(Message message) throws IOException {
		Result result = new Result();
		HttpURLConnection connection = null;
		ObjectInputStream in = null;
		URL url = new URL(message.getLink());
		try {
			message.setAttribute(Constants.SYSTEM_SERVLET_COMMAND, message.getCommand());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(message.getParam());
			out.flush();
			out.close();

			InputStream isObj = connection.getInputStream();
			in = new ObjectInputStream(isObj);
			result = (Result) in.readObject();
		} catch (Exception e) {
			logger.error("连接失败："+e);
			result.setStatus("0");
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
		return result;
	}
	
}
