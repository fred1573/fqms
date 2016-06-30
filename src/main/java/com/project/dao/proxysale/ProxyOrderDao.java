package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.ProxyParentOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/3.
 */
@Component("orderDao")
public class ProxyOrderDao extends HibernateDao<ProxyParentOrder, String> {

    public ProxyParentOrder findByOtaOrderNoAndOtaId(String otaOrderNo, Integer otaId) {
        String sql = "select * from tomato_proxysale_parent_order poo where poo.ota_order_no=? and poo.ota_id=? and poo.status=?";
        try {
            return findUniqueWithSql(sql, otaOrderNo, otaId, ProxyParentOrder.SUC);
        } catch (Exception e) {
            throw new RuntimeException("sql查询异常，参数otaOrderNo:" + otaOrderNo + ",otaId:" + otaId);
        }
    }

    public void update(ProxyParentOrder parentOrder) {
        getSession().update(parentOrder);
    }

    /**
     * 根据客栈id获取到代销经理
     *

     * @return
     */
    public List<Map<String, Object>> findUserWithInnId() {
        String sql = "SELECT t1.name as name, t2.pms_inn_id as id FROM tomato_crm_user t1 RIGHT  JOIN tomato_crm_distribution t2 ON t1.id=t2.user_id";
        return findListMapWithSql(sql);
    }

    /**
     * 根据区域经理获取客栈id集合
     *
     * @param name
     * @return
     */
    public List<Map<String, Object>> findInnIdWithName(String name) {
        String sql = "SELECT t2.pms_inn_id as id FROM tomato_crm_user t1 RIGHT JOIN tomato_crm_distribution t2 ON t1.id=t2.user_id WHERE t1.name=?";
        return findListMapWithSql(sql, name);
    }
}
