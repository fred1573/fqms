package com.project.tag;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.project.bean.Item;
import com.project.cache.abstractCache.IDataCache;
import com.project.cache.manager.LoadCacheDataManager;

public class WriteTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -634166859513319908L;
	private String type = null;
	private String value = null;

	public int doStartTag() throws JspException {
		StringBuffer results = new StringBuffer("");
		if ((this.value != null) && (!(this.value.trim().equals("")))) {
			String condspVal = getSysConDspval(this.value.trim());
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

	@SuppressWarnings("rawtypes")
	protected String getSysConDspval(String conVal) {
		if (this.type == null) {
			return null;
		}
		IDataCache dataCache = LoadCacheDataManager.get("SysConstantCache");
		List list = (List) dataCache.get(this.type);
		if ((list != null) && (!(list.isEmpty()))) {
			for (Iterator localIterator = list.iterator(); localIterator
					.hasNext();) {
				Object obj = localIterator.next();
				Item item = (Item) obj;
				if (item.getValue().equals(conVal)) {
					return item.getName();
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

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return this.value.trim();
	}

	public void setValue(String value) {
		this.value = value;
	}
}