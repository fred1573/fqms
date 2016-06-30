/**
* @Title: HotelReviewManager.java
* @Package com.project.service.audit
* @Description: 
* @author Administrator
* @date 2014年3月27日 上午11:57:56
*/

/**
 * 
 */
package com.project.service.inn;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.dao.inn.InnTaskDao;
import com.project.entity.inn.InnTask;

/**
 * @author cyc
 *
 */
@Component
@Transactional
public class InnTaskManager {

	@Autowired
	private InnTaskDao innTaskDao;
	
	public void save(InnTask task){
		if(this.findByTypeAndDate(task.getRecordedAt(), task.getFuncItemType(), task) == null){
			innTaskDao.save(task);
		}
	}
	
	public InnTask findByTypeAndDate(Date time, Integer funcItemType, InnTask task){
		return innTaskDao.findByTypeAndDate(time, funcItemType, task);
	}

}
