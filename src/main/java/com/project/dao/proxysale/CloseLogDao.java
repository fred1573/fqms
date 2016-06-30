package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.CloseLog;
import com.project.utils.CollectionsUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 关房持久化对象
 * Created by 番茄桑 on 2015/9/9.
 */
@Component("closeLogDao")
public class CloseLogDao extends HibernateDao<CloseLog, Integer> {

    public CloseLog selectCloseLogByInnId(Integer innId) {
        return findUniqueWithSql("select * from tomato_proxysale_close_log where inn_id='" + innId + "'");
    }

    /**
     * 查询指定区域ID的关房对象
     *
     * @param areaId
     * @return
     */
    public CloseLog selectCloseLogByAreaId(Integer areaId) {
        List<CloseLog> closeLogList = findWithSql("select * from tomato_proxysale_close_log where close_type = '1' and area_id='" + areaId + "' ORDER BY date_updated desc limit 1");
        if (CollectionsUtil.isNotEmpty(closeLogList)) {
            return closeLogList.get(0);
        }
        return null;
    }

    /**
     * 查询全国的关房记录
     *
     * @return
     */
    public CloseLog selectCloseLogOfAll() {
        List<CloseLog> closeLogList = findWithSql("select * from tomato_proxysale_close_log where close_type='0' ORDER BY date_updated desc limit 1");
        if (CollectionsUtil.isNotEmpty(closeLogList)) {
            return closeLogList.get(0);
        }
        return null;
    }

    public void deleteCloseDate(Integer closeLogId) {
        executeUpdateWithSql("delete from tomato_proxysale_close_date where log_id=" + closeLogId);
    }

    /**
     * 根据时间区间查询关房对象集合
     * @param closeBeginDate
     * @param closeEndDate
     * @return
     */
    public List<CloseLog> selectCloseDateByIntervalTime(String closeBeginDate, String closeEndDate) {
        return findWithSql("SELECT a1.* FROM tomato_proxysale_close_log a1 LEFT JOIN tomato_proxysale_close_date a2 ON a1. ID = a2.log_id WHERE a2.close_begin_date = '" + closeBeginDate + "' and a2.close_end_date = '" + closeEndDate + "'");
    }

}
