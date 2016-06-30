package com.project.service.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.project.bean.CommSearchBean;
import com.project.bean.bo.ActiveNum;
import com.project.bean.bo.RegionRadioBo;
import com.project.bean.report.ActiveSearchBean;
import com.project.bean.report.TimelineCell;
import com.project.cache.InnCache;
import com.project.cache.RegionCache;
import com.project.cache.abstractCache.IDataCache;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.dao.inn.InnDao;
import com.project.entity.inn.Inn;
import com.project.entity.inn.InnRegion;
import com.project.entity.report.RegionCount;
import com.project.utils.NumberUtil;
import com.project.utils.time.DateUtil;

/**
 * 
 * @author
 * mowei
 */
//Spring Bean的标识.
@Component
//默认将类中的所有函数纳入事务管理.
@Transactional
public class ActiveService {

	@Autowired
	private InnDao innDao;
	
	/**
	 * 获取所有区域
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object getAllRegion() {
		IDataCache dataCache = new RegionCache();
		return (List<InnRegion>) dataCache.getOrElse(Constants.CACHE_FLAG_ALL_REGION);
	}
	
	public String getRegionNameById(int id){
		IDataCache dataCache = new RegionCache();
		List<InnRegion> regions = (List<InnRegion>) dataCache.getOrElse(Constants.CACHE_FLAG_ALL_REGION);
		for (InnRegion innRegion : regions) {
			if(innRegion.getId() == id){
				return innRegion.getName();
			}
		}
		return null;
	}
	
	/**
	 * 获取指定的所有客栈
	 * @param activeSearchBean
	 */
	@SuppressWarnings("unchecked")
	public void getSearchInn(Page<Inn> page, ActiveSearchBean activeSearchBean) {
//		List<Inn> innList = Lists.newArrayList();
//		IDataCache dataCache = new InnCache();
//		List<Inn> inns = (List<Inn>) dataCache.getOrElse(Constants.CACHE_FLAG_ALL_INN);
//		if(activeSearchBean.getActiveType() == ActiveSearchBean.ACTIVE_TYPE_ALL){
//			if(activeSearchBean.getAreaId()!=0){
//				for(Inn i:inns){
//					if(i.getRegion()!=null && i.getRegion().getId() == activeSearchBean.getAreaId()){
//						innList.add(i);
//					}
//				}
//			}else{
//				innList.addAll(inns);
//			}
//			page.setTotalCount(innList.size());
//			List<Inn> toPageInns = Lists.newArrayList();
//			if(innList.size()>page.getFirst()-1){
//				toPageInns = innList.subList(page.getFirst()-1, innList.size()>page.getPageNo()*page.getPageSize()?page.getPageNo()*page.getPageSize():innList.size());
//			}
//			page.setResult(toPageInns);
//		}else{
//		}
		innDao.getInnByDateRanges(page,activeSearchBean);
	}
	
	/**
	 * 获取指定日期内的 时间轴单元格
	 * @return
	 */
	public List<TimelineCell> getTimelineCells(ActiveSearchBean activeSearchBean)  {
		DateTime nowDate = new DateTime(activeSearchBean.getToDate());
		Date fromDate = new DateTime(activeSearchBean.getFromDate()).toDate();
//		if(StringUtils.isNotBlank(activeSearchBean.getSelectDate())){
//			fromDate = new DateTime(activeSearchBean.getSelectDate()).toDate();
//		}
		String from = activeSearchBean.getFromDate();
 		String to = activeSearchBean.getToDate();
 		int days = DateUtil.getDifferDay(from, to) + 1;
		List<TimelineCell> timelineCells = new ArrayList<TimelineCell>(days);
		Calendar fromCal = Calendar.getInstance();
		fromCal.setTime(fromDate);

		for (int i = 0; i < days; i++) {
			int span = Days.daysBetween(new DateTime(fromCal.getTime()), nowDate).getDays();
			TimelineCell cell = new TimelineCell();
			cell.setCdate(fromCal.getTime());
			cell.setDayOfWeek(fromCal.get(Calendar.DAY_OF_WEEK));
			cell.setYesterday(false);
			cell.setToday(false);
			if (span == 1)
				cell.setYesterday(true);
			if (span == 0)
				cell.setToday(true);
			cell.setWeekday(Constants.CHINESE_WEEK_DAYS[cell.getDayOfWeek() - 1]);
			timelineCells.add(cell);
			fromCal.add(Calendar.DATE, 1);
		}
		return timelineCells;
	}
	
	/**
	 * 查询活跃数据
	 */
	@Transactional(readOnly = true)
	public Map<String,ActiveNum> statisticsActive(ActiveSearchBean activeSearchBean) {
		Map<String,ActiveNum> innDateActiveMap = Maps.newHashMap();
//		if(StringUtils.isNotBlank(activeSearchBean.getInnIds()) && (activeSearchBean.getActiveFlag()==null || 
//				activeSearchBean.getActiveFlag()!=null && activeSearchBean.getActiveFlag())){
		if(StringUtils.isNotBlank(activeSearchBean.getInnIds())){
			List<Map<String,Object>> searchMap = innDao.statisticsActives(activeSearchBean);
			for(Map<String,Object> m:searchMap){
				ActiveNum an = new ActiveNum();
				an.setLoginNum(Integer.parseInt(m.get("login_num").toString()));
				an.setBookNum(Integer.parseInt(m.get("book_num").toString()));
				an.setCheckNum(Integer.parseInt(m.get("check_num").toString()));
				innDateActiveMap.put(m.get("created_time")+"_"+m.get("inn_id"), an);
			}
		}
		return innDateActiveMap;
	}

	public void getRegionCounts(Map<String, RegionRadioBo> resultMap,
			String fromDate, String toDate) {
		List<RegionRadioBo> radioBos = innDao.getRegionCounts(fromDate, toDate);
		for (RegionRadioBo r : radioBos) {
			r.setAvgPrice(NumberUtil.round(r.getAvgPrice(), Constants.REPORT_RATIO_ACCURATE_LENGTH));
			r.setCheckInRadio(NumberUtil.round(r.getCheckInRadio(), Constants.REPORT_RATIO_ACCURATE_LENGTH));
			resultMap.put(r.getCreateTime()+"_"+r.getAreaId(), r);
		}
	}

	public List<TimelineCell> getTimelineCells(String from, String to) {
		DateTime nowDate = new DateTime(to);
		Date fromDate = new DateTime(from).toDate();
//		if(StringUtils.isNotBlank(activeSearchBean.getSelectDate())){
//			fromDate = new DateTime(activeSearchBean.getSelectDate()).toDate();
//		}
 		int days = DateUtil.getDifferDay(from, to) + 1;
		List<TimelineCell> timelineCells = new ArrayList<TimelineCell>(days);
		Calendar fromCal = Calendar.getInstance();
		fromCal.setTime(fromDate);

		for (int i = 0; i < days; i++) {
			int span = Days.daysBetween(new DateTime(fromCal.getTime()), nowDate).getDays();
			TimelineCell cell = new TimelineCell();
			cell.setCdate(fromCal.getTime());
			cell.setDayOfWeek(fromCal.get(Calendar.DAY_OF_WEEK));
			cell.setYesterday(false);
			cell.setToday(false);
			if (span == 1)
				cell.setYesterday(true);
			if (span == 0)
				cell.setToday(true);
			cell.setWeekday(Constants.CHINESE_WEEK_DAYS[cell.getDayOfWeek() - 1]);
			timelineCells.add(cell);
			fromCal.add(Calendar.DATE, 1);
		}
		return timelineCells;
	}

}
