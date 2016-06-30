package com.project.dao.wg;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.wg.WgRoomType;

/**
 * @author 
 * X
 */
@Component
public class WgRoomTypeDao extends HibernateDao<WgRoomType, Long> {
	
	public List<WgRoomType> getAll(){
		return super.getAll();
	}
	
	@Cacheable(value="wgRoomTypeCache", key="'_wg_room_type_'+#innId")
	public List<WgRoomType> getByInnId(int innId){
		StringBuilder sb = new StringBuilder();
		sb.append("select m.* from wg_room_type m ");
		sb.append("inner join wg_account c ON c.id = m.account_id and c.inn_id = ? and c.ota_id = 101");
		return this.findWithSql(sb.toString(), innId);
	}
	
}
