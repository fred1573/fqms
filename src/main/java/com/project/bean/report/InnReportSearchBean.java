package com.project.bean.report;

import java.io.Serializable;

/**
 * @author Simon
 */
public class InnReportSearchBean implements Serializable {
    private boolean xzOnly = false;
    private String innName;
    private int pageNo;
    private int pageSize = 15;

    public boolean isXzOnly() {
        return xzOnly;
    }

    public void setXzOnly(boolean xzOnly) {
        this.xzOnly = xzOnly;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
