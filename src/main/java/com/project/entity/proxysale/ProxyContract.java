package com.project.entity.proxysale;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 代销合同
 * Created by Administrator on 2015/8/24.
 */
public class ProxyContract {

    public static final String STATUS_UNCHECK = "UNCHECK";
    public static final String STATUS_CHECKED = "CHECKED" ;
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_REPEAT = "REPEAT";

    private Integer innId;
    private String status;
    private Date commitTime;//待审核状态时为提交时间，重新提交状态时为重新提交时间
    private String innName;
    private String userName;

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommitTime() {
        return new SimpleDateFormat("yyyy-MM-dd").format(commitTime);
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
