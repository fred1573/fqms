package com.project.dao.report;

import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.report.RegionCount;

/**
 * 区域入住率统计DAO
 * @author X
 *
 */

@Component
public class RegionCountDao extends HibernateDao<RegionCount, Long> {

}
