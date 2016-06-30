package com.project.servlet.impl;
import java.util.HashMap;

import com.project.connect.Result;
import com.project.servlet.IEvent;

/**
 * test事件实现 
 * @author mowei
 */
public class TestEvent implements IEvent
{
	static final String command = "testCommand";

	public Result deal(HashMap var) {
			Result res = new Result();
			HashMap map = (HashMap)var;
			String test = (String) map.get("test");
			HashMap returnMap = new HashMap();
			returnMap.put("test", "test");
			res.setData(returnMap);
			return res;
     }

	public String getCommand() {
		return command;
	}

}
