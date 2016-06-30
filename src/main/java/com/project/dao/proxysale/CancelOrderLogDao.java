package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.CancelOrderLog;
import com.project.utils.CollectionsUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by admin on 2016/4/28.
 */
@Component
public class CancelOrderLogDao extends HibernateDao<CancelOrderLog, Integer> {
    /**
     * 根据订单渠道ID查询操作记录
     * @param channelNo
     * @return
     */
    public CancelOrderLog findCancelLogWithChannelNo(String channelNo){
        String sql=" SELECT col.* from cancel_order_log col WHERE channel_order_no=? ORDER BY operate_time DESC";
        List<CancelOrderLog> logs=findWithSql(sql,channelNo);
        if(CollectionsUtil.isNotEmpty(logs)){
            return logs.get(0);
        }
        return null;
    }
}
