package com.project.dao.inn;

import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.inn.InnLog;

/**
 * @author 
 * mowei
 */
@Component
public class InnLogDao extends HibernateDao<InnLog, Long> {
}
