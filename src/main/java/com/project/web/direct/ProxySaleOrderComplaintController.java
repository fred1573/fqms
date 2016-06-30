package com.project.web.direct;

import com.alibaba.fastjson.JSON;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.project.bean.proxysale.OrderComplaintSearch;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.entity.proxysale.OrderComplaintProcessLog;
import com.project.entity.proxysale.ProxySaleOrderComplaint;
import com.project.enumeration.SearchTimeType;
import com.project.service.direct.ProxySaleOrderService;
import com.project.service.proxysale.OrderComplaintProcessLogService;
import com.project.service.proxysale.ProxySaleOrderComplaintService;
import org.joda.time.DateTime;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yuneng.huang on 2016/6/14.
 */
@Controller
@RequestMapping(value = "/proxySaleOrder/complaint")
public class ProxySaleOrderComplaintController {

    private static final int DEFAULT_LIMIT = 15;
    private static final String CURRENT_PAGE = "proxySaleOrder";
    @Resource
    private ProxySaleOrderComplaintService proxySaleOrderComplaintService;
    @Resource
    private OrderComplaintProcessLogService orderComplaintProcessLogService;
    @Resource
    private ProxySaleOrderService proxySaleOrderService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping("list")
    @ResponseBody
    public AjaxResult complaintList(String orderNo) {
        List<OrderComplaintProcessLog> processLogs = orderComplaintProcessLogService.findByOrderNo(orderNo);
        return new AjaxResult(Constants.HTTP_OK,processLogs);
    }

    @RequestMapping("search")
    public String complaintSearch(Model model, OrderComplaintSearch complaintSearch,@RequestParam(defaultValue = "1")int page) {
        if (complaintSearch.getSearchType() == null) {
            complaintSearch.setSearchTimeType(SearchTimeType.CREATE_TIME);
            complaintSearch.setStartTime(new DateTime().toLocalDate().toDate());
            complaintSearch.setEndTime(new DateTime().toLocalDate().toDate());
        }
        PageBounds pageBounds = new PageBounds(page,DEFAULT_LIMIT);
        PageList<ProxySaleOrderComplaint> proxySaleOrderComplaints = proxySaleOrderComplaintService.findByPage(pageBounds, complaintSearch);
        model.addAttribute("complaintList", proxySaleOrderComplaints);
        model.addAttribute("paginator", proxySaleOrderComplaints.getPaginator());
        int complaintInnCount = proxySaleOrderComplaintService.findInnCountBySearch(complaintSearch);
        model.addAttribute("complaintInnCount", complaintInnCount);
        // 通过OMS接口，获取渠道对象集合
        Map<String, String> otaMap = proxySaleOrderService.getChannelInfoFromOMS();
        Map<String, String> childOtaMap = proxySaleOrderService.getChildChannelInfoFromOMS(complaintSearch.getChannelId());
        model.addAttribute("otaMap", otaMap);
        model.addAttribute("childOtaMap", childOtaMap);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("complaintSearch", complaintSearch);
        return "/direct/proxy_sale_order_complaint_list";
    }


    @RequestMapping("save")
    @ResponseBody
    public AjaxResult save(String data) {
        ProxySaleOrderComplaint orderComplaint = JSON.parseObject(data, ProxySaleOrderComplaint.class);
        proxySaleOrderComplaintService.save(orderComplaint);
        return new AjaxResult(Constants.HTTP_OK,null);
    }

    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletResponse response, OrderComplaintSearch complaintSearch) throws IOException {
        response.setContentType("application/vnd.ms-excel; charset=utf-8");
        response.addHeader("Content-Disposition", new String(("attachment; filename=" + "客诉列表" + ".xls").getBytes("GBK"), "ISO-8859-1"));
        OutputStream os = response.getOutputStream();
        proxySaleOrderComplaintService.exportExcel(os, complaintSearch);
    }

}
