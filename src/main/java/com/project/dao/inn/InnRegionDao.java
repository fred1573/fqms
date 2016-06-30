package com.project.dao.inn;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.inn.InnRegion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author mowei
 */
@Component
public class InnRegionDao extends HibernateDao<InnRegion, Long> {
    /**
     * 获取目的地名称
     *
     * @return
     */
    public List<Map<String, Object>> findRegionName() {
        String sql = "SELECT id AS id,name as name from tomato_inn_region ";
        return findListMapWithSql(sql);
    }
}
