package com.project.bean.proxysale;

/**
 * OMS批量关房，JSON请求对象
 * Created by sam on 2016/4/11.
 */
public class OmsInnCloseDate {
    // PMS客栈ID
    private Integer innId;
    // 关房开始日期
    private String from;
    // 关房结束日期
    private String to;

    public OmsInnCloseDate() {

    }

    public OmsInnCloseDate(Integer innId, String from, String to) {
        this.innId = innId;
        this.from = from;
        this.to = to;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
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
}
