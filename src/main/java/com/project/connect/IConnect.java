package com.project.connect;

/**
 * 系统内部servlet访问接口
 * @author mowei
 *
 */
public interface IConnect
{
	public Result interactive(Message message) throws Exception;
}
