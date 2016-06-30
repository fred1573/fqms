package com.project.service.proxysale;

/**
 * @author Hunhun
 *         2015-09-17 17:23
 */
public interface ChannelInnService {

    /**
     * 是否已关联
     */
    boolean isExist(Integer channelId, Integer proxyInnId);
}
