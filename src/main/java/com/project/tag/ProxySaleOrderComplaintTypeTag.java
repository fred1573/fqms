package com.project.tag;

import com.project.enumeration.ProxySaleOrderComplaintType;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * 代销订单投诉类型标签
 *
 * @author yuneng.huang on 2016/6/12.
 */
public class ProxySaleOrderComplaintTypeTag extends TagSupport {

    private ProxySaleOrderComplaintType value;

    public ProxySaleOrderComplaintType getValue() {
        return value;
    }

    public void setValue(ProxySaleOrderComplaintType value) {
        this.value = value;
    }

    @Override
    public int doStartTag() throws JspException {
        StringBuffer results = new StringBuffer();
        results.append("<select>");
        results.append("<option >请选择类型</option>");
        for (ProxySaleOrderComplaintType type : ProxySaleOrderComplaintType.values()) {
            results.append("<option");
            if (type.equals(this.value)) {
                results.append(" selected");
            }
            results.append(" value=\"").append(type.name()).append("\" >").append(type.getDescription()).append("</option>");
        }
        results.append("</select>");
        try {
            this.pageContext.getOut().write(results.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.doStartTag();
    }
}
