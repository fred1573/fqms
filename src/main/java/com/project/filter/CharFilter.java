package com.project.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.common.Constants;

public class CharFilter implements Filter {
	
	private static Logger logger = LoggerFactory.getLogger(CharFilter.class);

	public FilterConfig config;
	
	public void setFilterConfig(FilterConfig config) {
		this.config = config;
	}
	public FilterConfig getFilterConfig() {
		return config;
	}

	
	@SuppressWarnings("rawtypes")
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		try{
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			
			//session失效并且曾经登陆过系统:以下适用于不需要登录就可以访问页面
			/*if(req.getSession().isNew() && CookiesUtil.getCookieByName(req, "userName")!=null){
				//清空username的cookie
				CookiesUtil.deleteCookieByName("userName",req,res);
				//根据错误类型以及页面提交方式置入返回参数或者页面
				toJumpByType(req,res,"sessionTimeout");
				return;
			}*/
					
			boolean filter = true;		// 是否过滤；
			String excludeURL;			// 需要过滤的url地址
			excludeURL = config.getInitParameter("excludeURL");
			if (excludeURL == null || "".equals(excludeURL)) {
				filter = true;
			} else {
				String url = req.getRequestURI();		// url地址
				String[] excludeURLA = excludeURL.split(",");
				for (int i = 0; i < excludeURLA.length; i++) {
					if (url.indexOf(excludeURLA[i]) > 0) {	// 如果含有不需过滤的url地址则不过滤。
						filter = true; 
					}
				}
			}
			//得到应用服务器类型
			String appServer = config.getInitParameter("appServer");
			//防止篡改form的随机数的session
			String session_f_r = (String)req.getSession().getAttribute(Constants.SESSION_FORM_RAMDOM);
			//form中的随机参数
			String f_r_p_request = req.getParameter(Constants.FORM_RAMDOM_PARAMETER);
			
			if(filter) {		//需要过滤
				if("tomcat".equals(appServer)) {		//应用服务器为tomcat时则：
					if(checkTomcat(req,res)) {
						//根据错误类型以及页面提交方式置入返回参数或者页面
						toJumpByType(req,res,"illegalCharacter");
						return;
					} else {
						//通过字符过滤后验证页面传递的随机码是否在session中存在
						if(StringUtils.isNotBlank(f_r_p_request) && StringUtils.isNotBlank(session_f_r)){
							if(session_f_r.indexOf(f_r_p_request)!=-1){
								chain.doFilter(request, response);
							}else{
								toJumpByType(req,res,"illegalForm");
								return;
							}
						}else{
							chain.doFilter(request, response);
						}
					}
				} else {		//应用服务器为weblogic时则：
					Map m = req.getParameterMap();
					if(req instanceof ParameterRequestWrapper) {
						m = ((ParameterRequestWrapper)req).getSuperRequest().getParameterMap();
						req = ((ParameterRequestWrapper)req).getSuperRequest();
					}
					ParameterRequestWrapper wrapRequest=new ParameterRequestWrapper(req, m);
					if(checkWeblogic(wrapRequest, res)) {
						toJumpByType(req,res,"illegalCharacter");
						return;
					} else {
						if(StringUtils.isNotBlank(f_r_p_request) && StringUtils.isNotBlank(session_f_r)){
							if(session_f_r.indexOf(f_r_p_request)!=-1){
								chain.doFilter(request, response);
							}else{
								toJumpByType(req,res,"illegalForm");
								return;
							}
						}else{
							chain.doFilter(request, response);
						}
					}
				}
			} else {		//不需要过滤
				chain.doFilter(request, response);
			}
			//将随机码置入页面以及session中
			String f_r_p_response = String.valueOf(new Date().getTime());
			req.getSession().setAttribute(Constants.FORM_RAMDOM_PARAMETER, f_r_p_response);
			req.getSession().setAttribute(Constants.SESSION_FORM_RAMDOM, session_f_r==null?f_r_p_response:new StringBuffer(session_f_r).append(",").append(f_r_p_response).toString());
		}catch(Exception e){
			logger.error(e.getMessage());
			chain.doFilter(request, response);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public boolean checkWeblogic(HttpServletRequest req, HttpServletResponse response) {
		Map map = req.getParameterMap();
		Set set = map.entrySet();
		//request中的参数设置
		boolean bl = false;
		if (map != null) {
			for (Iterator it = set.iterator(); it.hasNext();) {
				Map.Entry entry = (Entry) it.next();
				if (entry.getValue() instanceof String[]) {
					String[] values = (String[]) entry.getValue();
					for (int i = 0; i < values.length; i++) {
						//是否包括sql关键字
						if(sqlValidate(values[i])) {
							return true;
						}
						//是否包含有特殊字符
						if (scriptValidate(values[i])) {
							return true;
						}
					}
				}
			}
		}
		
		return bl;
	}

	@SuppressWarnings("rawtypes")
	public boolean checkTomcat(HttpServletRequest req, HttpServletResponse response) {
		Map map = req.getParameterMap();
		//运用反射机制，让其可修改。
		/*try {
			Method method=map.getClass().getMethod("setLocked",new Class[]{boolean.class});
			method.invoke(map,new Object[]{new Boolean(false)});
		} catch(Exception e) {
			e.printStackTrace();
		}*/
		
		Set set = map.entrySet();

		boolean bl = false;
		if (map != null) {
			for (Iterator it = set.iterator(); it.hasNext();) {
				Map.Entry entry = (Entry) it.next();
				if (entry.getValue() instanceof String[]) {
					String[] values = (String[]) entry.getValue();
					for (int i = 0; i < values.length; i++) {
						//是否包括sql关键字
						if(sqlValidate(values[i])) {
							return true;
						}
						//是否包含有特殊字符
						if (scriptValidate(values[i])) {
							return true;
						}
					}
				}
			}
		}
		return bl;
	}

	/**
	 * 过滤javascript关键字
	 * @param str
	 * @return
	 */
	public boolean scriptValidate(String str) {
		//统一转为小写
        str = str.toLowerCase();
        //过滤掉的javascript关键字，可以手动添加
        String[] scriptKeyword = {"'","&",";","$","%","#","\"","<",">","(",")","+","cr","lf","\\","script","javascript","alert"};
        for (int i = 0; i < scriptKeyword.length; i++) {
            if (str.indexOf(scriptKeyword[i]+" ") !=-1) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * 过滤SQL关键字
	 * @param str
	 * @return
	 */
	public boolean sqlValidate(String str) {
		//统一转为小写
        str = str.toLowerCase();
        //过滤掉的sql关键字，可以手动添加
        String[] sqlKeyword = {"and","exec","execute","insert","create","drop","table","from","grant","use","group_concat","column_name",
				"information_schema.columns","table_schema","union","where","select","delete","update","order by","count","*","chr","mid",
				"master","having","truncate","char","declare","or","like"};
        for (int i = 0; i < sqlKeyword.length; i++) {
            if (str.indexOf(sqlKeyword[i]+" ") !=-1) {
                return true;
            }
        }
        return false;
    }

	public void destroy() {
		this.config = null;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = filterConfig;
	}
	
	public String toParamenterString(Object obj) {
		if (obj == null)
			return "NULL";
		if (obj instanceof String[]) {
			StringBuffer sb = new StringBuffer();
			String[] values = (String[]) obj;
			for (int i = 0; i < values.length; i++) {
				sb.append(values[i] + ",");
			}
			return sb.toString();
		}
		if (obj instanceof String) {
			return obj.toString();
		}
		return obj.toString();
	}

	@SuppressWarnings("rawtypes")
	public void printHttpHeader(HttpServletRequest request) {
		Enumeration e = request.getHeaderNames();
		if (e != null) {
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				String value = request.getHeader(name);
				System.out.println(name + "=" + value);
			}
		}
	}
	
	/**
	 * 根据错误类型以及页面提交方式置入返回参数或者页面
	 * @param req
	 * @param res
	 * @param type
	 * @throws IOException
	 */
	private void toJumpByType(HttpServletRequest req, HttpServletResponse res, String type) throws IOException{
		String webapp = req.getContextPath();
		//ajax提交
		if(req.getHeader("x-requested-with")!=null   
                && req.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){
			if(type.equals("illegalCharacter"))
				writeObjByType("提交的内容中包含非法字符",req,res);
			else if(type.equals("illegalForm"))
				writeObjByType("非法表单",req,res);
			else if(type.equals("sessionTimeout"))
				writeObjByType("用户失效",req,res);
		}else{
			if(type.equals("illegalCharacter"))
				res.sendRedirect(webapp + "/common/illegalCharacterSet.jsp");
			else if(type.equals("illegalForm"))
				res.sendRedirect(webapp + "/common/illegalForm.jsp");
			else if(type.equals("sessionTimeout"))
				res.sendRedirect(webapp + "/login.action?error=sessionTimeout");
		}
	}
	
	/**
	 * 返回ajax调用结果
	 * @return text
	 */
	private void writeObjByType(String returnStr, HttpServletRequest req, HttpServletResponse response) throws IOException {
		String returnType = req.getParameter("_returnType_");
		String contentType = "";
		if(Constants.DATA_TYPE_XML.equalsIgnoreCase(returnType)){
			contentType = "text/xml;charset=UTF-8";		//xml格式字符串
		}else if(Constants.DATA_TYPE_JSON.equalsIgnoreCase(returnType)){
			contentType = "application/json;charset=UTF-8";	//json格式字符串
			returnStr = "{\"returnStr\":\""+returnStr+"\"}";
		}else{
			contentType = "text/html; charset=UTF-8";
		}
		response.setContentType(contentType);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();
		out.write(returnStr);
		out.close();
	}

}
