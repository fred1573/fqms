package com.project.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cookies帮助类
 * 
 * @author mowei
 * 
 */
public class CookiesUtil {

	private static Logger logger = LoggerFactory.getLogger(CookiesUtil.class);
	
	/**
	 * 设置cookie
	 * @param response
	 * @param name  cookie名字
	 * @param value cookie值
	 * @param maxAge cookie生命周期  以秒为单位
	 * @throws UnsupportedEncodingException 
	 */
	public static void addCookie(HttpServletResponse response,String name,String value,int maxAge) throws UnsupportedEncodingException{
	    Cookie cookie = new Cookie(name,URLEncoder.encode(value, "utf-8"));
	    cookie.setPath("/");
	    if(maxAge>0)  
	    	cookie.setMaxAge(maxAge);
	    response.addCookie(cookie);
	}
	
	/**
	 * 根据名字获取cookie的值
	 * @param request
	 * @param name cookie名字
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getCookieValueByName(HttpServletRequest request,String name) throws UnsupportedEncodingException{
		Cookie cookie = getCookieByName(request,name);
	    if(cookie!=null){
	        return URLDecoder.decode(cookie.getValue(),"utf-8");
	    }else{
	        return null;
	    }   
	}
	
	/**
	 * 根据名字获取cookie
	 * @param request
	 * @param name cookie名字
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static Cookie getCookieByName(HttpServletRequest request,String name) {
	    Map<String,Cookie> cookieMap = ReadCookieMap(request);
	    if(cookieMap.containsKey(name)){
	        Cookie cookie = (Cookie)cookieMap.get(name);
	        return cookie;
	    }else{
	        return null;
	    }   
	}
	 
	/**
	 * 将cookie封装到Map里面
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request) {  
	    Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
	    Cookie[] cookies = request.getCookies();
	    if(null!=cookies){
	        for(Cookie cookie : cookies){
	        	cookieMap.put(cookie.getName(), cookie);
	        }
	    }
	    return cookieMap;
	}
	
	/**
	 * 清空cookie 
	 * @param request
	 * @return
	 */
	public static void clearCookies(HttpServletRequest request,HttpServletResponse response){  
		try { 
			Cookie[] cookies = request.getCookies();
		    if(null!=cookies){
		        for(Cookie cookie : cookies){
		        	Cookie c = new Cookie(cookie.getName(), null); 
		        	c.setPath("/");
		        	c.setMaxAge(0);
		        	response.addCookie(c);  
		        }
		    }
		} catch (RuntimeException e) {  
			logger.error(e.getClass().getName()+":"+e.getMessage());
		}
	}
	
	/** 
     * 删除cookie中对应数值 
     * @param name 
     * @param request 
     * @param response 
	 * @throws UnsupportedEncodingException 
     */  
    public static void deleteCookieByName(String name, HttpServletRequest request, HttpServletResponse response) {  
        try {  
            Cookie[] cookies = request.getCookies();  
            if(null!=cookies){
            	for(Cookie cookie : cookies){
	                if (cookie.getName().equalsIgnoreCase(name)) {  
	                	Cookie c = new Cookie(cookie.getName(), null); 
	    	        	c.setPath("/");
	    	        	c.setMaxAge(0);
	    	        	response.addCookie(c); 
	                }
	            } 
            }
        } catch (RuntimeException e) {  
        	logger.error(e.getClass().getName()+":"+e.getMessage());
        }  
    }  
	
}