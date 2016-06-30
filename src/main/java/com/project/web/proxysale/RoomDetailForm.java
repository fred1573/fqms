package com.project.web.proxysale;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author frd
 */
public class RoomDetailForm {

    private Integer channelId;
    private Integer proxyInnId;
    private String from;
    private String to;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getProxyInnId() {
        return proxyInnId;
    }

    public void setProxyInnId(Integer proxyInnId) {
        this.proxyInnId = proxyInnId;
    }

    public String getFrom() {
        if(StringUtils.isBlank(from)) {
            from = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        if(StringUtils.isBlank(to)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 15);
            to = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        }
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
