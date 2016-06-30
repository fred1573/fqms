package com.project.service.proxysale;

import com.project.entity.proxysale.ProxyFailedOrder;

/**
 * Created by Administrator on 2015/7/3.
 */
public interface FailedOrderService {

    void create(ProxyFailedOrder failedOrder);
}
