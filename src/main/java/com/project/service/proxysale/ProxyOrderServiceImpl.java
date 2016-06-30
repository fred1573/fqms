package com.project.service.proxysale;

import com.project.dao.proxysale.ProxyOrderDao;
import com.project.entity.proxysale.ProxyParentOrder;
import com.project.utils.CollectionsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2015/7/3.
 */
@Service("orderService")
@Transactional
public class ProxyOrderServiceImpl implements ProxyOrderService {

    @Autowired
    private ProxyOrderDao orderDao;

    @Override
    public void createOrder(ProxyParentOrder parentOrder) {
        orderDao.save(parentOrder);
    }

    @Override
    public void cancel(ProxyParentOrder parentOrder, BigDecimal penalty) {
        if (parentOrder == null) {
            return;
        }
        //违约金须大于等于0
        if (penalty.compareTo(new BigDecimal(0)) < 0) {
            return;
        }
        parentOrder.setPenalty(penalty);
        parentOrder.setStatus(ProxyParentOrder.CANCEL);
        orderDao.update(parentOrder);
    }

    @Override
    public ProxyParentOrder findByOtaOrderNoAndOtaId(String otaOrderNo, Integer otaId) {
        try {
            return orderDao.findByOtaOrderNoAndOtaId(otaOrderNo, otaId);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void createOrder(Collection<ProxyParentOrder> proxyParentOrders) {
        for (ProxyParentOrder parentOrder : proxyParentOrders) {
            createOrder(parentOrder);
        }
    }

    @Override
    public Map<Integer, String> findUserWithInnId() {
        List<Map<String, Object>> userWithInnId = orderDao.findUserWithInnId();
        Map<Integer, String> listMap = new HashMap<>();
        Integer innId = null;
        String innName = null;
        if (CollectionsUtil.isNotEmpty(userWithInnId)) {
            for (Map<String, Object> map : userWithInnId) {
                innId = (Integer) map.get("id");
                innName = (String) map.get("name");
                if (null != innId && null != innName) {
                    listMap.put(innId, innName);
                }
            }
            return listMap;
        }
        return null;
    }

    @Override
    public List<Integer> findInnIdWithName(String name) {
        List<Map<String, Object>> innIdWithName = orderDao.findInnIdWithName(name);
        List<Integer> list = new ArrayList<>();
        Integer innId = null;
        if (CollectionsUtil.isNotEmpty(innIdWithName)) {
            for (Map<String, Object> map : innIdWithName) {
                innId = (Integer) map.get("id");
                if (null != innId) {
                    list.add(innId);
                }
            }
            return list;
        }
        return null;
    }
}
