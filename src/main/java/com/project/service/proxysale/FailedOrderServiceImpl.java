package com.project.service.proxysale;

import com.project.dao.proxysale.FailedOrderDao;
import com.project.entity.proxysale.ProxyFailedOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Administrator on 2015/7/3.
 */
@Service("failedOrderService")
@Transactional
public class FailedOrderServiceImpl implements FailedOrderService {

    @Autowired
    private FailedOrderDao failedOrderDao;

    @Override
    public void create(ProxyFailedOrder failedOrder) {
        failedOrderDao.save(failedOrder);
    }
}
