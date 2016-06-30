package com.project.servlet;

import java.util.HashMap;

import com.project.connect.Result;

public interface IEvent
{
	public String getCommand();
	
	public Result deal(HashMap var);
}
