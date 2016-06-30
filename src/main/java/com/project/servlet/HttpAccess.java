package com.project.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.project.connect.Result;

/*
 * http事件入口 
 * @author mowei
 */
public class HttpAccess {
	private static HashMap<String, IEvent> data = new HashMap<String, IEvent>();
	private static List<IEvent> dataClass = new ArrayList<IEvent>();

	//初始化事件对象
	static {
		eventClass();
		for (int i = 0; i < dataClass.size(); i++) {
			IEvent event = dataClass.get(i);
			data.put(event.getCommand(), event);
		}
	}

	/**
	 * 事件执行类
	 * 
	 * @param command
	 * @param obj
	 * @throws Exception 
	 */
	public static Result excuteEvent(String command, HashMap obj) throws Exception {
		IEvent se = data.get(command);
		if(se==null)
			throw new Exception("Get aaaaEvent Object Fail!");
		return se.deal(obj);
	}

	/**
	 *  声明事件对象
	 */
	private static void eventClass() {
		//dataClass.add(new UpdateCacheEvent());
	}

}
