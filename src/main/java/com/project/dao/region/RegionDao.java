/**
* @author cyc
* @date 2014骞�鏈�1鏃�涓嬪崍6:02:16
*/
package com.project.dao.region;

import org.hibernate.Query;
import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.inn.InnRegion;

/**
 * 地区后台
 * @author cyc
 *
 */
@Component
public class RegionDao extends HibernateDao<InnRegion, Long> {



	/**
	 * 查询总共有多少家客栈在使用这个系统
	 * @param id 
	 * @return
	 */
	public Long selectTotalCount(int id) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id from tomato_inn_area_region where region_id = ").append(id);
		return this.countForLongWithSql(sql.toString());
	}

	/**
	 * 查询有多少家客栈3天未用该客栈
	 * @param id 
	 * @return
	 */
	public Long selectCount(int id) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id from tomato_inn_area_region iar where iar.region_id =").append(id)
			.append(" and NOT EXISTS ( select inn_id from tomato_inn_admin a where a.inn_id = iar.inn_id and a.parent_id is null")
			.append(" and a.last_logined_at > to_char(to_date(to_char(now(),'yyyy-mm-dd'),'YYYY-MM-dd')-3,'YYYY-MM-dd') )");
		return this.countForLongWithSql(sql.toString());
	}

	/**
	 * 查询地区ID
	 * @param status 
	 * @return
	 */
	public int selectId(String status) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from tomato_inn_region where name='").append(status+"'");	
		Query query = createSqlQuery(sql.toString());
		InnRegion region = null;
		if(query.list().size()!=0){
			region = (InnRegion)query.list().get(0);
			return region.getId();
		}
		return 0;
	}

	public InnRegion get(Integer id){
		return (InnRegion) getSession().get(InnRegion.class, id);
	}
	

}
