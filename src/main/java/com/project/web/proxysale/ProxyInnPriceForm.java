package com.project.web.proxysale;

import com.project.service.proxysale.ProxyInnPriceService;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2015/8/27.
 */
public class ProxyInnPriceForm {
    private String keyword;  //审核单号、客栈名称
    private String status;  //状态  eg.  审核通过、待审核、否决、取消
    private String pattern;  //合作模式   eg. 普通代销、精品代销
    private String from;  //提交审核单的时间
    private String to;  //提交审核单的时间
    private String pageSize = "10";
    private String pageNo = "1";

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        if(StringUtils.isBlank(status)){
            status = ProxyInnPriceService.STATUS_EVERY;
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        if(StringUtils.isBlank(pageSize)){
            pageSize = "10";
        }
        this.pageSize = pageSize;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        if(StringUtils.isBlank(pageNo)){
            pageNo = "1";
        }
        this.pageNo = pageNo;
    }
}
