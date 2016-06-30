package com.project.bean.finance;

import com.project.utils.time.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

/**
 * 数据统计和数据分析表单数据封装类
 * Created by sam on 2015/12/24.
 */
public class DataForm {
    // 查询开始时间
    private String beginDate;
    // 查询结束时间
    private String endDate;
    // 渠道名称
    private String channelName;
    // 渠道ID
    private Integer channelId;
    // 是否只统计已接受订单（即status=1）
    private boolean isAcceptedOnly;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getBeginDate() {
        if (StringUtils.isBlank(endDate)) {
            Calendar curr = Calendar.getInstance();
            curr.set(Calendar.DAY_OF_MONTH, curr.get(Calendar.DAY_OF_MONTH) - 7);
            return DateUtil.format(curr.getTime());
        }
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        if (StringUtils.isBlank(beginDate)) {
            Calendar curr = Calendar.getInstance();
            curr.set(Calendar.DAY_OF_MONTH, curr.get(Calendar.DAY_OF_MONTH) - 1);
            return DateUtil.format(curr.getTime());
        }
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public boolean getIsAcceptedOnly() {
        return isAcceptedOnly;
    }

    public void setIsAcceptedOnly(boolean isAcceptedOnly) {
        this.isAcceptedOnly = isAcceptedOnly;
    }
}
