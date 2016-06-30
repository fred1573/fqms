package com.project.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.common.Constants;
import com.project.connect.Result;


/*
 * servlet入口 
 * @author mowei
 */
public class Interactive extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectInputStream in = null;
		try {
			//接受参数
			InputStream isObj = request.getInputStream();
			in = new ObjectInputStream(isObj);
			Object obj = in.readObject();
			HashMap ms = (HashMap) obj;
			//调用事件类
			Result returnObj = HttpAccess.excuteEvent( ms.get(Constants.SYSTEM_SERVLET_COMMAND) + "", ms);

			//返回数据
			OutputStream ou = response.getOutputStream();
			ObjectOutputStream objOs = new ObjectOutputStream(ou);
			objOs.writeObject(returnObj);
			objOs.close();
			objOs.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
