package com.project.bean.proxysale;

import java.util.Date;

/**
 * @author frd
 */
public class PriceDetailQuery {

    private Integer outerId;
    private Integer channelId;
    private Date from;
    private Date to;

    public PriceDetailQuery(Integer outerId, Integer channelId, Date from, Date to) {
        this.outerId = outerId;
        this.channelId = channelId;
        this.from = from;
        this.to = to;
    }

    public Integer getOuterId() {
        return outerId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

}
