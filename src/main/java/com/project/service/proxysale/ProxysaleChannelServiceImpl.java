package com.project.service.proxysale;

import com.project.bean.bo.RelationInnBo;
import com.project.dao.proxysale.PricePatternDao;
import com.project.dao.proxysale.ProxysaleChannelDao;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.ProxyInn;
import com.project.entity.proxysale.ProxysaleChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.project.entity.proxysale.PriceStrategy.STRATEGY_BASE_PRICE;
import static com.project.entity.proxysale.PriceStrategy.STRATEGY_SALE_BASE_PRICE;
import static com.project.entity.proxysale.PriceStrategy.STRATEGY_SALE_PRICE;


@Service
public class ProxysaleChannelServiceImpl implements ProxysaleChannelService {


    @Autowired
    private ProxysaleChannelDao proxysaleChannelDao;
    @Autowired
    private PricePatternDao pricePatternDao;

    @Override
    public List<ProxysaleChannel> findProxysaleChannelByChannel(Integer cid, Short strategy) {
        return proxysaleChannelDao.findByChannelId(cid, strategy);
    }

    @Override
    public List<ProxysaleChannel> findProxysaleChannelByProxyInnId(Integer innId, Short strategy) {
        return proxysaleChannelDao.findByProxyInnId(innId, strategy);
    }

    @Override
    public void delete(Integer id) {
        proxysaleChannelDao.delete(id);
    }

    @Override
    public void update(ProxysaleChannel proxysaleChannel) {
        proxysaleChannelDao.save(proxysaleChannel);

    }

    @Override
    public List<ProxysaleChannel> findValidByProxyId(Integer proxyId, Short... strategies) {
        return proxysaleChannelDao.findValidByProxyId(proxyId, strategies);
    }

    @Override
    public List<ProxysaleChannel> findValidByChannelId(Integer channelId, Short... strategies) {
        return proxysaleChannelDao.findValidByChannelId(channelId, strategies);
    }

    @Override
    public ProxysaleChannel findValidByChannelIdAndAccountId(Integer channelId, Integer accountId) {
        PricePattern pricePattern = pricePatternDao.getByOuterId(accountId);
        ProxysaleChannel proxysaleChannel = null;
        if (pricePattern != null) {
            ProxyInn proxyInn = pricePattern.getProxyInn();
            List<ProxysaleChannel> proxysaleChannels = proxysaleChannelDao.findValidByChannelIdAndInnId(channelId, proxyInn.getId());
            for (ProxysaleChannel channel : proxysaleChannels) {
                if (channel.strategy2Pattern().equals(pricePattern.getPattern())) {
                    proxysaleChannel = channel;
                    break;
                }
            }
        }
        return proxysaleChannel;
    }


    @Override
    public List<RelationInnBo> findRelationInn(Integer channelId) {
        return proxysaleChannelDao.findRelationInn(channelId);
    }
}
