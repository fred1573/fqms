package com.project.service.finance;

import com.project.core.orm.Page;
import com.project.dao.finance.FinanceSpecialOrderDao;
import com.project.entity.finance.FinanceSpecialOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author frd
 */
@Service("financeSpecialOrderService")
@Transactional(rollbackFor = Exception.class)
public class FinanceSpecialOrderServiceImpl implements FinanceSpecialOrderService {

    @Autowired
    private FinanceSpecialOrderDao financeSpecialOrderDao;

    @Override
    public Page<FinanceSpecialOrder> findDebitOrders(Page<FinanceSpecialOrder> page, String settlementTime, Integer channelId, Integer innId, String orderNo, String auditStatus, Short priceStrategy) {
        return financeSpecialOrderDao.findFinanceSpecialOrder(page, FinanceSpecialOrder.STATUS_KEY_DEBIT, innId, channelId, settlementTime, orderNo, auditStatus, priceStrategy);
    }

    @Override
    public Map<String, Object> debitOrderStatistic(String settlementTime, Integer channelId, Integer innId) {
        return financeSpecialOrderDao.debitOrderStatistic(settlementTime, channelId, innId);
    }

    @Override
    public Page<FinanceSpecialOrder> findRefundOrders(Page<FinanceSpecialOrder> page, String settlementTime, Integer channelId, Integer innId, String channelOrderNo, String auditStatus, Short priceStrategy) {
        return financeSpecialOrderDao.findFinanceSpecialOrder(page, FinanceSpecialOrder.STATUS_KEY_REFUND, innId, channelId, settlementTime, channelOrderNo, auditStatus, priceStrategy);
    }

    @Override
    public Map<String, Object> refundOrderStatistic(String settlementTime, Integer channelId, Integer innId) {
        return financeSpecialOrderDao.refundOrderStatistic(settlementTime, channelId, innId);
    }

    @Override
    public Map<String, Object> replenishmentOrderStatistic(String settlementTime, Integer channelId, Integer innId) {
        return financeSpecialOrderDao.replenishmentOrderStatistic(settlementTime, channelId, innId);
    }

    @Override
    public Page<FinanceSpecialOrder> findReplenishmentOrders(Page<FinanceSpecialOrder> page, String settlementTime, Integer channelId, Integer innId, String channelOrderNo, String auditStatus, Short priceStrategy) {
        return financeSpecialOrderDao.findFinanceSpecialOrder(page, FinanceSpecialOrder.STATUS_KEY_REPLENISHMENT, innId, channelId, settlementTime, channelOrderNo, auditStatus, priceStrategy);
    }
}
