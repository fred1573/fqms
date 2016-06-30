package com.project.bean.direct;

/**
 * 用于提交直连订单的form表单对象
 * Created by 番茄桑 on 2015/7/28.
 */
public class DirectOrderForm {
    // 渠道来源
    private String channelId;
    // 订单状态，0:未处理、1:已接受、2:已拒绝、3:已取消
    private String orderStatus;
    // 客栈名称
    private String innName;
    // 查询日期类型,CHECK_IN,//入住 CHECK_OUT ,//离店 CREATE //下单
    private String searchTimeTyep;
    // 查询开始时间
    private String startDate;
    // 查询结束时间
    private String endDate;
    // 当前页数
    private int page = 1;

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

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getSearchTimeTyep() {
        return searchTimeTyep;
    }

    public void setSearchTimeTyep(String searchTimeTyep) {
        this.searchTimeTyep = searchTimeTyep;
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
