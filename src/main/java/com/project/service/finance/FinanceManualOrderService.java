package com.project.service.finance;

import com.project.core.orm.Page;
import com.project.entity.finance.FinanceManualOrder;
import com.project.web.finance.ManualOrderForm;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 无订单赔付
 * @author frd
 */
public interface FinanceManualOrderService {

    Page<FinanceManualOrder> list(Page<FinanceManualOrder> page, Integer channelId, String settlementTime, String orderId);

    Map<String, Object> getManualOrderAmount(Integer channelId, String settlementTime, String orderId);

    void add(ManualOrderForm manualOrderForm);

    FinanceManualOrder get(Integer id);

    void edit(Integer id, String orderId, BigDecimal refund, String remark);

    void delete(Integer id);
}
