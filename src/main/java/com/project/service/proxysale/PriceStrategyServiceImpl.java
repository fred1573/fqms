package com.project.service.proxysale;

import com.project.dao.proxysale.PriceStrategyDao;
import com.project.entity.proxysale.PriceStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by Administrator on 2015/7/6.
 */
@Service("priceStrategyService")
@Transactional
public class PriceStrategyServiceImpl implements PriceStrategyService {

    @Autowired
    private PriceStrategyDao priceStrategyDao;

    @Override
    public List<PriceStrategy> findValidByChannel(Integer channelId) {
        return priceStrategyDao.findValidByChannel(channelId);
    }

    @Override
    public PriceStrategy findHistory(Integer channelId, Date time, Short strategy) {
        PriceStrategy history = priceStrategyDao.findHistory(channelId, time, strategy);
        return history;
    }

}
