/**
* @Title: AdminDao.java
* @Package com.project.dao.region
* @Description: 
* @author Administrator
* @date 2014年4月3日 下午12:58:41
*/

/**
 * 
 */
package com.project.dao.region;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.area.InnArea;
import com.project.entity.inn.InnAdmin;

/**
 * @author Administrator
 *
 */
@Component
public class AdminDao extends HibernateDao<InnAdmin, Long> {
	@Autowired
	private RegionDao regionDao;
	
	// 查询是否注册客栈
	public Query getQuery(String status,String phone){
		StringBuffer sql2 = new StringBuffer();
		int id = regionDao.selectId(status); 
		sql2.append("select a.* from tomato_inn i,tomato_inn_admin a where i.id=a.id and a.status=2 and mobile='").append(phone).append("'");
		Query query = createSqlQuery(sql2.toString());
		return query;
	}
	
	// (0代表未注册)
	// 查询是否注册客栈查询电话号码是否是已经注册
	public InnAdmin selectInnId(String phone) {
		return this.findUniqueBy("mobile", phone);
	}
}
