package com.project.dao.inn;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.inn.InnAdmin;
import com.project.utils.StringUtil;
import com.project.utils.encode.RandomUtil;
import java.math.BigInteger;
/**
 * 重置密码
 * @author 陈亚超
 * 
 */
@Component
public class InnAdminDao extends HibernateDao<InnAdmin, Long>  {

	// 重置密码时查询用户 
	public Page<InnAdmin> findPageByPar(Page<InnAdmin> page,String condition)  {
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct tia.* from tomato_inn_admin tia,tomato_inn tin where tia.inn_id = tin.id and status= ")
			.append(Constants.MEMBER_STATE_AUDITED);
		if(StringUtils.isNotBlank(condition)){	
			sql.append("and (tin.name like '%"+condition+"%' or tia.mobile like '%"+condition+"%') ");
			return this.findPageWithSql(page, sql.toString());
		}
		return this.findPageWithSql(page, sql.toString());
	}
	
	// 重置密码
	public String resetPassword(int id) {
		StringBuffer sql = new StringBuffer();
		InnAdmin admin = findById(id);
		String randomPwd = RandomUtil.getRandomNumber(6)+"";
		String password2 = StringUtil.encryptBySHA(randomPwd + admin.getSalt());
		sql.append("update tomato_inn_admin tia set password=? where id=?");
		super.executeUpdateWithSql(sql.toString(),new Object[]{password2,id});
		return randomPwd+"";
	}
	
	//重置密码时查找盐值
	public InnAdmin findById(int id) {
		StringBuffer sql = new StringBuffer("select distinct tia.* from tomato_inn_admin tia where id = ?");
		return super.findUniqueWithSql(sql.toString(),id);
	}
	
	// 审核查询申请客栈数据 
	public Page<InnAdmin> finder(Page<InnAdmin> page) {
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct tia.* from tomato_inn_admin tia,tomato_inn tin where tia.inn_id = tin.id and status= ")
		.append(Constants.MEMBER_STATE_CHECKPENDING);
		return this.findPageWithSql(page, sql.toString());
	}
	
	// 审核通过
	public void update(String id, String status, String sysUserCode) {
		int id2 = Integer.parseInt(id);
		int st = Integer.parseInt(status);
		StringBuffer sql = new StringBuffer();		
		sql.append("update tomato_inn_admin tia set status=? where id=?");
		super.executeUpdateWithSql(sql.toString(),new Object[]{st,id2});
		InnAdmin admin = super.findUniqueBy("id", Integer.parseInt(id));
		String sqls = "update tomato_inn set auditor = ? where id = ?";
		super.executeUpdateWithSql(sqls,new Object[]{sysUserCode, admin.getInn().getId()});
	}

	public int getInnAmount() {
		StringBuffer sql = new StringBuffer();		
		sql.append("select COUNT(i.id) AS num from tomato_inn i");
		Map<String,Object> row = this.findMapWithSql(sql.toString());
		BigInteger num = (BigInteger) row.get("num");
		return (num == null)?0:num.intValue();
	}

	public int getAdminAmount(Integer innId) {
		StringBuffer sql = new StringBuffer();		
		sql.append(" select COUNT(i.id) AS num from tomato_inn_admin i");
		if(innId != null){
			sql.append(" where i.inn_id = ?");
		}
		Map<String,Object> row = new HashMap<>();
		if(innId != null){
			row = this.findMapWithSql(sql.toString(), innId);
		}else{
			row = this.findMapWithSql(sql.toString());
		}
		BigInteger num = (BigInteger)row.get("num");
		return (num == null)?0:num.intValue();
	}
	
}
