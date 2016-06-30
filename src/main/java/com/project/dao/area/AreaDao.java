package com.project.dao.area;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.area.Area;
import com.project.utils.CollectionsUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author frd
 */
@Component("areaDao")
public class AreaDao extends HibernateDao<Area, Integer> {

    public Area getCityByCode(String code) {
        String hql = "from Area area where area.level=3 and area.code=?";
        return (Area) getSession().createQuery(hql).setString(0, code).uniqueResult();
    }

    public Area selectAreaByAreaName(String areaName) {
        List<Area> areaList = findWithSql("select * from tomato_base_area area where area.name=? ORDER BY area.level DESC", areaName);
        if (!CollectionsUtil.isEmpty(areaList)) {
            return areaList.get(0);
        }
        return null;
    }

    /**
     * 根据ID查询区域
     * @param id
     * @return
     */
    public Area selectAreaById(Integer id) {
        return findUniqueWithSql("select * from tomato_base_area where id='" + id + "'");
    }
}
