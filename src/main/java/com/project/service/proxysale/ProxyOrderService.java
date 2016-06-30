package com.project.service.proxysale;

import com.project.entity.proxysale.ProxyParentOrder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 订单
 * Created by Administrator on 2015/7/3.
 */
public interface ProxyOrderService {

    /**
     * 下单
     */
    void createOrder(ProxyParentOrder parentOrder);

    /**
     * 取消订单
     */
    void cancel(ProxyParentOrder parentOrder, BigDecimal penalty);

    ProxyParentOrder findByOtaOrderNoAndOtaId(String otaOrderNo, Integer otaId);

    void createOrder(Collection<ProxyParentOrder> proxyParentOrders);

    /**
     * 根据客栈id获取到代销经理
     *
     * @param innId
     * @return
     */
    Map<Integer,String>  findUserWithInnId();

    /**
     * 根据区域经理获取客栈id集合
     *
     * @param name
     * @return
     */
     List<Integer> findInnIdWithName(String name);
}
