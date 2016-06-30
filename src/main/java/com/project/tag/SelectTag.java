package com.project.tag;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.project.enumeration.Status;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.project.bean.Item;
import com.project.cache.abstractCache.IDataCache;
import com.project.cache.manager.LoadCacheDataManager;

public class SelectTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4466346309052553848L;
	
	protected final Log log = LogFactory.getLog(super.getClass());
	private String id = null;
	private String name = null;
	private String onclick = null;
	private String onchange = null;
	private String onfocus = null;
	private boolean disabled = false;
	private boolean readonly = false;
	private String style = null;
	private String styleClass = null;
	private String size = null;
	private String value = null;
	private String type = null;
	private boolean blank = false;
	private boolean showValue = true;
	private String optionDisableValue = null;
	private String optionNoShowValue = null;

	public int doStartTag() throws JspException {
		StringBuffer results = new StringBuffer("<select");
		results = getAllProp(results);
		results.append(">");
		if (this.blank) {
			if (this.value == null)
				results.append("<option value=\"\" selected=\"selected\">请选择</option>");
			else {
				results.append("<option value=\"\">请选择</option>");
			}
		}

		List<Item> dics = getDicts();
		if (dics != null) {
			int size = dics.size();
			for (int i = 0; i < size; ++i) {
				Item item = dics.get(i);
				if(Status.ENABLED.equals(item.getStatus())){
					if(StringUtils.isNotBlank(this.optionNoShowValue) && this.optionNoShowValue.equals(item.getValue())){
						continue;
					}else{
						results.append("<option value=\"");
						results.append(item.getValue());
						results.append("\"");
						if (item.getValue().equals(this.value)) {
							results.append(" selected=\"selected\"");
						}
						if(StringUtils.isNotBlank(this.optionDisableValue)){
							List<String> a = Arrays.asList(this.optionDisableValue.split(","));
							if (a.contains(item.getValue())) {
								results.append(" disabled=\"disabled\"");
							}
						}
						results.append(">");
						if (this.showValue) {
							results.append(item.getValue());
							results.append(" - ");
						}
						results.append(formatStr(item.getName()));
						results.append("</option>");
					}
				}
			}
		}
		results.append("</select>");
		try {
			this.pageContext.getOut().write(results.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	private List<Item> getDicts() {
		if (this.type == null) {
			return null;
		}
		IDataCache dataCache = LoadCacheDataManager.get("SysConstantCache");
		List<Item> list = (List<Item>) dataCache.get(this.type);
		return list;
	}

	private String formatStr(String s) {
		if (s != null) {
			return s.trim();
		}
		return "";
	}

	private StringBuffer getAllProp(StringBuffer results) throws JspException {
		prepareAttribute(results, "onclick", getOnclick());
		prepareAttribute(results, "onchange", getOnchange());
		prepareAttribute(results, "onfocus", getOnfocus());

		if (getDisabled()) {
			results.append(" disabled=\"disabled\"");
		}
		if (getReadonly()) {
			results.append(" readonly=\"readonly\"");
		}
		prepareAttribute(results, "style", getStyle());
		prepareAttribute(results, "class", getStyleClass());
		prepareAttribute(results, "name", getName());
		prepareAttribute(results, "id", getId());
		prepareAttribute(results, "size", getSize());
		prepareAttribute(results, "value", getValue());

		return results;
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

	public String getId() {
		return this.id;
	}

	public void setId(String value) {
		this.id = value;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isBlank() {
		return this.blank;
	}

	public void setBlank(boolean blank) {
		this.blank = blank;
	}

	public String getSize() {
		return this.size;
	}

	public void setSize(String value) {
		this.size = value;
	}

	public void setOnclick(String value) {
		this.onclick = value;
	}

	public String getOnclick() {
		return this.onclick;
	}

	public void setOnchange(String value) {
		this.onchange = value;
	}

	public String getOnchange() {
		return this.onchange;
	}

	public void setOnfocus(String value) {
		this.onfocus = value;
	}

	public String getOnfocus() {
		return this.onfocus;
	}

	public void setDisabled(boolean value) {
		this.disabled = value;
	}

	public boolean getDisabled() {
		return this.disabled;
	}

	public void setReadonly(boolean value) {
		this.readonly = value;
	}

	public boolean getReadonly() {
		return this.readonly;
	}

	public void setStyle(String value) {
		this.style = value;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyleClass(String value) {
		this.styleClass = value;
	}

	public String getStyleClass() {
		return this.styleClass;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean getShowValue() {
		return this.showValue;
	}

	public void setShowValue(boolean showValue) {
		this.showValue = showValue;
	}

	public String getOptionDisableValue() {
		return optionDisableValue;
	}

	public void setOptionDisableValue(String optionDisableValue) {
		this.optionDisableValue = optionDisableValue;
	}

	public String getOptionNoShowValue() {
		return optionNoShowValue;
	}

	public void setOptionNoShowValue(String optionNoShowValue) {
		this.optionNoShowValue = optionNoShowValue;
	}
	
}