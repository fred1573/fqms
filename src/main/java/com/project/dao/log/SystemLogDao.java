package com.project.dao.log;

import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.log.SystemLog;

/**
 * @author 
 * mowei
 */
@Component
public class SystemLogDao extends HibernateDao<SystemLog, Long> {
}
