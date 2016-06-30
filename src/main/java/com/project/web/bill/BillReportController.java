package com.project.web.bill;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.project.bean.bill.BillCountBean;
import com.project.bean.bill.BillDetailBean;
import com.project.bean.bill.BillSearchBean;
import com.project.core.orm.Page;
import com.project.service.bill.BillManager;
import com.project.web.BaseController;

/**
 * 番茄小站对账统计
 *
 * @author xiamaoxuan
 */
@Controller
@RequestMapping(value = "/bill")
public class BillReportController extends BaseController {
	@Autowired
	private BillManager billManager;

	@RequestMapping(value = "/count")
	public String list(Model model, HttpServletRequest request, @ModelAttribute BillSearchBean billSearchBean) {

		if (billSearchBean.getTotalPage() < billSearchBean.getNowPage() && billSearchBean.getTotalPage() != 0) {
			return "redirect:/bill/count ";
		}
		Page<BillDetailBean> page = new Page<>(10);
		Map<String, Object> paramMap = Maps.newConcurrentMap();
		billManager.getPayRecords(billSearchBean, paramMap, page);
		if (null != billSearchBean.getInnId()) {
			model.addAttribute("innId", billSearchBean.getInnId());
			model.addAttribute("innDetail", billManager.getInnDetail(billSearchBean.getInnId()));
		}
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		model.addAttribute("billSearchBean", billSearchBean);
		model.addAttribute("countBean", null);
		model.addAttribute("currentPage", "xzCount");
		return "bill/newCount";
	}

	@RequestMapping(value = "/cashpay")
	public String cash(Model model, HttpServletRequest request, @ModelAttribute BillSearchBean billSearchBean) {

		if (billSearchBean.getTotalPage() < billSearchBean.getNowPage() && billSearchBean.getTotalPage() != 0) {
			return "redirect:/bill/cash ";
		}
		Page<BillDetailBean> page = new Page<>(10);
		Map<String, Object> paramMap = Maps.newConcurrentMap();
		billManager.getXzNoPayRecords(billSearchBean, paramMap, page);
		if (null != billSearchBean.getInnId()) {
			model.addAttribute("innId", billSearchBean.getInnId());
			model.addAttribute("innDetail", billManager.getInnDetail(billSearchBean.getInnId()));
		}
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		model.addAttribute("billSearchBean", billSearchBean);
		model.addAttribute("countBean", null);
		model.addAttribute("currentPage", "xzCount");
		return "bill/cashCount";
	}

	@RequestMapping(value = "/fastCheckAndCheckstand")
	public String fastCheckAndCheckstand(Model model, HttpServletRequest request, @ModelAttribute BillSearchBean billSearchBean) {
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(10);
		page = billManager.getFastCheckPayRecords(billSearchBean, page);
		if (null != billSearchBean.getInnId()) {
			model.addAttribute("innDetail", billManager.getInnDetail(billSearchBean.getInnId()));
		}
		model.addAttribute("page", page);
		model.addAttribute("billSearchBean", billSearchBean);
		return "bill/fastCheckAndCheckstand";
	}
	
	@RequestMapping(value = "/getFinancialAccount")
	public String getFinancialAccount(Model model, BillSearchBean billSearchBean) {
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(5);
		billSearchBean.setType("financial");
		page = billManager.getFinancialAccountInnIds(billSearchBean, page);
		//获取订单总数  金额等信息
		BillCountBean bean = billManager.getGetBillCountBean(billSearchBean);
		String innIds = page.getIds();
		List<Map<String, Object>> result = billManager.getFinancialAccounts(innIds, billSearchBean);
		page.setResult(result);
		model.addAttribute("page", page);
		model.addAttribute("billSearchBean", billSearchBean);
		return "bill/financialAccount";
	}

	@RequestMapping(value = "/change")
	@ResponseBody
	public String changeState(int id, String code) {
		try {
			billManager.changeIsBalance(code);
			billManager.setPayRecBalance(id, code);
		} catch (Exception e) {
			return "false";
		}
		return "true";
	}
	
	@RequestMapping(value = "/balanceOrders")
	@ResponseBody
	public String balanceOrders(String payIds) {
		try {
			billManager.balanceOrders(payIds);
		} catch (Exception e) {
			return "false";
		}
		return "true";
	}

}
