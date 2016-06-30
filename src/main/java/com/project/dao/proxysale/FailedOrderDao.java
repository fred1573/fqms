package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.ProxyFailedOrder;
import org.springframework.stereotype.Component;

/**
 *
 * Created by Administrator on 2015/7/3.
 */
@Component("failedOrderDao")
public class FailedOrderDao extends HibernateDao<ProxyFailedOrder, String> {
}
