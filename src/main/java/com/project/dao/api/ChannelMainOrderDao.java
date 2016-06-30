package com.project.dao.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.project.bean.SearchChannelOrderBean;
import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.api.ChannelMainOrder;

@Component
public class ChannelMainOrderDao extends HibernateDao<ChannelMainOrder, Long> {

	public List<ChannelMainOrder> findall() {
		return this.getAll();
	}
	
	public void getPage(Page<ChannelMainOrder> page, SearchChannelOrderBean searchBean){
		StringBuilder sb = getSql4MainOrder(searchBean, page);
		this.findPageWithSql(searchBean.isPage(), page, sb.toString());
	}

	private StringBuilder getSql4MainOrder(SearchChannelOrderBean searchBean, Page<ChannelMainOrder> page) {
		String from = searchBean.getFromDate() + " 00:00:00";
		String to = searchBean.getToDate() + " 23:59:59";
		StringBuilder sb = new StringBuilder();
		sb.append("select m.* from tomato_channel_main_order m ");
		sb.append("inner join tomato_inn i ON i.id = m.inn_id ");
		sb.append("where m.order_time >= '"+from+"' and m.order_time <= '"+to+"' ");
		if(searchBean.getChannelId() > 0){
			sb.append("and m.fx_channel_id = "+searchBean.getChannelId()+" ");
		}
		if(searchBean.getInnIds() > 0){
			sb.append("and m.inn_id = "+searchBean.getInnIds()+" ");
		}
		if(StringUtils.isNoneBlank(searchBean.getIsBalance())){
			sb.append("and m.is_balance = '"+searchBean.getIsBalance()+"' ");
		}
		if(StringUtils.isNotBlank(searchBean.getInput())){
			switch(searchBean.getSearchType()){
			case 1:
				sb.append("and i.name like '%"+searchBean.getInput()+"%' ");
				break;
			case 2:
				sb.append("and m.channel_order_no like '%"+searchBean.getInput()+"%' ");
				break;
			}
		}
		sb.append("and m.channel_id = 102 ");
		if(StringUtils.isNoneBlank(page.getOrderBy())){
			sb.append("order by ").append(page.getOrderBy()).append(" ").append(page.getOrder());
		}
		return sb;
	}
	
	private String getSql4MainOrderCount(SearchChannelOrderBean searchBean) {
		String from = searchBean.getFromDate() + " 00:00:00";
		String to = searchBean.getToDate() + " 23:59:59";
		StringBuilder sb = new StringBuilder();
		sb.append("where m.order_time >= '"+from+"' and m.order_time <= '"+to+"' ");
		if(searchBean.getChannelId() > 0){
			sb.append("and m.fx_channel_id = "+searchBean.getChannelId()+" ");
		}
		if(searchBean.getInnIds() > 0){
			sb.append("and m.inn_id = "+searchBean.getInnIds()+" ");
		}
		if(StringUtils.isNoneBlank(searchBean.getIsBalance())){
			sb.append("and m.is_balance = '"+searchBean.getIsBalance()+"' ");
		}
		if(StringUtils.isNotBlank(searchBean.getInput())){
			switch(searchBean.getSearchType()){
			case 1:
				sb.append("and i.name like '%"+searchBean.getInput()+"%' ");
				break;
			case 2:
				sb.append("and m.channel_order_no like '%"+searchBean.getInput()+"%' ");
				break;
			}
		}
		sb.append("and m.channel_id = 102 ");
		return sb.toString();
	}

	public Double getTotalInComePrice(SearchChannelOrderBean searchBean) {
		StringBuilder sb = new StringBuilder();
		sb.append("select SUM(foo.price * foo.days) as money ");
		sb.append("from ( ");
		sb.append("select o.original_price AS price,EXTRACT(DAY FROM(o.check_out_at - o.check_in_at)) AS days ");
		sb.append("from tomato_channel_main_order m ");
		sb.append("inner join tomato_inn i ON i.id = m.inn_id ");
		sb.append("inner join tomato_channel_order o ON o.main_id = m.id ");
		sb.append(getSql4MainOrderCount(searchBean));
		sb.append(" ) foo");
		Map<String, Object> resultMap = new HashMap<>();
		resultMap = this.findMapWithSql(sb.toString());
		Double money = (Double) resultMap.get("money");
		return money;
	}
	
	public Double getTotalSalePrice(SearchChannelOrderBean searchBean) {
		StringBuilder sb = new StringBuilder();
		sb.append("select SUM(foo.price * foo.days) as money ");
		sb.append("from ( ");
		sb.append("select o.sale_price AS price,EXTRACT(DAY FROM(o.check_out_at - o.check_in_at)) AS days ");
		sb.append("from tomato_channel_main_order m ");
		sb.append("inner join tomato_inn i ON i.id = m.inn_id ");
		sb.append("inner join tomato_channel_order o ON o.main_id = m.id ");
		sb.append(getSql4MainOrderCount(searchBean));
		sb.append(" ) foo");
		Map<String, Object> resultMap = new HashMap<>();
		resultMap = this.findMapWithSql(sb.toString());
		Double money = (Double) resultMap.get("money");
		return money;
	}
	
}
