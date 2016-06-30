package com.project.dao.inn;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.project.bean.SearchInnBean;
import com.project.bean.bo.RegionRadioBo;
import com.project.bean.report.ActiveSearchBean;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.dao.region.RegionDao;
import com.project.dao.report.RegionCountDao;
import com.project.entity.area.InAreaPage;
import com.project.entity.inn.Inn;
import com.project.entity.inn.InnRegion;
import com.project.entity.report.RegionCount;
import com.project.utils.ListUtil;
import com.project.utils.NumberUtil;
import com.project.utils.time.DateUtil;

/**
 * @author 
 * mowei
 */
@Component
public class InnDao extends HibernateDao<Inn, Long>  {
	@Autowired
	private RegionDao regionDao;
	@Autowired
	private RegionCountDao regionCountDao;
	/**
	 * 查询活跃数据
	 */
	@Transactional(readOnly = true)
	public List<Map<String,Object>> statisticsActive(ActiveSearchBean activeSearchBean)  {
		StringBuilder sb = new StringBuilder();
		sb.append("select sum(l_num) as login_num,sum(o_num) as book_num,sum(c_num) as check_num,inn_id,created_time from ( ");
		sb.append("(select count(l.id) as l_num,0 as o_num,0 as c_num,l.inn_id,to_char(l.operate_time,'yyyy-MM-dd') as created_time from tomato_inn_log l ");
		sb.append("where to_char(l.operate_time,'yyyy-MM-dd') >= '").append(activeSearchBean.getFromDate()).append("' ");
		sb.append("and to_char(l.operate_time,'yyyy-MM-dd') <= '").append(activeSearchBean.getToDate()).append("' ");
		sb.append("and l.inn_id in (").append(activeSearchBean.getInnIds()).append(") ");
		sb.append("and l.log_type = 0 ");
		sb.append("group by l.inn_id,created_time ) ");
		sb.append("union ");
		sb.append("(select 0 as l_num,count(mo.id) as o_num,0 as c_num,i.id as inn_id,to_char(mo.ordered_at,'yyyy-MM-dd') as created_time from tomato_inn i ");
		sb.append("inner join tomato_inn_admin a on a.inn_id = i.id ");
		sb.append("inner join tomato_inn_room r on r.inn_id = i.id ");
		sb.append("inner join tomato_inn_room_order o on r.id = o.room_id ");
		sb.append("inner join tomato_inn_room_main_order mo on mo.id = o.main_id ");
		sb.append("where to_char(mo.ordered_at,'yyyy-MM-dd') >= '").append(activeSearchBean.getFromDate()).append("' ");
		sb.append("and to_char(mo.ordered_at,'yyyy-MM-dd') <= '").append(activeSearchBean.getToDate()).append("' ");
		sb.append("and i.id in (").append(activeSearchBean.getInnIds()).append(") ");
		sb.append("and a.parent_id is null ");
		sb.append("group by i.id,created_time ) ");
		sb.append("union ");
		sb.append("(select 0 as l_num,0 as o_num,count(ci.id) as c_num,i.id as inn_id,to_char(ci.created_at,'yyyy-MM-dd') as created_time from tomato_inn i ");
		sb.append("inner join tomato_inn_admin a on a.inn_id = i.id ");
		sb.append("inner join tomato_inn_room r on r.inn_id = i.id ");
		sb.append("inner join tomato_inn_room_check_in c on r.id = c.room_id ");
		sb.append("inner join tomato_inn_room_main_check_in ci on ci.id = c.main_id ");
		sb.append("where to_char(ci.created_at,'yyyy-MM-dd') >= '").append(activeSearchBean.getFromDate()).append("' ");
		sb.append("and to_char(ci.created_at,'yyyy-MM-dd') <= '").append(activeSearchBean.getToDate()).append("' ");
		sb.append("and i.id in (").append(activeSearchBean.getInnIds()).append(") ");
		sb.append("and a.parent_id is null ");
		sb.append("group by i.id,created_time ) ");
		sb.append(") as count_all ");
		sb.append("group by inn_id,created_time ");
		return this.findListMapWithSql(sb.toString());
	}
	
	@Transactional(readOnly = true)
	public List<Map<String,Object>> statisticsActives(ActiveSearchBean activeSearchBean)  {
		StringBuilder sb = new StringBuilder();
		String fromDate = activeSearchBean.getFromDate() + " 00:00:00";
		String toDate = activeSearchBean.getToDate() + " 23:59:59";
		StringBuilder sb_login = new StringBuilder();
		StringBuilder sb_book = new StringBuilder();
		StringBuilder sb_check = new StringBuilder();
		sb_login.append("(select count(distinct l.id) as l_num,0 as o_num,0 as c_num,l.inn_id,to_char(l.operate_time,'yyyy-MM-dd') as created_time from tomato_inn_log l ");
		sb_login.append("where l.operate_time >= '").append(fromDate).append("' ");
		sb_login.append("and l.operate_time <= '").append(toDate).append("' ");
		if(activeSearchBean.isPage()){
			sb_login.append("and l.inn_id in (").append(activeSearchBean.getInnIds()).append(") ");
		}
		sb_login.append("and l.log_type = 0 ");
		sb_login.append("group by l.inn_id,created_time ) ");
		
		sb_book.append("(select 0 as l_num,count(distinct mo.id) as o_num,0 as c_num,i.id as inn_id,to_char(mo.ordered_at,'yyyy-MM-dd') as created_time from tomato_inn i ");
		sb_book.append("inner join tomato_inn_room r on r.inn_id = i.id ");
		sb_book.append("inner join tomato_inn_room_order o on r.id = o.room_id ");
		sb_book.append("inner join tomato_inn_room_main_order mo on mo.id = o.main_id ");
		sb_book.append("where mo.ordered_at >= '").append(fromDate).append("' ");
		sb_book.append("and mo.ordered_at <= '").append(toDate).append("' ");
		if(activeSearchBean.isPage()){
			sb_book.append("and i.id in (").append(activeSearchBean.getInnIds()).append(") ");
		}
		sb_book.append("group by i.id,created_time ) ");
		
		sb_check.append("(select 0 as l_num,0 as o_num,count(distinct ci.id) as c_num,i.id as inn_id,to_char(ci.created_at,'yyyy-MM-dd') as created_time from tomato_inn i ");
		sb_check.append("inner join tomato_inn_room r on r.inn_id = i.id ");
		sb_check.append("inner join tomato_inn_room_check_in c on r.id = c.room_id ");
		sb_check.append("inner join tomato_inn_room_main_check_in ci on ci.id = c.main_id ");
		sb_check.append("where ci.created_at >= '").append(fromDate).append("' ");
		sb_check.append("and ci.created_at <= '").append(toDate).append("' ");
		if(activeSearchBean.isPage()){
			sb_check.append("and i.id in (").append(activeSearchBean.getInnIds()).append(") ");
		}
		sb_check.append("group by i.id,created_time ) ");
		
		sb.append("select sum(l_num) as login_num,sum(o_num) as book_num,sum(c_num) as check_num,inn_id,created_time from ( ");
		switch (activeSearchBean.getActiveType())  {
//		case ActiveSearchBean.ACTIVE_TYPE_LOGIN:
//			sb.append(sb_login.toString());
//			break;
		case ActiveSearchBean.ACTIVE_TYPE_BOOK:
			sb.append(sb_book.toString());
			break;
		case ActiveSearchBean.ACTIVE_TYPE_CHECK:
			sb.append(sb_check.toString());
			break;
		default:
			sb.append(sb_book.toString());
			sb.append(" union ");
			sb.append(sb_check.toString());
			break;
		}
		sb.append(") as count_all ");
		sb.append("group by inn_id,created_time ");
		return this.findListMapWithSql(sb.toString());
	}
	
	/**
	 * 查询一定日期段内的所有活跃/不活跃用户
	 */
	@Transactional(readOnly = true)
	public Page<Inn> getInnByDateRange(Page<Inn> page, ActiveSearchBean activeSearchBean)  {
		StringBuilder sb = new StringBuilder();
		StringBuilder sb_login = new StringBuilder();
		StringBuilder sb_book = new StringBuilder();
		StringBuilder sb_check = new StringBuilder();
		String fromDate = activeSearchBean.getFromDate() + " 00:00:00";
		String toDate = activeSearchBean.getToDate() + " 23:59:59";
		String fromSortDate = activeSearchBean.getSortDate() + " 00:00:00";
		String toSortDate = activeSearchBean.getSortDate() + " 23:59:59";
		
		sb_login.append("select l.inn_id from tomato_inn_log l ");
		sb_login.append("where l.operate_time >= '").append(fromDate).append("' ");
		sb_login.append("and l.operate_time <= '").append(toDate).append("' ");
		sb_login.append("and l.log_type = 0 and l.inn_id = i.id");
		
		sb_book.append("select i.id as inn_id from tomato_inn_room_main_order mo ");
		sb_book.append("inner join tomato_inn_room_order o on mo.id = o.main_id ");
		sb_book.append("inner join tomato_inn_room r on r.id = o.room_id and r.inn_id = i.id ");
		sb_book.append("where mo.ordered_at >= '").append(fromDate).append("' ");
		sb_book.append("and mo.ordered_at <= '").append(toDate).append("' ");
		
		sb_check.append("select i.id as inn_id from tomato_inn_room_main_check_in ci ");
		sb_check.append("inner join tomato_inn_room_check_in c on ci.id = c.main_id ");
		sb_check.append("inner join tomato_inn_room r on r.id = c.room_id  and r.inn_id = i.id ");
		sb_check.append("where ci.created_at >= '").append(fromDate).append("' ");
		sb_check.append("and ci.created_at <= '").append(toDate).append("' ");
		
		
		sb.append("select i.* from tomato_inn i ");
//		sb.append("left join tomato_inn_room r on r.inn_id = i.id ");
//		sb.append("left join tomato_inn_room_check_in c on r.id = c.room_id ");
//		sb.append("left join tomato_inn_room_main_check_in ci on ci.id = c.main_id ");
//		sb.append("and ci.created_at >= '").append(fromSortDate).append("' ");
//		sb.append("and ci.created_at <= '").append(toSortDate).append("' ");
		sb.append("where ");
		switch (activeSearchBean.getActiveType())  {
		case ActiveSearchBean.ACTIVE_TYPE_LOGIN:
			sb.append("exists ( ").append(sb_login.toString()).append(") ");
			sb.append("and not exists ( ").append(sb_book.toString()).append(") ");
			sb.append("and not exists ( ").append(sb_check.toString()).append(") ");
			break;
		case ActiveSearchBean.ACTIVE_TYPE_BOOK:
			sb.append("exists ( ").append(sb_book.toString()).append(") ");
			sb.append("and not exists ( ").append(sb_check.toString()).append(") ");
			break;
		case ActiveSearchBean.ACTIVE_TYPE_CHECK:
			sb.append("exists ( ").append(sb_check.toString()).append(") ");
			break;
		}
		if(activeSearchBean.getAreaId()!=0) {
			sb.append("and i.region_id = ").append(activeSearchBean.getAreaId()).append(" ");
		}
//		sb.append("group by i.id ");
		sb.append("order by i.registered_at desc ");
		return this.findPageWithSql(activeSearchBean.isPage(), page, sb.toString());
	}
	
	@Transactional(readOnly = true)
	public Page<Inn> getInnByDateRanges(Page<Inn> page, ActiveSearchBean activeSearchBean)  {
		StringBuilder sb = new StringBuilder();
		StringBuilder sb_book = new StringBuilder();
		StringBuilder sb_check = new StringBuilder();
		String fromDate = activeSearchBean.getFromDate() + " 00:00:00";
		String toDate = activeSearchBean.getToDate() + " 23:59:59";
		String fromSortDate = activeSearchBean.getSortDate() + " 00:00:00";
		String toSortDate = activeSearchBean.getSortDate() + " 23:59:59";
		String sortDate = activeSearchBean.getSortDate();
		
		sb_book.append("select i.id as inn_id from tomato_inn_room_main_order mo ");
		sb_book.append("inner join tomato_inn_room_order o on mo.id = o.main_id ");
		sb_book.append("inner join tomato_inn_room r on r.id = o.room_id and r.inn_id = i.id ");
		sb_book.append("where mo.ordered_at >= '").append(fromDate).append("' ");
		sb_book.append("and mo.ordered_at <= '").append(toDate).append("' ");
		
		sb_check.append("select i.id as inn_id from tomato_inn_room_main_check_in ci ");
		sb_check.append("inner join tomato_inn_room_check_in c on ci.id = c.main_id ");
		sb_check.append("inner join tomato_inn_room r on r.id = c.room_id  and r.inn_id = i.id ");
		sb_check.append("where ci.created_at >= '").append(fromDate).append("' ");
		sb_check.append("and ci.created_at <= '").append(toDate).append("' ");
		
		sb.append("select sum(l_num) as login_num,sum(o_num) as book_num,sum(c_num) as check_num,inn.*,created_time from ( ");
		switch (activeSearchBean.getActiveType())  {
		case ActiveSearchBean.ACTIVE_TYPE_BOOK:
			sb.append("(select distinct 0 as l_num,case when mo.id is null then 0 else 1 end as o_num,0 as c_num,i.id as inn_id,case when mo.id is null then '"+sortDate+"' else to_char(mo.ordered_at,'yyyy-MM-dd') end as created_time,mo.id AS orderId from tomato_inn i ");
			sb.append("left join tomato_inn_room r on r.inn_id = i.id ");
			sb.append("left join tomato_inn_room_order o on r.id = o.room_id ");
			sb.append("left join tomato_inn_room_main_order mo on mo.id = o.main_id ");
			sb.append("and mo.ordered_at >= '").append(fromSortDate).append("' ");
			sb.append("and mo.ordered_at <= '").append(toSortDate).append("' ");
			if(activeSearchBean.getAreaId()!=0) {
				sb.append("where i.region_id = ").append(activeSearchBean.getAreaId()).append(" ");
			}
			sb.append(" ) ");
			break;
		case ActiveSearchBean.ACTIVE_TYPE_CHECK:
			sb.append("(select distinct 0 as l_num,0 as o_num,case when ci.id is null then 0 else 1 end as c_num,i.id as inn_id,case when ci.id is null then '"+sortDate+"' else to_char(ci.created_at,'yyyy-MM-dd') end as created_time,ci.id AS orderId from tomato_inn i ");
			sb.append("left join tomato_inn_room r on r.inn_id = i.id ");
			sb.append("left join tomato_inn_room_check_in c on r.id = c.room_id ");
			sb.append("left join tomato_inn_room_main_check_in ci on ci.id = c.main_id ");
			sb.append("and ci.created_at >= '").append(fromSortDate).append("' ");
			sb.append("and ci.created_at <= '").append(toSortDate).append("' ");
			if(activeSearchBean.getAreaId()!=0) {
				sb.append("where i.region_id = ").append(activeSearchBean.getAreaId()).append(" ");
			}
			sb.append(" ) ");
			break;
		case ActiveSearchBean.ACTIVE_TYPE_ALL:
			sb.append("(select distinct 0 as l_num,0 as o_num,case when ci.id is null then 0 else 1 end as c_num,i.id as inn_id,case when ci.id is null then '"+sortDate+"' else to_char(ci.created_at,'yyyy-MM-dd') end as created_time,ci.id AS orderId from tomato_inn i ");
			sb.append("left join tomato_inn_room r on r.inn_id = i.id ");
			sb.append("left join tomato_inn_room_check_in c on r.id = c.room_id ");
			sb.append("left join tomato_inn_room_main_check_in ci on ci.id = c.main_id ");
			sb.append("and ci.created_at >= '").append(fromSortDate).append("' ");
			sb.append("and ci.created_at <= '").append(toSortDate).append("' ");
			if(activeSearchBean.getAreaId()!=0) {
				sb.append("where i.region_id = ").append(activeSearchBean.getAreaId()).append(" ");
			}
			sb.append(" ) ");
			break;
		}
		sb.append(") as count_all ");
		sb.append("inner join tomato_inn inn ON inn.id = count_all.inn_id ");
		sb.append("group by inn.id,created_time ");
		if(activeSearchBean.getActiveType() == ActiveSearchBean.ACTIVE_TYPE_BOOK){
			sb.append("order by book_num desc,inn.id desc");
		}else{
			sb.append("order by check_num desc,inn.id desc");
		}
		return this.findPageWithSql(activeSearchBean.isPage(), page, sb.toString());
	}
	
	// 查询该地区客栈分页		
	public Page<InAreaPage> select(Page<InAreaPage> page, String condition,String status, String useStatus)  {
		StringBuffer sql = new StringBuffer();
		int regionId = regionDao.selectId(status);
		sql.append("select en.* from ( ");
		sql.append("select i.id,i.name,a.mobile as contact, ");
		sql.append("case when a.last_logined_at > to_char(to_date(to_char(now(),'yyyy-mm-dd'),'YYYY-MM-dd')-3,'YYYY-MM-dd') then 1 else 0 end as status, ");
		sql.append("i.registered_at as regDate ");
		sql.append("from tomato_inn i ");
		sql.append("inner join tomato_inn_admin a on i.id = a.inn_id ");
		sql.append("inner join tomato_inn_area_region r on i.id = r.inn_id ");
		sql.append("where r.region_id = ? and a.parent_id is null ");//不查子账号
		if(StringUtils.isNotBlank(condition)) {	
			sql.append("and (i.name like '%"+condition+"%' or a.mobile like '%"+condition+"%') ");		
		}
		sql.append("order by i.registered_at desc ");
		sql.append(") as en ");
		if(!useStatus.equals("")){
			int s = 0;
			if(useStatus.equals("正在使用")){
				s = 1;
			}
			sql.append("where en.status="+s);
		}
		return (Page<InAreaPage>) this.findPageWithSql(InAreaPage.class, page, sql.toString(),new Object[] {regionId});
	}

	public void searchMarketInn(Page<Inn> page, SearchInnBean searchInnBean) {
		StringBuilder sb = new StringBuilder();
		String input = searchInnBean.getInput();
		sb.append("select i.*,ad.mobile as mobile ");
		sb.append("from tomato_inn i ");
		sb.append("inner join tomato_inn_admin ad ON ad.inn_id = i.id and ad.parent_id is null ");
		sb.append("where i.in_market = '1' ");
		if(StringUtils.isNoneBlank(input)){
			switch(searchInnBean.getSearchType()){
			case 1:
				sb.append("and i.name like '%"+input+"%' ");
				break;
			case 2:
				sb.append("and ad.mobile like '%"+input+"%' ");
				break;
			}
		}
		if(StringUtils.isNoneBlank(page.getOrderBy())){
			sb.append("order by ").append(page.getOrderBy()).append(" ").append(page.getOrder());
		}
//		this.findPageWithSql(searchInnBean.isPage(), page, sb.toString());
		this.setTotalCount(sb.toString(), page);
		this.setPageResult(sb.toString(), page.getPageNo(), page);
	}
	

	public List<Inn> rows2Obj(List<Map<String, Object>> rows) {
		List<Inn> inns = Lists.newArrayList();
		for (Map<String, Object> map : rows) {
			Inn inn = new Inn();
			inn.setId((Integer) map.get("id"));
			inn.setName((String) map.get("name"));
			inn.setMobile((String) map.get("mobile"));
			inn.setAlipayCode((String) map.get("alipay_code"));
			inn.setAlipayUser((String) map.get("alipay_user"));
			inn.setTenpayCode((String) map.get("tenpay_code"));
			inn.setTenpayUser((String) map.get("tenpay_user"));
			BigDecimal type = (BigDecimal) map.get("bank_type");
			inn.setBankType((type != null)?type.intValue():null);
			inn.setBankAccount((String) map.get("bank_account"));
			inn.setBankCode((String) map.get("bank_code"));
			inn.setBankName((String) map.get("bank_name"));
			inn.setBankRegion((String) map.get("bank_region"));
			inn.setBankProvince((String) map.get("bank_province"));
			inn.setBankCity((String) map.get("bank_city"));
			inn.setInMarketCreatedUser((String) map.get("in_market_created_user"));
			Character inMarket = (Character) map.get("in_market");
			inn.setInMarket(String.valueOf(inMarket));
			inn.setJoinMarketTime((Date) map.get("join_market_time"));
			Character pricePolicy = (Character) map.get("price_policy");
			inn.setPricePolicy(String.valueOf(pricePolicy));
			Short ratio = (Short) map.get("total_commission_ratio");
			if(ratio != null){
				inn.setTotalCommissionRatio(Integer.valueOf(ratio));
			}
			inns.add(inn);
		}
		return inns;
	}

	public Map<String, Object> getAccountMap(int innId) {
		StringBuilder sb = new StringBuilder();
		sb.append("select c.contract_person as person,c.contract_phone as contact,m.alipay_account as alipay");
		sb.append(",b.bank_no as bankNo ");
		sb.append("from wg_inn_wei_shop m ");
		sb.append("inner join wg_inn_wei_bank b ON b.inn_wei_shop_id = m.id ");
		sb.append("inner join wg_inn_wei_contract c ON c.inn_wei_shop_id = m.id ");
		sb.append("where m.inn_id = ? ");
		return this.findMapWithSql(sb.toString(), innId);
	}

	@SuppressWarnings("unchecked")
	public List<RegionRadioBo> getRegionCounts(String fromDate, String toDate) {
		fromDate += " 00:00:00";
		toDate += " 23:59:59";
		StringBuilder sb = new StringBuilder();
		sb.append("select new com.project.bean.bo.RegionRadioBo(rg.id, r.avgPrice, r.checkInRadio, r.createTime, r.checkInRooms) ");
		sb.append("from RegionCount r inner join r.region rg ");
		sb.append("where r.createTime >= '").append(fromDate).append("' ");
		sb.append("and r.createTime <= '").append(toDate).append("' ");
		return this.createQuery(sb.toString()).list();
	}
	
	public List<RegionCount> getRegionCounts(String day){
		String from = day;
		String to = day + " 23:59:59";
		StringBuilder sb = new StringBuilder();
		sb.append("select count(foo.room_id) as room_num,count(distinct foo.room_id) as room_nums,foo.reg_id as reg_id,sum(foo.price) as money ");
		sb.append("from ( ");
		sb.append("	select r.id as room_id,rg.id as reg_id,o.income_room_price as price ");
		sb.append("	from tomato_inn i ");
		sb.append("	inner join tomato_inn_region rg on rg.id = i.region_id ");
		sb.append("	inner join tomato_inn_room r on r.inn_id = i.id ");
		sb.append("	inner join tomato_inn_room_order o on o.room_id = r.id ");
		sb.append("	where o.status = 1 ");
		sb.append("	AND ( ");
		sb.append("		(o.check_in_at BETWEEN '"+from+"' AND '"+to+"') ");
		sb.append("		OR (o.check_in_at < '"+from+"' AND o.check_out_at > '"+to+"') ");
		sb.append("	) ");
		sb.append("	and i.name not like '%测试%' ");
		sb.append("	union all ");
		sb.append("	select r.id as room_id,rg.id as reg_id,o.income_room_price as price ");
		sb.append("	from tomato_inn i ");
		sb.append("	inner join tomato_inn_region rg on rg.id = i.region_id ");
		sb.append("	inner join tomato_inn_room r on r.inn_id = i.id ");
		sb.append("	inner join tomato_inn_room_check_in o on o.room_id = r.id ");
		sb.append("	where o.status in (1,3) ");
		sb.append("	AND ( ");
		sb.append("		(o.check_in_at BETWEEN '"+from+"' AND '"+to+"') ");
		sb.append("		OR (o.check_in_at < '"+from+"' AND o.check_out_at > '"+to+"') ");
		sb.append("	) ");
		sb.append("	and i.name not like '%测试%' ");
		sb.append(") foo ");
		sb.append("group by foo.reg_id ");
		List<Map<String, Object>> resultMaps = this.findListMapWithSql(sb.toString());
		Map<String, RegionCount> map = Maps.newConcurrentMap();
		getAllRooms4Region(map, day);
		for (Map<String, Object> m : resultMaps) {
			Integer regionId = (Integer) m.get("reg_id");
			String key = day + "_" + regionId;
			RegionCount count = map.get(key);
			if(count == null){
				continue;
			}
			BigInteger num = (BigInteger) m.get("room_nums");
			count.setCheckInRooms(NumberUtil.isNull(num));
			num = (BigInteger) m.get("room_num");
			count.setNoMergeRooms(NumberUtil.isNull(num));
			count.setCreateTime(DateUtil.parse(day));
			count.setTotalFee((Double) m.get("money"));
			Double avgPrice = count.getTotalFee()/count.getNoMergeRooms();
			Double radio = (count.getTotalRooms() > 0)?(double)count.getCheckInRooms()/count.getTotalRooms()*100:0.0;
			count.setAvgPrice(avgPrice);
			count.setCheckInRadio(radio);
			regionCountDao.save(count);
		}
		return null;
	}
	
	private void getAllRooms4Region(Map<String, RegionCount> map, String day){
		String from = DateUtil.format(DateUtil.addDay(DateUtil.parse(day), -3));
		String to = DateUtil.format(DateUtil.addDay(DateUtil.parse(day), 3)) + " 23:59:59";
		StringBuilder sb = new StringBuilder();
		sb.append("select count(r.id) as num,rg.id as reg_id ");
		sb.append("from tomato_inn ii ");
		sb.append("inner join tomato_inn_region rg on rg.id = ii.region_id ");
		sb.append("inner join tomato_inn_room r on r.inn_id = ii.id ");
		sb.append("where exists ( ");
		sb.append("	select i.id ");
		sb.append("	from tomato_inn i ");
		sb.append("	inner join tomato_inn_room r on r.inn_id = i.id ");
		sb.append("	inner join tomato_inn_room_order o on o.room_id = r.id ");
		sb.append("	where o.status = 1 ");
		sb.append("	AND ( ");
		sb.append("		(o.check_in_at BETWEEN '"+from+"' AND '"+to+"') ");
		sb.append("		OR (o.check_in_at < '"+from+"' AND o.check_out_at > '"+to+"') ");
		sb.append("	) ");
		sb.append("	and i.id = ii.id ");
		sb.append("	and i.name not like '%测试%' ");
		sb.append("	union all ");
		sb.append("	select i.id ");
		sb.append("	from tomato_inn i ");
		sb.append("	inner join tomato_inn_room r on r.inn_id = i.id ");
		sb.append("	inner join tomato_inn_room_check_in o on o.room_id = r.id ");
		sb.append("	where o.status in (1,3) ");
		sb.append("	AND ( ");
		sb.append("		(o.check_in_at BETWEEN '"+from+"' AND '"+to+"') ");
		sb.append("		OR (o.check_in_at < '"+from+"' AND o.check_out_at > '"+to+"') ");
		sb.append("	) ");
		sb.append("	and i.id = ii.id ");
		sb.append("	and i.name not like '%测试%' ");
		sb.append(") ");
		sb.append("group by rg.id ");
		List<Map<String, Object>> resultMaps = this.findListMapWithSql(sb.toString());
		if(ListUtil.isNotEmpty(resultMaps)){
			for (Map<String, Object> m : resultMaps) {
				RegionCount count = new RegionCount();
				BigInteger num =  (BigInteger) m.get("num");
				count.setTotalRooms(NumberUtil.isNull(num));
				InnRegion region = new InnRegion();
				region.setId((Integer) m.get("reg_id"));
				count.setRegion(region);
				map.put(day+"_"+region.getId(), count);
			}
		}
	}

	public void updateInnBrand(Integer innId, boolean brand) {
		String sql = "update tomato_inn set has_brand=? where id=? ";
		executeUpdateWithSql(sql,brand,innId);
	}

	/**
	 * @param page
	 * @param keyWord
	 * @param isFilt 
	 * @return
	 */
	public Page<Inn> getPage(Page<Inn> page, String keyWord, boolean isFilt) {
		StringBuffer sb = new StringBuffer();
//		sb.append("select i.*,a.user_code as mobile ");
//		sb.append("from tomato_inn i ");
//		sb.append("inner join tomato_inn_admin a on a.inn_id = i.id and a.parent_id is null ");
//		sb.append("where a.status = 2 and (i.name like '%"+keyWord+"%') ");
//		sb.append("union ");
		sb.append("select i.*,a.user_code as mobile ");
		sb.append("from tomato_inn i ");
		sb.append("inner join tomato_inn_admin a on a.inn_id = i.id and a.parent_id is null ");
		sb.append("where a.status = 2 ");
		if(StringUtils.isNotBlank(keyWord)){
			sb.append("and (a.user_code = '"+keyWord+"') ");
		}
		if(isFilt){
			sb.append("and i.bank_code != '' ");
		}
		sb.append("order by i.id desc ");
		String sql = sb.toString();
		this.setTotalCount(sql, page);
		this.setPageResult(sql, page.getPageNo(), page);
		return page;
	}

	/**
	 * 根据PMS注册账号，查询客栈房态
	 * @param userCode PMS注册账号
	 */
	public List<Map<String, Object>> selectInnAdminByUserCode(String userCode) {
		List<Map<String, Object>> listMap = null;
		if(StringUtils.isNotBlank(userCode)) {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT t1.ID AS inn_id,t1.name AS inn_name,t2.user_code AS user_code,t2.admin_type AS admin_type,");
			sql.append("(CASE WHEN t2.admin_type = '1' THEN 'PMS' WHEN t2.admin_type = '2' THEN 'EBK' END) AS admin_type_str");
			sql.append(" FROM tomato_inn t1 LEFT JOIN tomato_inn_admin t2 ON t1. ID = t2.inn_id");
			sql.append(" WHERE t2.user_code = '" + userCode + "'");
			listMap = findListMapWithSql(sql.toString());
		}
		return listMap;
	}

	/**
	 * 根据客栈ID，更新全部房态切换状态
	 * @param innId PMS客栈ID
	 * @param adminType 房态切换状态
	 */
	public void updateInnAdminType(Integer innId, Integer adminType) {
		executeUpdateWithSql("UPDATE tomato_inn_admin SET admin_type='" + adminType + "' where inn_id='" + innId + "'");
	}
}
