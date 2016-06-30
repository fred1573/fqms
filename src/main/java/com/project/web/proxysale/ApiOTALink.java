package com.project.web.proxysale;

/**
 * @author Administrator
 *         2015-09-29 16:36
 */
public class ApiOTALink {

    private Integer innId;
    private String otaLink;

    public ApiOTALink(Integer innId, String otaLink) {
        this.innId = innId;
        this.otaLink = otaLink;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public String getOtaLink() {
        return otaLink;
    }

    public void setOtaLink(String otaLink) {
        this.otaLink = otaLink;
    }
}
