package com.project.dao.inn;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.project.bean.report.InnReportBean;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;





import com.project.bean.report.XzReportBean;
import com.project.common.Constants;
import com.google.common.collect.Lists;
import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.inn.InnFuncReport;
import com.project.entity.plug.InnPlugFunc;
import com.project.utils.NumberUtil;
import com.project.utils.time.DateUtil;
/**
 * 
 * @author X
 * 
 */
@Component
public class InnFuncReportDao extends HibernateDao<InnFuncReport, Long>  {
	
	public void save(List<InnFuncReport> reports){
		for (InnFuncReport r : reports) {
			super.save(r);
		}
	}
	
	public void save(InnFuncReport report){
		super.save(report);
	}
	
	/**
	 * 获取功能库插件 每日开启状况
	 * @param plugFuncs
	 * @param recordAt
	 * @param innAmount
	 * @return
	 */
	public List<InnFuncReport> getPlugFuncsAmount(List<InnPlugFunc> plugFuncs, Date recordAt, Integer innAmount){
		List<InnFuncReport> funcReports = Lists.newArrayList();
		StringBuilder sb = new StringBuilder();
		appendSql4PlugFuncs(sb, plugFuncs);
		List<Map<String,Object>> rows = Lists.newArrayList();
		rows = this.findListMapWithSql(sb.toString());
		sqlRow2Obj(recordAt, innAmount, funcReports, sb, rows);
		return funcReports;
	}

	private void sqlRow2Obj(Date recordAt, Integer innAmount,
			List<InnFuncReport> funcReports, StringBuilder sb, List<Map<String, Object>> rows) {
		for (Map<String, Object> row : rows) {
			InnFuncReport tmp = new InnFuncReport();
			Integer id = (Integer) row.get("funcitem");
			tmp.setFuncItemType(id.intValue());
			BigInteger num = (BigInteger) row.get("num");
			tmp.setApplicationAmount(num.intValue());
			tmp.setRecordedAt(recordAt);
			Double innPercent = (double) (tmp.getApplicationAmount().doubleValue()/innAmount * 100);
			innPercent = NumberUtil.round(innPercent, Constants.REPORT_RATIO_ACCURATE_LENGTH);
			tmp.setInnPercent(innPercent);
			funcReports.add(tmp);
		}
	}
	
	/**
	 * 获取每日  日志操作中的 操作情况
	 * @param recordAt
	 * @param innAmount
	 * @param logType
	 * @return
	 */
	public InnFuncReport getLogAmount(Date recordAt, Integer innAmount, Integer logType, Integer reportItem){
		String from = DateUtil.format(recordAt);
		String to = from + " 23:59:59";
		from += " 00:00:00";
		InnFuncReport tmp = new InnFuncReport();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT distinct lg.inn_id AS innId");
		sb.append(" FROM tomato_inn_log lg");
		sb.append(" WHERE lg.operate_time > '"+from+"' AND lg.operate_time < '"+to+"'");
		if(logType != null){
			sb.append(" AND lg.log_type = ?");
		}
		String sql = sb.toString();
		sb = new StringBuilder();
		sb.append(" SELECT COUNT(*) AS num FROM(");
		sb.append(sql);
		sb.append(") t");
		Map<String,Object> row = this.findMapWithSql(sb.toString(), logType);
		if(row != null){
			BigInteger num = (BigInteger) row.get("num");
			tmp.setApplicationAmount(num.intValue());
		}else{
			tmp.setApplicationAmount(0);
		}
		//直接用日志类型的id 不再去重复新建
		tmp.setFuncItemType(reportItem);
		tmp.setRecordedAt(recordAt);
		Double innPercent = (double) (tmp.getApplicationAmount().doubleValue()/innAmount * 100);
		innPercent = NumberUtil.round(innPercent, Constants.REPORT_RATIO_ACCURATE_LENGTH);
		tmp.setInnPercent(innPercent);
		return tmp;
	}
	
	/**
	 * 获取每日 OTA打通使用的情况
	 * @param recordAt
	 * @param innAmount
	 * @param otaIds
	 * @return
	 */
	public List<InnFuncReport> getOtaAmount(Date recordAt, Integer innAmount, List<Integer> otaIds){
		List<InnFuncReport> funcReports = Lists.newArrayList();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COUNT(w.id) AS num");
		sb.append(" FROM wg_account w");
		sb.append(" WHERE w.ota_id = ?");
		String sql = sb.toString();
		for (Integer otaId : otaIds) {
			InnFuncReport tmp = new InnFuncReport();
			Map<String,Object> row = this.findMapWithSql(sql, otaId);
			if(row != null){
				BigInteger num = (BigInteger) row.get("num");
				tmp.setApplicationAmount(num.intValue());
			}else{
				tmp.setApplicationAmount(0);
			}
			//通过OTA的id来获取  对应的功能id
			tmp.setFuncItemType(otaId+1000);
			tmp.setRecordedAt(recordAt);
			Double innPercent = (double) (tmp.getApplicationAmount().doubleValue()/innAmount * 100);
			innPercent = NumberUtil.round(innPercent, Constants.REPORT_RATIO_ACCURATE_LENGTH);
			tmp.setInnPercent(innPercent);
			funcReports.add(tmp);
		}
		return funcReports;
	}
	
	/**
	 * 获取 每日房态风格使用情况
	 * @param recordAt
	 * @param userAmount
	 * @return
	 */
	public List<InnFuncReport> getRoomStyleAmount(Date recordAt, Integer userAmount){
		List<InnFuncReport> funcReports = Lists.newArrayList();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COUNT(ad.id) AS num,2001 AS funcItem");
		sb.append(" FROM tomato_inn_admin ad");
		sb.append(" WHERE ad.room_status_style = ?");
		sb.append(" UNION ALL");
		sb.append(" SELECT COUNT(ad.id) AS num,2002 AS funcItem");
		sb.append(" FROM tomato_inn_admin ad");
		sb.append(" WHERE ad.room_status_style = ?");
		sb.append(" UNION ALL");
		sb.append(" SELECT COUNT(ad.id) AS num,2004 AS funcItem");
		sb.append(" FROM tomato_inn_admin ad");
		sb.append(" WHERE ad.room_status_style = ?");
		sb.append(" GROUP BY funcItem");
		List<Map<String,Object>> rows = Lists.newArrayList();
		rows = this.findListMapWithSql(sb.toString(), Constants.ROOM_STATUS_STYLE_DEFAULT
				, Constants.ROOM_STATUS_STYLE_XY, Constants.ROOM_STATUS_STYLE_EXCEL);
		sqlRow2Obj(recordAt, userAmount, funcReports, sb, rows);
		return funcReports;
	}
	
	/**
	 * 获取  每日活跃用户情况
	 * @param recordAt
	 * @param innAmount
	 * @return
	 */
	public InnFuncReport getActiveAmount(Date recordAt, Integer innAmount){
		String from = DateUtil.format(recordAt);
		String to = from + " 23:59:59";
		from += " 00:00:00";
		from = DateUtil.format(DateUtil.addDay(DateUtil.parse(from), -7), DateUtil.FORMAT_DATE_STR_SECOND);
		InnFuncReport tmp = new InnFuncReport();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT distinct lg.inn_id AS innId");
		sb.append(" FROM tomato_inn_log lg");
		sb.append(" WHERE lg.operate_time > '"+from+"' AND lg.operate_time < '"+to+"'");
		
		String sql = sb.toString();
		sb = new StringBuilder();
		sb.append(" SELECT COUNT(*) AS num FROM(");
		sb.append(sql);
		sb.append(") t");
		Map<String,Object> row = this.findMapWithSql(sb.toString());
		if(row != null){
			BigInteger num = (BigInteger) row.get("num");
			tmp.setApplicationAmount(num.intValue());
		}else{
			tmp.setApplicationAmount(0);
		}
		tmp.setFuncItemType(Constants.REPORT_ITEM_TYPE_ACTIVE);
		tmp.setRecordedAt(recordAt);
		Double innPercent = (double) (tmp.getApplicationAmount().doubleValue()/innAmount * 100);
		innPercent = NumberUtil.round(innPercent, Constants.REPORT_RATIO_ACCURATE_LENGTH);
		tmp.setInnPercent(innPercent);
		return tmp;
	}
	
	/**
	 * 获取 连锁客栈情况
	 * @param recordAt
	 * @param innAmount
	 * @return
	 */
	public InnFuncReport getChainStoreAmount(Date recordAt, Integer innAmount){
		InnFuncReport tmp = new InnFuncReport();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT SUM(oo.num) AS num");
		sb.append(" FROM (");
		sb.append(" SELECT COUNT(m.id) AS num,m.admin_id AS admin");
		sb.append(" FROM tomato_inn_admin_inn_role m");
		sb.append(" WHERE m.role_id = 1");
		sb.append(" GROUP BY m.admin_id");
		sb.append(" ) oo");
		sb.append(" WHERE oo.num > 1");
		Map<String,Object> row = this.findMapWithSql(sb.toString());
		if(row != null){
			BigDecimal num = (BigDecimal) row.get("num");
			tmp.setApplicationAmount(num.intValue());
		}else{
			tmp.setApplicationAmount(0);
		}
		tmp.setFuncItemType(Constants.REPORT_ITEM_TYPE_LINK_INN);
		tmp.setRecordedAt(recordAt);
		Double innPercent = (double) (tmp.getApplicationAmount().doubleValue()/innAmount * 100);
		innPercent = NumberUtil.round(innPercent, Constants.REPORT_RATIO_ACCURATE_LENGTH);
		tmp.setInnPercent(innPercent);
		return tmp;
	}
	
	/**
	 * 获取 锁屏使用情况
	 * @param recordAt
	 * @param innAmount
	 * @return
	 */
	public InnFuncReport getLockScreenAmount(Date recordAt, Integer innAmount){
		String from = DateUtil.format(recordAt);
		String to = from + " 23:59:59";
		from += " 00:00:00";
		from = DateUtil.format(DateUtil.addDay(DateUtil.parse(from), -7), DateUtil.FORMAT_DATE_STR_SECOND);
		InnFuncReport tmp = new InnFuncReport();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COUNT(m.id) AS num");
		sb.append(" FROM tomato_sys_daily_task m");
		sb.append(" WHERE m.recorded_at > '"+from+"' AND m.recorded_at < '"+to+"'");
		sb.append(" AND m.func_item_type = ?");
		Map<String,Object> row = this.findMapWithSql(sb.toString(), Constants.REPORT_ITEM_TYPE_LOCK);
		BigInteger num = (BigInteger) row.get("num");
		tmp.setApplicationAmount(num.intValue());
		tmp.setFuncItemType(Constants.REPORT_ITEM_TYPE_LOCK);
		tmp.setRecordedAt(recordAt);
		Double innPercent = (double) (tmp.getApplicationAmount().doubleValue()/innAmount * 100);
		innPercent = NumberUtil.round(innPercent, Constants.REPORT_RATIO_ACCURATE_LENGTH);
		tmp.setInnPercent(innPercent);
		return tmp;
	}
	
	/**
	 * 获取 房型排序使用情况
	 * @param recordAt
	 * @param innAmount
	 * @return
	 */
	public InnFuncReport getRoomSortAmount(Date recordAt, Integer innAmount){
		String from = DateUtil.format(recordAt);
		String to = from + " 23:59:59";
		from += " 00:00:00";
		from = DateUtil.format(DateUtil.addDay(DateUtil.parse(from), -7), DateUtil.FORMAT_DATE_STR_SECOND);
		InnFuncReport tmp = new InnFuncReport();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COUNT(m.id) AS num");
		sb.append(" FROM tomato_sys_daily_task m");
		sb.append(" WHERE m.recorded_at > '"+from+"' AND m.recorded_at < '"+to+"'");
		sb.append(" AND m.func_item_type = ?");
		Map<String,Object> row = this.findMapWithSql(sb.toString(), Constants.REPORT_ITEM_TYPE_ROOM_SORT);
		BigInteger num = (BigInteger) row.get("num");
		tmp.setApplicationAmount(num.intValue());
		tmp.setFuncItemType(Constants.REPORT_ITEM_TYPE_ROOM_SORT);
		tmp.setRecordedAt(recordAt);
		Double innPercent = (double) (tmp.getApplicationAmount().doubleValue()/innAmount * 100);
		innPercent = NumberUtil.round(innPercent, Constants.REPORT_RATIO_ACCURATE_LENGTH);
		tmp.setInnPercent(innPercent);
		return tmp;
	}
	
	private void appendSql4PlugFuncs(StringBuilder sb, List<InnPlugFunc> plugFuncs){
		sb.append(" SELECT COUNT(if.id) AS num,");
		sb.append(" if.func_id AS funcItem");
		sb.append(" FROM tomato_inn_func if");
		sb.append(" GROUP BY funcItem");
		sb.append(" ORDER BY funcItem asc");
	}

	@SuppressWarnings("unchecked")
	public List<InnFuncReport> getByDate(String from, String to) {
		from += " 00:00:00";
		to += " 23:59:59";
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from tomato_sys_func_report");
		sb.append(" where recorded_at >= '"+from+"' and recorded_at <= '"+to+"'");
		sb.append(" order by recorded_at asc");
		Page<InnFuncReport> page = new Page<InnFuncReport>(5000);
		this.findPageWithSql(page, sb.toString());
		return (List<InnFuncReport>) page.getResult();
	}

	public Page<XzReportBean> getFQXZPage(int pageNo, String date, Page<XzReportBean> page
			, String input) {
		StringBuilder sb = new StringBuilder();
		sb.append("select i.name AS innName,ad.me_chat_code AS openMC,m.created_at AS createdAt,");
		sb.append("s.wei_address AS webSite ");
		sb.append("from wg_account m ");
		sb.append("inner join tomato_inn i ON i.id = m.inn_id ");
		sb.append("LEFT join tomato_inn_admin ad ON ad.mobile = m.name ");
		sb.append("LEFT join wg_inn_wei_shop s ON s.inn_id = m.inn_id ");
		sb.append("where m.ota_id = 101 and m.created_at <= '"+date+"' ");
		if(StringUtils.isNotBlank(input)){
			sb.append("and i.name like '%"+input+"%' ");
		}
		sb.append("order by m.created_at desc ");
		String sql = sb.toString();
		setTotalCount(sql, page);
		//获取分页实体
		setPageResult(sql, pageNo, page);
		sb = null;
		return page;
	}
	
	public Page<XzReportBean> getFQXZPageOpenMc(int pageNo, String date, Page<XzReportBean> page
			, String input) {

		StringBuilder sb = new StringBuilder();
		sb.append("select i.name AS innName,ad.me_chat_code AS openMC,m.created_at AS createdAt,");
		sb.append(" s.wei_address AS webSite ");
		sb.append("from wg_account m ");
		sb.append("inner join tomato_inn i ON i.id = m.inn_id ");
		sb.append("inner join tomato_inn_admin ad ON ad.mobile = m.name ");
		sb.append(" and ad.me_chat_code is not null and ad.me_chat_code != '' ");
		sb.append("LEFT join wg_inn_wei_shop s ON s.inn_id = m.inn_id ");
		sb.append("where m.ota_id = 101 and m.created_at <= '"+date+"' ");
		if(StringUtils.isNotBlank(input)){
			sb.append("and i.name like '%"+input+"%' ");
		}
		sb.append("order by m.created_at desc ");
		String sql = sb.toString();
		setTotalCount(sql, page);
		sb = null;
		return page;
	}
	
	public Page<XzReportBean> getStampPage(int pageNo, String date,
			Page<XzReportBean> page) {
		StringBuilder sb = new StringBuilder();
		sb.append("select i.name AS innName,m.pay_at AS createdAt,m.pay_user AS alipayUnit ");
		sb.append("from tomato_pay_record m ");
		sb.append("inner join tomato_inn i ON i.id = m.inn_id ");
		sb.append("where m.product_code = 'printer_common'");
		sb.append("order by m.pay_at desc ");
		String sql = sb.toString();
		setTotalCount(sql, page);
		//获取分页实体
		setPageResult(sql, pageNo, page);
		sb = null;
		return page;
	}
	
//	private void setTotalCount(String sql, Page<?> page){
//		StringBuilder sb = new StringBuilder();
//		sb.append(" SELECT COUNT(*) AS num FROM(");
//		sb.append(sql);
//		sb.append(") t");
//		Map<String,Object> row = this.findMapWithSql(sb.toString());
//		if(row != null){
//			BigInteger num = (BigInteger) row.get("num");
//			page.setTotalCount(num.longValue());
//		}else{
//			page.setTotalCount(0);
//		}
//		sb = null;
//	}
//	
//	private void setPageResult(String sql, int pageNo, Page<XzReportBean> page){
//		StringBuilder sb = new StringBuilder();
//		int pageSize = page.getPageSize();
//		int offset = pageSize * (pageNo - 1);
//		sb = new StringBuilder();
//		sb.append(sql);
//		sb.append(" LIMIT " + page.getPageSize() + " OFFSET " + offset);
//		List<Map<String,Object>> rows = this.findListMapWithSql(sb.toString());
//		page.setResult(rows2Obj(rows));
//		page.setPageNo(pageNo);
//		sb = null;
//	}
	
	public List<XzReportBean> rows2Obj(List<Map<String,Object>> rows){
		List<XzReportBean> list = Lists.newArrayList();
		for (Map<String, Object> map : rows) {
			XzReportBean tmp = new XzReportBean();
			tmp.setInnName((String)map.get("innname"));
			tmp.setCreatedAt((Date)map.get("createdat"));
			tmp.setWebSite((String)map.get("website"));
			tmp.setAlipayUnit((String)map.get("alipayunit"));
			tmp.setOpenMC((String)map.get("openmc"));
			list.add(tmp);
		}
		return list;
	}


	public List<InnReportBean> getPagingInn(int pageNo, int pageSize, String innName, boolean xzOnly) {
		String s="select i.id, i.name AS inn_name,s.inn_name xz_name,ad.me_chat_code AS open_mc,s.wei_address AS web_site,i.has_brand from tomato_inn i \n" +
				"LEFT join tomato_inn_admin ad ON ad.inn_id = i.id and ad.parent_id is null\n" +
				"LEFT JOIN wg_inn_wei_shop s on s.inn_id = i.id \n" +
				"left JOIN wg_account m on i.id=m.inn_id and m.ota_id=101  where  1=1 ";

		if (xzOnly){
			s+=" and s.id is not null";
		}
		boolean hasInnName = org.springframework.util.StringUtils.hasText(innName);
		if (hasInnName){
			s+=" and i.name like :innName";
		}
		SQLQuery sqlQuery = getSession().createSQLQuery(s);
		sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if (hasInnName){
			sqlQuery.setParameter("innName", "%" + innName + "%");
		}
		sqlQuery.setFirstResult((pageNo-1)*pageSize);
		sqlQuery.setMaxResults(pageSize);
		List<Map<String,Object>> list = sqlQuery.list();
		List<InnReportBean> inns = Lists.newArrayListWithCapacity(list.size());
		for (Map<String, Object> som : list) {
			InnReportBean irb = new InnReportBean();
			irb.setId((Integer)som.get("id"));
			irb.setInnName((String) som.get("inn_name"));
			irb.setHasBrand((boolean)som.get("has_brand"));
			irb.setOpenMc((String)som.get("open_mc"));
			irb.setXzName((String)som.get("xz_name"));
			irb.setWebSite((String)som.get("web_site"));
			inns.add(irb);
		}
		return inns;
	}

	public long countInnReport(String innName, boolean xzOnly) {
		String s="select count(1) from tomato_inn i \n" +
				"LEFT join tomato_inn_admin ad ON ad.inn_id = i.id and ad.parent_id is null\n" +
				"LEFT JOIN wg_inn_wei_shop s on s.inn_id = i.id \n" +
				"left JOIN wg_account m on i.id=m.inn_id and m.ota_id=101  where  1=1 ";
		if (xzOnly){
			s+=" and s.id is not null";
		}
		boolean hasInnName = org.springframework.util.StringUtils.hasText(innName);
		if (hasInnName){
			s+=" and i.name like :innName";
		}
		SQLQuery sqlQuery = getSession().createSQLQuery(s);
//		sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

		if (hasInnName) {
			sqlQuery.setParameter("innName","%"+innName+"%");
		}
		Object o = sqlQuery.uniqueResult();
		return ((BigInteger)o).longValue();
	}
}
