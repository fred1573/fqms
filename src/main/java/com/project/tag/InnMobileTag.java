package com.project.tag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;

import com.project.cache.InnCache;
import com.project.cache.abstractCache.IDataCache;
import com.project.common.Constants;
import com.project.core.utils.spring.SpringContextHolder;
import com.project.dao.api.ApiChannelDao;
import com.project.entity.inn.Inn;
import com.project.utils.ListUtil;

public class InnMobileTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -634166859513319908L;
	private String value = null;

	public int doStartTag() throws JspException {
		StringBuffer results = new StringBuffer("");
		if ((this.value != null) && (!(this.value.trim().equals("")))) {
			String condspVal = getMobile(this.value.trim());
			if (condspVal != null)
				results.append(condspVal);
		}
		try {
			this.pageContext.getOut().write(results.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	
	@SuppressWarnings("unchecked")
	protected String getMobile(String conVal) {
		IDataCache dataCache = new InnCache();
		List<Inn> inns = (List<Inn>) dataCache.getOrElse(Constants.CACHE_FLAG_ALL_INN);
		if(ListUtil.isNotEmpty(inns)){
			for (Inn c : inns) {
				if(Integer.parseInt(conVal) == c.getId()){
					if(StringUtils.isBlank(c.getMobile())){
						setMobile(c);
					}
					return c.getMobile();
				}
			}
		}
		return "";
	}
	
	private void setMobile(Inn inn) {
		ApiChannelDao apiChannelDao = SpringContextHolder.getBean("apiChannelDao");
		String sql = "select m.mobile as mobile from tomato_inn_admin m where m.inn_id = ? and m.parent_id is null";
		Map<String, Object> result = apiChannelDao.findMapWithSql(sql, inn.getId());
		inn.setMobile((String) result.get("mobile"));
	}

	protected void prepareAttribute(StringBuffer handlers, String name,
			Object value) {
		if (value != null) {
			handlers.append(" ");
			handlers.append(name);
			handlers.append("=\"");
			handlers.append(value);
			handlers.append("\"");
		}
	}

	public String getValue() {
		return this.value.trim();
	}

	public void setValue(String value) {
		this.value = value;
	}
}