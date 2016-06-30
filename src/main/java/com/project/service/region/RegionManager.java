/**
* @Title: RegionService.java
* @Package com.project.service.region
* @Description: 
* @author Administrator
* @date 2014骞�鏈�1鏃�涓嬪崍6:05:23
*/

/**
 * 
 */
package com.project.service.region;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.core.orm.Page;
import com.project.dao.inn.InnDao;
import com.project.dao.region.AdminDao;
import com.project.dao.region.InnAreaDao;
import com.project.dao.region.RegionDao;
import com.project.entity.area.InAreaPage;
import com.project.entity.inn.InnAdmin;

/**
 * 地区后台
 * @author cyc
 *
 */
@Component
@Transactional
public class RegionManager  {
	@Autowired
	private RegionDao regionDao;
	@Autowired
	private InnAreaDao areaDao;
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private InnDao innDao;
	
	/**
	 * 查询该地区客栈分页
	 * @param userS 
	 * 
	 * @param region 
	 */
	public Page<InAreaPage> selectList(Page<InAreaPage> page, String condition,String status,String useStatus)  {
		
		return innDao.select(page,condition,status,useStatus);
	}

	/**
	 * 该地区有多少家客栈
	 *
	 * @return
	 */
	public Long selectTotalCount(int id) {
		return regionDao.selectTotalCount(id);
	}

	/**
	 * 查询有多少家客栈3天未操作
	 * 
	 * @return
	 */
	
	public Long selectCount(int id) {	
		return regionDao.selectCount(id);
	}

	/**
	 * 查询所属地区的ID
	 * 
	 * @return
	 */
	
	public int selectId(String status) {
		return regionDao.selectId(status);
	}

	/**
	 * 查询电话号码的店家ID
	 * 
	 * @param status 
	 * @throws SQLException 
	 * 
	 */
	
	public int selectPhone(int id, String status) {
		return areaDao.selectPhone(id,status);
	}
	
	/**
	 * 添加客栈信息
	 *
	 * @param status 
	 * @throws SQLException 
	 */
	
	public void addInn(int i, String status) throws SQLException {
		areaDao.addInn(i,status);
	}
	
	/**
	 * 删除客栈信息
	 * @param status
	 * @param status2 
	 */
	
	public void delete(String id, String status) {
		areaDao.delete(id,status);
	}

	/**
	 * @param phone
	 * @return
	 */
	public InnAdmin selectInnId(String phone) {
		return adminDao.selectInnId(phone);
	}
}
