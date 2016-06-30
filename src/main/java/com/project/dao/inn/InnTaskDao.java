package com.project.dao.inn;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.inn.InnTask;
import com.project.utils.time.DateUtil;

/**
 * @author 
 * X
 */
@Component
public class InnTaskDao extends HibernateDao<InnTask, Long> {
	
	/**
	 * 保存
	 */
	public void save(InnTask task){
		task.setNum(1);
		task.setRecordedAt(new Date());
		super.save(task);
	}
	
	/**
	 * 删除某时间之前的记录
	 * @param time
	 */
	public void removeByDate(Date time){
		this.createSqlQuery("delete from tomato_sys_daily_task where record_at < ?", time);
	}
	
	/**
	 * 查找某客栈某天是否已经完成了某任务
	 * @param time
	 * @param funcItemType
	 * @param task
	 * @return
	 */
	public InnTask findByTypeAndDate(Date time, Integer funcItemType, InnTask task){
		String from = DateUtil.format(time);
		String to = from + " 23:59:59";
		from += " 00:00:00";
		StringBuilder sb = new StringBuilder();
		sb.append(" select m.id,m.inn_id");
		sb.append(" from tomato_sys_daily_task m");
		sb.append(" where m.func_item_type = ? AND m.recorded_at > '"+from+"' AND m.recorded_at < '"+to+"'");
		sb.append(" AND m.inn_id = ?");
		return this.findUniqueWithSql(sb.toString(), funcItemType, task.getInn().getId());
	}
	
}
