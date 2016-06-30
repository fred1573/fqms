package com.project.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.project.core.utils.spring.SpringContextHolder;
import com.project.entity.api.ApiChannel;
import com.project.entity.wg.WgOtaInfo;
import com.project.service.api.ApiChannelService;
import com.project.service.wg.WgOtaInfoManager;
import com.project.utils.CacheUtil;
import com.project.utils.ListUtil;

public class ApiChannelTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -634166859513319908L;
	private String value = null;

	public int doStartTag() throws JspException {
		StringBuffer results = new StringBuffer("");
		if ((this.value != null) && (!(this.value.trim().equals("")))) {
			String condspVal = getChannelName(this.value.trim());
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

	
	protected String getChannelName(String conVal) {
		List<WgOtaInfo> apiChannels = CacheUtil.getWgOtaInfos();
		if(ListUtil.isNotEmpty(apiChannels)){
			for (WgOtaInfo c : apiChannels) {
				if(Integer.parseInt(conVal) == c.getId()){
					return c.getName();
				}
			}
		}
		return "";
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