package com.project.service.proxysale;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.project.entity.proxysale.PriceStrategy;
import com.project.entity.proxysale.ProxyInn;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.dao.proxysale.PricePatternDao;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.ProxysaleChannel;

/**
 * Created by Administrator on 2015/7/29.
 */
@Service("pricePatternService")
@Transactional
public class PricePatternServiceImpl implements PricePatternService {

    @Autowired
    private PricePatternDao pricePatternDao;
    @Autowired
    private ProxysaleChannelService proxysaleChannelService;

    @Override
    public List<Integer> findRelationOtaByAccountId(Integer accountId) {
        List<Integer> result = new ArrayList<>();
        if (accountId == null) {
            return result;
        }
        PricePattern pattern = getByAccountId(accountId);
        if (pattern == null) {
            throw new RuntimeException("未找到accId对应的价格模式，accId=" + accountId);
        }
        ProxyInn proxyInn = pattern.getProxyInn();
        List<ProxysaleChannel> pcs;
        if (pattern.getPattern().equals(PricePattern.PATTERN_BASE_PRICE)) {
            pcs = proxysaleChannelService.findValidByProxyId(proxyInn.getId(), PriceStrategy.STRATEGY_BASE_PRICE);
        } else if (pattern.getPattern().equals(PricePattern.PATTERN_SALE_PRICE)) {
            pcs = proxysaleChannelService.findValidByProxyId(proxyInn.getId(), PriceStrategy.STRATEGY_SALE_PRICE, PriceStrategy.STRATEGY_SALE_BASE_PRICE);
        } else {
            throw new RuntimeException("价格模式异常");
        }
        if (CollectionUtils.isEmpty(pcs)) {
            return result;
        }
        for (ProxysaleChannel pc : pcs) {
            result.add(pc.getChannel().getId());
        }
        return result;
    }

    @Override
    public Short getPattern(Integer accountId) {
        PricePattern pricePattern = getByAccountId(accountId);
        if (pricePattern == null) {
            throw new RuntimeException("未找到accId对应的价格模式，accId=" + accountId);
        }
        return pricePattern.getPattern();
    }

    @Override
    public PricePattern getByAccountId(Integer accountId) {
        return pricePatternDao.getByOuterId(accountId);
    }

    @Override
    public Set<PricePattern> batchFindValidPricePattern(List<Integer> proxyInns, PriceStrategy... priceStrategies) {
        List<Short> pricePatterns = new ArrayList<>();
        for (PriceStrategy priceStrategy : priceStrategies) {
            Short strategy = priceStrategy.getStrategy();
            if(strategy.equals(PriceStrategy.STRATEGY_BASE_PRICE)){
                pricePatterns.add(PricePattern.PATTERN_BASE_PRICE);
            }else if(strategy.equals(PriceStrategy.STRATEGY_SALE_PRICE) || strategy.equals(PriceStrategy.STRATEGY_SALE_BASE_PRICE)) {
                pricePatterns.add(PricePattern.PATTERN_SALE_PRICE);
            }
        }
        return pricePatternDao.batchFindByProxyInns(proxyInns, pricePatterns);
    }
}
