package com.project.web.proxysale;

import java.util.Set;

/**
 * Created by Administrator on 2015/6/25.
 */
public class ApiProxyInn {

    private Integer innId;
    private Set<ApiPricePattern> pricePatterns;

    public ApiProxyInn(Integer innId, Set<ApiPricePattern> apiPricePatterns) {
        this.innId = innId;
        this.pricePatterns = apiPricePatterns;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public Set<ApiPricePattern> getPricePatterns() {
        return pricePatterns;
    }

    public void setPricePatterns(Set<ApiPricePattern> pricePatterns) {
        this.pricePatterns = pricePatterns;
    }
}
