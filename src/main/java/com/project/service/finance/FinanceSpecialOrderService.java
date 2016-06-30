package com.project.service.finance;

import com.project.core.orm.Page;
import com.project.entity.finance.FinanceSpecialOrder;

import java.util.Map;

/**
 * 特殊订单服务
 * @author frd
 */
public interface FinanceSpecialOrderService {

    /**
     * 查询赔付订单
     */
    Page<FinanceSpecialOrder> findDebitOrders(Page<FinanceSpecialOrder> page, String settlementTime, Integer channelId, Integer innId, String orderNo, String auditStatus, Short priceStrategy);

    /**
     * 赔付订单统计
     */
    Map<String, Object> debitOrderStatistic(String settlementTime, Integer channelId, Integer innId);

    /**
     * 查询退款订单
     */
    Page<FinanceSpecialOrder> findRefundOrders(Page<FinanceSpecialOrder> page, String settlementTime, Integer channelId, Integer innId, String channelOrderNo, String auditStatus, Short priceStrategy);

    /**
     * 退款订单统计
     */
    Map<String, Object> refundOrderStatistic(String settlementTime, Integer channelId, Integer innId);

    /**
     * 查询补款订单
     */
    Map<String, Object> replenishmentOrderStatistic(String settlementTime, Integer channelId, Integer innId);

    /**
     * 补款订单统计
     */
    Page<FinanceSpecialOrder> findReplenishmentOrders(Page<FinanceSpecialOrder> page, String settlementTime, Integer channelId, Integer innId, String channelOrderNo, String auditStatus, Short priceStrategy);
}
