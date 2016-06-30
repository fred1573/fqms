package com.project.web.proxysale;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2015/8/26.
 */
public class ProxyContractForm {

    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String from;
    private String to;
    private String status;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getFrom() {
//        if(StringUtils.isBlank(from)){
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.MONTH, -1);
//            Date time = calendar.getTime();
//            from = new SimpleDateFormat("yyyy-MM-dd").format(time);
//        }
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
//        if(StringUtils.isBlank(to)){
//            to = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
//        }
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
