package com.project.service.finance;

import com.project.core.orm.Page;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.account.HibernateUserDao;
import com.project.dao.finance.FinanceManualOrderDao;
import com.project.dao.proxysale.ChannelDao;
import com.project.entity.finance.FinanceManualOrder;
import com.project.service.account.AccountService;
import com.project.web.finance.ManualOrderForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author frd
 */
@Service("financeManualOrderService")
@Transactional
public class FinanceManualOrderServiceImpl implements FinanceManualOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceManualOrderServiceImpl.class);

    @Autowired
    private FinanceManualOrderDao financeManualOrderDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private HibernateUserDao hibernateUserDao;

    @Override
    public Page<FinanceManualOrder> list(Page<FinanceManualOrder> page, Integer channelId, String settlementTime, String orderId) {
        return financeManualOrderDao.list(page, channelId, settlementTime, orderId);
    }

    @Override
    public Map<String, Object> getManualOrderAmount(Integer channelId, String settlementTime, String orderId) {
        return financeManualOrderDao.getManualOrderAmount(channelId, settlementTime, orderId);
    }

    @Override
    public FinanceManualOrder get(Integer id) {
        FinanceManualOrder financeManualOrder = financeManualOrderDao.get(id);
        if(financeManualOrder == null || !financeManualOrder.isAvailable()) {
            LOGGER.error("无订单赔付不存在或已删除,id={}", id);
            throw new RuntimeException("无订单赔付不存在或已删除,id=" + id);
        }
        return financeManualOrder;
    }

    @Override
    public void delete(Integer id) {
        financeManualOrderDao.delete(id);
    }

    @Override
    public void edit(Integer id, String orderId, BigDecimal refund, String remark) {
        FinanceManualOrder financeManualOrder = get(id);
        financeManualOrder.setRefund(refund);
        financeManualOrder.setRemark(remark);
        financeManualOrder.setOrderId(orderId);
        financeManualOrder.setUpdateUser(hibernateUserDao.findUserByUserCode(SpringSecurityUtil.getCurrentUserName()));
        financeManualOrder.setUpdateTime(new Date());
    }

    @Override
    public void add(ManualOrderForm manualOrderForm) {
        FinanceManualOrder financeManualOrder = new FinanceManualOrder();
        financeManualOrder.setChannel(channelDao.get(manualOrderForm.getChannelId()));
        financeManualOrder.setCreateUser(hibernateUserDao.findUserByUserCode(SpringSecurityUtil.getCurrentUserName()));
        financeManualOrder.setOrderId(manualOrderForm.getOrderId());
        financeManualOrder.setRefund(manualOrderForm.getRefund());
        financeManualOrder.setRemark(manualOrderForm.getRemark());
        financeManualOrder.setSettlementTime(manualOrderForm.getSettlementTime());
        financeManualOrderDao.save(financeManualOrder);
    }
}
