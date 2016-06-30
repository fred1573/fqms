package com.project.dao.ota;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.ota.OtaInfo;
import org.springframework.stereotype.Component;

/**
 *
 * Created by Administrator on 2015/6/25.
 */
@Component("otaInfoDao")
public class OtaInfoDao extends HibernateDao<OtaInfo, Integer> {

    public Page<OtaInfo> list(Page<OtaInfo> page){
        //tomato_oms_ota_info中pid=102代表是代销渠道
        String sqlQuery = "select tooi.id, tooi.name, tooi.ota_id, tooi.pid from tomato_oms_ota_info tooi where tooi.pid=102 and tooi.ota_id<>tooi.pid";
        return findPageWithSql(page, sqlQuery);
    }

    public OtaInfo getByOtaId(Integer otaId){
        String hql = "select oi from OtaInfo oi where oi.otaId=?";
        OtaInfo otaInfo = findUnique(hql, otaId);
        return otaInfo;
    }
}
