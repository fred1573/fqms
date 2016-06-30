package com.project.bean.report;

import java.io.Serializable;

/**
 * @author Simon
 */
public class InnReportBean implements Serializable {
    private Integer id;
    private String innName;
    private String xzName;
    private String webSite;
    private String openMc;
    private boolean hasBrand = false;

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getXzName() {
        return xzName;
    }

    public void setXzName(String xzName) {
        this.xzName = xzName;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public boolean isHasBrand() {
        return hasBrand;
    }

    public void setHasBrand(boolean hasBrand) {
        this.hasBrand = hasBrand;
    }

    public String getOpenMc() {
        return openMc;
    }

    public void setOpenMc(String openMc) {
        this.openMc = openMc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
