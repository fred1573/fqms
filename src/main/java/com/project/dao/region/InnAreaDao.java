/**
* 
* @author cyc
* @date 2014年4月3日 上午10:40:33
*/

/**
 * 
 */
package com.project.dao.region;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.area.InnArea;
import com.project.entity.inn.Inn;
import com.project.entity.inn.InnRegion;


/**
 * @author 用于对Inn_Area_region操作
 *
 */
@Component
public class InnAreaDao extends HibernateDao<InnArea, Long>  {

	@Autowired
	private RegionDao regionDao;
	@Autowired
	private AdminDao adminDao;
	//jdbc 工具类
	
	// 查询该客栈是否已存在该地区
	 public int selectPhone(int id, String status) {
		StringBuffer sql = new StringBuffer();
		int statuId = regionDao.selectId(status);
		sql.append("select * from tomato_inn_area_region where inn_id=").append(id).append(" and region_id= ").append(statuId);
		List<InnArea> area = this.findWithSql(sql.toString());
		if(area.size()==0) {return 1;}
		return 0;
	}
	 /**
	  * 添加地区客栈
	  * @param i
	 * @param status 
	 * @throws SQLException 
	  */
	public void addInn(int i, String status) throws SQLException  {

		StringBuffer sql = new StringBuffer();
		int id = regionDao.selectId(status); 
		InnArea area = new InnArea();
		Inn in = new Inn();
		in.setId(i);
		InnRegion region2 = new InnRegion();
		region2.setId(id);
		area.setInn(in);
		area.setRegion(region2);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		String time =  format.format(new Date());
		Date currentTime = null;
		try {
			currentTime = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		area.setCreatetime(currentTime);
		save(area);
	}
	/**
	 * 删出该地区的客栈
	 * @param status 
	 * @param id2 
	 */
	public void delete(String id, String status)  {
		//StringBuffer sql = new StringBuffer();
		//sql.append("delete from tomato_inn_area_region where id = ").append(id);
		StringBuffer sql = new StringBuffer();
		int statusId = regionDao.selectId(status);
		sql.append("select * from tomato_inn_area_region where inn_id=").append(id).append(" and region_id=").append(statusId);
		List<InnArea> list = this.findWithSql(sql.toString());
		
		
		if(list.size()!=0) {
	 		InnArea area = new InnArea();
	 		Long regionId = list.get(0).getId();
			area.setId(regionId);
			delete(regionId);
		}
		//用delete方法
	}
}
