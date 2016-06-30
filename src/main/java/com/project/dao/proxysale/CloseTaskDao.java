package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.CloseTask;
import org.springframework.stereotype.Component;

/**
 * 关房任务持久化操作对象
 * Created by sam on 2016/4/12.
 */
@Component("closeTaskDao")
public class CloseTaskDao extends HibernateDao<CloseTask, Integer> {

}
