package com.project.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * <h3>Class name</h3>
 * <h4>Description</h4>
 * 
 * <h4>Special Notes</h4>
 * 
 * @author mowei
 * 
 */
public class ResourceBundleUtil {
	protected static ResourceBundle bundle = ResourceBundle.getBundle("config");

	public static String getString(String key) {
		try {
			return bundle.getString(key);
		} catch (Exception e) {
		}
		return key;
	}
	
	public static String getString(String key, String resource) {
		bundle = ResourceBundle.getBundle(resource);
		try {
			return bundle.getString(key);
		} catch (Exception e) {
		}
		return key;
	}

	public static String getString(String key, Object... params) {
		try {
			String value = bundle.getString(key);
			MessageFormat form = new MessageFormat(value);
			return form.format(value, params);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}

	/**
	 * 
	 * @param key 需要获得的key值
	 * @param resource 资源文件名称
	 * @return 返回value值
	 */
	public static String getString(String key, String resource, Object... params) {
		ResourceBundle bundles = ResourceBundle.getBundle(resource);
		try {
			String value = bundles.getString(key);
			MessageFormat form = new MessageFormat(value);
			return form.format(value, params);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println(ResourceBundleUtil.getString("test"));
	}
	
}
