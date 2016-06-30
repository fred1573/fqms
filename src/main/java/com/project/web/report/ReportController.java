package com.project.web.report;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;















import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.project.bean.CommSearchBean;
import com.project.bean.bo.ActiveNum;
import com.project.bean.bo.RegionRadioBo;
import com.project.bean.report.ActiveSearchBean;
import com.project.bean.report.TimelineCell;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.core.utils.reflection.ConvertUtil;
import com.project.core.utils.spring.SpringContextHolder;
import com.project.dao.inn.InnDao;
import com.project.entity.inn.Inn;
import com.project.entity.report.RegionCount;
import com.project.service.inn.InnManager;
import com.project.service.report.ActiveService;
import com.project.utils.ExcelExportUtil;
import com.project.utils.FileOperateUtil;
import com.project.utils.ResourceBundleUtil;
import com.project.utils.time.DateUtil;
import com.project.web.BaseController;

/**
 * 报表-活跃用户.
 * 
 * @author mowei
 */
@Controller
@RequestMapping(value = "/report/active")
public class ReportController extends BaseController{

	@Autowired
	private ActiveService activeService;
	@Autowired
	private InnManager innManager;

	@RequestMapping(value = "")
	public String list(Model model, Page<Inn> page, ActiveSearchBean activeSearchBean){
 		initPage(page,20);
 		int days = Integer.parseInt(ResourceBundleUtil.getString("report.active.days"));
 		activeSearchBean.setDays(days);
		//获取分页客栈
 		activeService.getSearchInn(page,activeSearchBean);
		//获取时间轴
		List<TimelineCell> timelineCells = activeService.getTimelineCells(activeSearchBean);
		//获取活跃map
		activeSearchBean.setInnIds(ConvertUtil.convertElementPropertyToString(page.getResult(), "id", ","));
		Map<String,ActiveNum> innDateActiveMap = activeService.statisticsActive(activeSearchBean);
		model.addAttribute("regions", activeService.getAllRegion());
		model.addAttribute("page", page);
		model.addAttribute("timelineCells", timelineCells);
		model.addAttribute("innDateActiveMap", innDateActiveMap);
		model.addAttribute("activeSearchBean", activeSearchBean);
		model.addAttribute("currentPage", "active");
		return "report/active";
	}
	
	@RequestMapping(value = "export")
	public void report(Model model, Page<Inn> page, ActiveSearchBean activeSearchBean
			,HttpServletRequest request, HttpServletResponse response){
		try {
			activeSearchBean.setPage(false);
			int days = Integer.parseInt(ResourceBundleUtil.getString("report.active.days"));
			activeSearchBean.setDays(days);
			activeService.getSearchInn(page,activeSearchBean);
			String[] timelineCells = getDateStrArray(activeSearchBean.getFromDate()
					, activeSearchBean.getToDate(), "yyyy-MM-dd");
			activeSearchBean.setInnIds(ConvertUtil.convertElementPropertyToString(page.getResult(), "id", ","));
			Map<String,ActiveNum> innDateActiveMap = activeService.statisticsActive(activeSearchBean);
			Map<String, Object> beans = new HashMap<>();
			List<Inn> innItems = (List<Inn>) page.getResult();
			beans.put("mapList", innDateActiveMap);
			beans.put("timeItems", timelineCells);
			beans.put("innItems", innItems);
			String filePath = "";
			String fileName = "";
			switch(activeSearchBean.getActiveType()){
			case ActiveSearchBean.ACTIVE_TYPE_BOOK:
				filePath = ExcelExportUtil.createExcelByJxls(beans,
						Constants.RESOURCE_SERVER_ROOT_PATH+Constants.SYS_RESOURCE_REPORT_BOOK_ACTIVE_PATH,true);
				fileName = activeSearchBean.getFromDate() + "至" + activeSearchBean.getToDate() + "("+activeService.getRegionNameById(activeSearchBean.getAreaId())+")预定活跃报表.xls";
				break;
			case ActiveSearchBean.ACTIVE_TYPE_CHECK:
				filePath = ExcelExportUtil.createExcelByJxls(beans,
						Constants.RESOURCE_SERVER_ROOT_PATH+Constants.SYS_RESOURCE_REPORT_CHECK_IN_ACTIVE_PATH,true);
				fileName = activeSearchBean.getFromDate() + "至" + activeSearchBean.getToDate() + "("+activeService.getRegionNameById(activeSearchBean.getAreaId())+")入住活跃报表.xls";
				break;
			}
			fileName = URLEncoder.encode(fileName, "UTF-8");
			FileOperateUtil.download(request, response, filePath, "application/octet-stream; charset=UTF-8", fileName);
		} catch (Exception e) {
		}
	}
	
	@RequestMapping(value = "region")
	public String getRegionRadio(Model model, CommSearchBean commSearchBean){
		if(commSearchBean.getToDate() == null){
			commSearchBean.setToDate(DateUtil.format(new Date()));
			commSearchBean.setFromDate(DateUtil.format(DateUtil.addDay(DateUtil.parse(commSearchBean.getToDate()), -10)));
		}
 		String fromDate = commSearchBean.getFromDate();
 		String toDate = commSearchBean.getToDate();
		List<TimelineCell> timelineCells = activeService.getTimelineCells(commSearchBean.getFromDate()
				, commSearchBean.getToDate());
		Map<String, RegionRadioBo> resultMap = Maps.newConcurrentMap();
//		innManager.saveRegionReports("2014-06-05");
		activeService.getRegionCounts(resultMap, fromDate, toDate);
		model.addAttribute("regions", activeService.getAllRegion());
		model.addAttribute("timelineCells", timelineCells);
		model.addAttribute("reportMap", resultMap);
		model.addAttribute("searchBean", commSearchBean);
		model.addAttribute("currentPage", "regionRadio");
		return "report/regionRadio";
	}
	
	@RequestMapping(value = "import")
	@ResponseBody
	public Map<String, Object> importReports(Model model, String day, String to){
		Map<String, Object> result = Maps.newConcurrentMap();
		try {
			innManager.saveRegionReports(day);
			result.put(Constants.STATUS, Constants.HTTP_OK);
			result.put("to", to);
			result.put("from", DateUtil.format(DateUtil.addDay(DateUtil.parse(day), 1)));
		} catch (Exception e) {
			result.put(Constants.STATUS, Constants.HTTP_400);
		}
		return result;
	}
	
	/****** help method  ***/
	public static String[] getDateStrArray(String fromDate,String toDate,String format) {
		Date startDate = DateUtil.parse(fromDate);
		int differ = DateUtil.getDifferDay(fromDate,toDate) + 1;
		String[] item = new String[differ];
		for(int i=0; i< differ; i++) {
			item[i] = DateUtil.format(startDate,format);
			startDate = DateUtil.addDay(startDate, 1);
		}
		return item;
	}
	
}
