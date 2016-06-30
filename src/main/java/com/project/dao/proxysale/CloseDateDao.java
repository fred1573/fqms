package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.CloseDate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 关房日期操作类
 * Created by sam on 2015/12/2.
 */
@Component("closeDateDao")
public class CloseDateDao extends HibernateDao<CloseDate, Integer> {
    /**
     * 根据客栈ID查询该客栈有效的关房记录
     *
     * @param innId
     * @return
     */
    public List<CloseDate> selectCloseDateByInnId(Integer innId) {
        return findWithSql("SELECT cd.* FROM tomato_proxysale_close_date cd LEFT JOIN tomato_proxysale_close_log cl ON cl. ID = cd. log_id WHERE cl.inn_id = '" + innId + "' AND cd.close_end_date >= to_char(now(), 'yyyy-MM-dd')");
    }

    /**
     * 查询全国有效的关房记录
     * 过期的关房记录不在查询范围内
     *
     * @return
     */
    public List<CloseDate> selectAllCloseDate() {
        return findWithSql("SELECT cd.* FROM tomato_proxysale_close_date cd LEFT JOIN tomato_proxysale_close_log cl ON cl. ID = cd. log_id WHERE cl.close_type='0' AND cd.close_end_date >= to_char(now(), 'yyyy-MM-dd')");
    }

    /**
     * 查询指定区域ID的关房记录
     * 过期的关房记录不在查询范围内
     *
     * @param areaId 区域ID
     * @return
     */
    public List<CloseDate> selectCloseDateByAreaId(Integer areaId) {
        return findWithSql("SELECT cd.* FROM tomato_proxysale_close_date cd LEFT JOIN tomato_proxysale_close_log cl ON cl. ID = cd. log_id WHERE cl.close_type='1' AND cl.area_id='" + areaId + "' AND cd.close_end_date >= to_char(now(), 'yyyy-MM-dd')");
    }

    public void deleteCloseDateById(Integer id) {
        executeUpdateWithSql("delete from tomato_proxysale_close_date where id='" + id + "'");
    }

}
