package com.project.bean.proxysale;

import com.project.enumeration.ProxySaleOrderComplaintStatus;
import com.project.enumeration.ProxySaleOrderComplaintType;
import com.project.enumeration.SearchTimeType;
import com.project.enumeration.SearchType;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * @author yuneng.huang on 2016/6/13.
 */
public class OrderComplaintSearch {

    private SearchType searchType;
    private SearchTimeType searchTimeType;

    //查询字段 目的地，客户经理
    private String queryValue;
    //查询开始日期
    private Date startTime;
    //查询结束日期
    private Date endTime;
    //投诉类型
    private ProxySaleOrderComplaintType complaintType;
    //投诉处理状态
    private ProxySaleOrderComplaintStatus complaintStatus;
    // 渠道订单Id
    private String channelId;
    // 渠道名
    private String channelName;
    // 子渠道名
    private String childChannelName;

    public String getChildChannelName() {
        return childChannelName;
    }

    public void setChildChannelName(String childChannelName) {
        this.childChannelName = childChannelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public SearchTimeType getSearchTimeType() {
        return searchTimeType;
    }

    public void setSearchTimeType(SearchTimeType searchTimeType) {
        this.searchTimeType = searchTimeType;
    }

    public String getQueryValue() {
        return queryValue;
    }

    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        if (this.endTime != null) {
            return new DateTime(this.endTime).plusDays(1).minusSeconds(1).toDate();
        }
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public ProxySaleOrderComplaintType getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(ProxySaleOrderComplaintType complaintType) {
        this.complaintType = complaintType;
    }

    public ProxySaleOrderComplaintStatus getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(ProxySaleOrderComplaintStatus complaintStatus) {
        this.complaintStatus = complaintStatus;
    }
}
