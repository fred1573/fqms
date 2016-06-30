package com.project.service.proxysale;

import com.project.entity.proxysale.ProxyInnOnoff;

import java.util.Date;

/**
 * @author Administrator
 *         2015-10-15 15:53
 */
public interface ProxyInnOnoffService {

    void save(ProxyInnOnoff innOnoff);

    Long count(Integer proxyInnId, Short pattern);

    Long count(Integer proxyInnId, Short pattern, Date from, Date to);
    
    /**
     *  获取指定客栈的最后一次下架记录
     * @param innerId
     * @return
     */
    ProxyInnOnoff getLastRow(Integer innerId);
}
