package com.project.bean.direct;

import java.util.List;

/**
 * 用于提交直连订单的form表单对象
 * Created by 番茄桑 on 2015/7/28.
 */
public class ProxySaleOrderForm {
    // 渠道来源
    private String channelId;
    // 子分销商
    private String childChannelId;
    // 价格模式
    private String pricePattern;
    // 订单状态，0:未处理、1:已接受、2:已拒绝、3:已取消
    private String orderStatus;
    // 查询关键字
    private String queryValue;
    // 查询日期类型,CHECK_IN,//入住 CHECK_OUT ,//离店 CREATE //下单
    private String searchTimeType;
    // 查询开始时间
    private String startDate;
    // 查询结束时间
    private String endDate;
    // 当前页数
    private int page = 1;
    // 页容量
    private int rows = 15;
    // 价格模式
    private Integer strategyType;
    //查询类型(1:客栈名称2:分销商订单号 3:oms订单号 4:目的地 5:客户经理)
    private String queryType;

    private String otaId;

    public String getOtaId() {
        return otaId;
    }

    public void setOtaId(String otaId) {
        this.otaId = otaId;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getChildChannelId() {
        return childChannelId;
    }

    public void setChildChannelId(String childChannelId) {
        this.childChannelId = childChannelId;
    }

    public Integer getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(Integer strategyType) {
        this.strategyType = strategyType;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPricePattern() {
        return pricePattern;
    }

    public void setPricePattern(String pricePattern) {
        this.pricePattern = pricePattern;
    }

    public String getQueryValue() {
        return queryValue;
    }

    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
    }

    public String getSearchTimeType() {
        return searchTimeType;
    }

    public void setSearchTimeType(String searchTimeType) {
        this.searchTimeType = searchTimeType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
