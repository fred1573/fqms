package com.project.web.proxysale;

/**
 * Created by Administrator on 2015/6/25.
 */
public class ApiPricePattern {

    private String pattern;
    private Integer accountId;

    public ApiPricePattern(String pattern, Integer accountId) {
        this.pattern = pattern;
        this.accountId = accountId;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
