/**
* @Title: HotelReviewManager.java
* @Package com.project.service.audit
* @Description: 
* @author Administrator
* @date 2014年3月27日 上午11:57:56
*/

/**
 * 
 */
package com.project.service.inn;

import com.google.common.collect.Lists;
import com.project.bean.report.FuncReportSearchBean;
import com.project.bean.report.XzReportBean;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.dao.inn.InnFuncReportDao;
import com.project.entity.inn.InnFuncReport;
import com.project.entity.plug.InnPlugFunc;
import com.project.utils.time.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author X
 *
 */
@Component
@Transactional
public class InnFuncReportManager {

	@Autowired
	private InnFuncReportDao innFuncReportDao;
	
	public List<InnFuncReport> getPlugFuncsAmount(List<InnPlugFunc> plugFuncs, Date recordAt, Integer innAmount){
		return innFuncReportDao.getPlugFuncsAmount(plugFuncs, recordAt, innAmount);
	}
	
	public InnFuncReport getLockScreenAmount(Date recordAt, Integer innAmount){
		return innFuncReportDao.getLockScreenAmount(recordAt, innAmount);
	}
	
	public InnFuncReport getRoomSortAmount(Date recordAt, Integer innAmount){
		return innFuncReportDao.getRoomSortAmount(recordAt, innAmount);
	}
	
	public List<InnFuncReport> getRoomStyleAmount(Date recordAt, Integer userAmount){
		return innFuncReportDao.getRoomStyleAmount(recordAt, userAmount);
	}
	
	public InnFuncReport getLogAmount(Date recordAt, Integer innAmount, Integer logType, Integer reportItem){
		return innFuncReportDao.getLogAmount(recordAt, innAmount, logType, reportItem);
	}
	
	public List<InnFuncReport> getOtaAmount(Date recordAt, Integer innAmount, List<Integer> otaIds){
		return innFuncReportDao.getOtaAmount(recordAt, innAmount, otaIds);
	}
	
	public InnFuncReport getActiveAmount(Date recordAt, Integer innAmount){
		return innFuncReportDao.getActiveAmount(recordAt, innAmount);
	}
	
	public InnFuncReport getChainStoreAmount(Date recordAt, Integer innAmount){
		return innFuncReportDao.getChainStoreAmount(recordAt, innAmount);
	}
	
	public void save(List<InnFuncReport> reports){
		innFuncReportDao.save(reports);
	}
	
	public void initReportMap(String[] items, Map<String, FuncReportSearchBean> reportMap){
		for (String item : items) {
			FuncReportSearchBean bean = new FuncReportSearchBean();
			bean.setSelectDate(item);
			reportMap.put(item, bean);
		}
	}
	
	public void getMapDetail(Map<String, FuncReportSearchBean> reportMap, String from, String to){
		List<InnFuncReport> reports = getByDate(from, to);
		for (InnFuncReport r : reports) {
			String date = DateUtil.format(r.getRecordedAt());
			FuncReportSearchBean bean = reportMap.get(date);
			bean.setReport(r);
		}
	}
	
	public void getMapDetailNew(Map<String, InnFuncReport> reportMap, String from, String to){
		List<InnFuncReport> reports = getByDate(from, to);
		for (InnFuncReport r : reports) {
			String date = DateUtil.format(r.getRecordedAt());
			date += "-" + r.getFuncItemType();
			reportMap.put(date, r);
		}
	}
	
	public List<InnFuncReport> getByDate(String from, String to){
		List<InnFuncReport> reports = Lists.newArrayList();
		reports = innFuncReportDao.getByDate(from, to);
		return reports;
	}
	
	public Page<XzReportBean> getPagingList(int type,int pageNo, int pageSize, String date
			, String input){
		Page<XzReportBean> page = new Page<>(pageSize);
		switch(type){
		case Constants.REPORT_DETAIL_TYPE_PLUG_STAMP:
			page = innFuncReportDao.getStampPage(pageNo, date, page);
			break;
		case Constants.REPORT_DETAIL_TYPE_XZ:
			page = innFuncReportDao.getFQXZPage(pageNo, date, page, input);
			break;
		}
		return page;
	}
	
	public Page<XzReportBean> getOpenMCInnNum(int pageNo, int pageSize, String date
			, String input){
		Page<XzReportBean> page = new Page<>(pageSize);
		page = innFuncReportDao.getFQXZPageOpenMc(pageNo, date, page, input);
		return page;
	}

}
