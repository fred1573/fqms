package com.project.service.proxysale;

import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.PriceStrategy;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2015/7/6.
 */
public interface PricePatternService {

    /**
     * 根据accountId查询ota
     *
     * @param accountId oms凭证
     * @return list
     */
    List<Integer> findRelationOtaByAccountId(Integer accountId);

    /**
     * 获取模式
     *
     * @param accountId oms凭证
     * @return pattern
     */
    Short getPattern(Integer accountId);

    PricePattern getByAccountId(Integer accountId);

    Set<PricePattern> batchFindValidPricePattern(List<Integer> proxyInns, PriceStrategy ... priceStrategies);
}
