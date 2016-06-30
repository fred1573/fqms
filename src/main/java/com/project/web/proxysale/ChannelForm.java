package com.project.web.proxysale;

/**
 *
 * Created by Administrator on 2015/6/16.
 */
public class ChannelForm {

    private String channelName;
    private Float basePercentage;
    private Float salePercentage;
    private Integer[] areaIds;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Float getBasePercentage() {
        return basePercentage;
    }

    public void setBasePercentage(Float basePercentage) {
        this.basePercentage = basePercentage;
    }

    public Float getSalePercentage() {
        return salePercentage;
    }

    public void setSalePercentage(Float salePercentage) {
        this.salePercentage = salePercentage;
    }

    public Integer[] getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(Integer[] areaIds) {
        this.areaIds = areaIds;
    }
}
