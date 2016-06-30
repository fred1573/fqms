package com.project.web.proxysale;

/**
 * @author frd
 */
public class PriceUpdateView {
    private String regionName;
    private String innName;
    private Integer proxyInnId;

    public PriceUpdateView(String regionName, String innName, Integer proxyInnId) {
        this.regionName = regionName;
        this.innName = innName;
        this.proxyInnId = proxyInnId;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getInnName() {
        return innName;
    }

    public Integer getProxyInnId() {
        return proxyInnId;
    }
}
