package com.project.web.proxysale;

/**
 * Created by Administrator on 2015/6/19.
 */
public class ProxyInnFormAdd {

    //outer inn_id
    private String accountId;
    //价格模式
    private String pricePattern;
    //json格式
    private String data;



    public String getPricePattern() {
        return pricePattern;
    }

    public void setPricePattern(String pricePattern) {
        this.pricePattern = pricePattern;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProxyInnFormAdd{" +
                "accountId='" + accountId + '\'' +
                ", pricePattern='" + pricePattern + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
