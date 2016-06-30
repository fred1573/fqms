package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.ProxyInnOnoff;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Administrator
 *         2015-10-15 15:50
 */
@Component
public class ProxyInnOnoffDao extends HibernateDao<ProxyInnOnoff, Long> {
    /**
     * 根据客栈ID查询客栈的下架历史记录集合
     *
     * @param proxyInnId 代销客栈ID
     * @return
     */
    public List<ProxyInnOnoff> selectProxyInnOffList(Integer proxyInnId) {
        return findWithSql("select * from tomato_proxysale_inn_onoff where operate_type='OFF' and proxy_inn='" + proxyInnId + "' order by time desc");
    }

    /**
     *  获取指定客栈最后一次下架信息
     * @param proxyInnId
     * @return
     */
    public ProxyInnOnoff selectLastRow(Integer proxyInnId) {
        return findUniqueWithSql("select * from tomato_proxysale_inn_onoff where operate_type='OFF' and proxy_inn='" + proxyInnId + "' order by time desc  limit 1 offset 0 ");
    }

}
