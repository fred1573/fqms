package com.project.service.proxysale;

import com.project.dao.proxysale.ChannelInnDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hunhun
 *         2015-09-17 17:27
 */
@Service
public class ChannelInnServiceImpl implements ChannelInnService {

    @Autowired
    private ChannelInnDao channelInnDao;

    @Override
    public boolean isExist(Integer channelId, Integer proxyInnId) {
        return channelInnDao.count(channelId, proxyInnId) > 0;
    }
}
