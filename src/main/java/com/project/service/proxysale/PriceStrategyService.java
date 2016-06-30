package com.project.service.proxysale;

import com.project.entity.proxysale.PriceStrategy;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/7/6.
 */
public interface PriceStrategyService {

    List<PriceStrategy> findValidByChannel(Integer channelId);

    /**
     * 按时间查询历史比例
     * @param time
     * @param strategy
     * @return
     */
    PriceStrategy findHistory(Integer channelId, Date time, Short strategy);

}
