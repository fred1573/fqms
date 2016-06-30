package com.project.service.proxysale;

import com.project.bean.proxysale.SyncChannel;
import com.project.dao.proxysale.ChannelDao;
import com.project.dao.proxysale.PriceStrategyDao;
import com.project.entity.proxysale.Channel;
import com.project.entity.proxysale.PriceStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author frd
 */
@Component
public class PriceStrategyBean {

    @Autowired
    private PriceStrategyDao priceStrategyDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private SyncChannel syncChannel;

    public void add(PriceStrategy... priceStrategies) {
        // 需要推送的佣金策略集合
        List<PriceStrategy> needSyncValidStrategies = new ArrayList<>();
        for (PriceStrategy priceStrategy : priceStrategies) {
            boolean commissionUpdated = add(priceStrategy);
            if (commissionUpdated) {
                needSyncValidStrategies.add(priceStrategy);
            }
        }
        Channel channel = channelDao.get(priceStrategies[0].getChannel());
        syncChannel.syncCommission(channel, needSyncValidStrategies);
    }

    private boolean add(PriceStrategy priceStrategy) {
        Float percentage = priceStrategy.getPercentage();
        Short strategy = priceStrategy.getStrategy();
        Integer channelId = priceStrategy.getChannel();
        PriceStrategy validStrategy = priceStrategyDao.findValidByChannelAndStrategy(channelId, strategy);
        if (validStrategy == null) {
            if (percentage != null) {
                priceStrategyDao.save(priceStrategy);
                return true;
            } else {
                return false;
            }
        } else {
            if (percentage == null) {
                disableStrategy(validStrategy);
                return false;
            } else {
                Float validPercentage = validStrategy.getPercentage();
                if (!validPercentage.equals(percentage)) {
                    disableStrategy(validStrategy);
                    priceStrategyDao.save(priceStrategy);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    private void disableStrategy(PriceStrategy validStrategy) {
        if (validStrategy != null && validStrategy.isValid()) {
            validStrategy.setValid(false);
            priceStrategyDao.update(validStrategy);
        }
    }

}
