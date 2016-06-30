package com.project.web.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;






import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.project.bean.SearchChannelOrderBean;
import com.project.bean.SearchInnBean;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.core.utils.reflection.ConvertUtil;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.entity.api.ApiChannel;
import com.project.entity.api.ChannelMainOrder;
import com.project.entity.inn.Inn;
import com.project.entity.inn.InnAdmin;
import com.project.entity.wg.WgOtaInfo;
import com.project.service.api.ApiChannelService;
import com.project.service.api.ChannelMainOrderService;
import com.project.service.inn.InnManager;
import com.project.utils.CacheUtil;
import com.project.utils.ResourceBundleUtil;
import com.project.web.BaseController;


@Controller
@RequestMapping(value = "/apisale/channel")
public class ApiChannelController extends BaseController{
	
	@Autowired
	private ApiChannelService apiChannelService;
	@Autowired
	private InnManager innManager;
	@Autowired
	private ChannelMainOrderService channelMainOrderService;
	
	
	@RequestMapping(value = "")
	public String list(Model model, Page<ApiChannel> page){
		//权限验证
		List<ApiChannel> apiChannels = apiChannelService.getAllApiChannel();
		page.setTotalCount(apiChannels.size());
		page.setResult(apiChannels);
		model.addAttribute("page", page);
		model.addAttribute("currentPage", "apiChannel");
		model.addAttribute("currentBtn", "apiChannel");
		
		return "api/apiChannel";
	}
	
	@RequestMapping(value = "marketInn")
	public String marketInns(Model model, Page<Inn> page, SearchInnBean searchInnBean){
		initPage(page,20);
		page.setOrderBy("join_market_time");
		page.setOrder(Page.DESC);
		//权限验证
		innManager.searchMarketInn(page, searchInnBean);
		//获取客栈可售房间数
		searchInnBean.setInnIds(ConvertUtil.convertElementPropertyToString(page.getResult(), "id", ","));
		innManager.getMarketRooms(page, searchInnBean);
		model.addAttribute("page", page);
		model.addAttribute("searchBean", searchInnBean);
		model.addAttribute("currentPage", "apiChannel");
		model.addAttribute("currentBtn", "marketInn");
		return "api/marketInn";
	}
	
	@RequestMapping(value = "saveMarketInn")
	@ResponseBody
	public AjaxResult saveMarketInns(Model model, Inn inn){
		//权限验证
		Inn proInn = innManager.findById(inn.getId());
		proInn.setInMarket(inn.getInMarket());
		proInn.setPricePolicy(inn.getPricePolicy());
		proInn.setTotalCommissionRatio(inn.getTotalCommissionRatio());
		String sysUserCode = SpringSecurityUtil.getCurrentUserName();
		if(proInn.getJoinMarketTime() != null){
			innManager.removeUpdate(proInn, sysUserCode);
		}else{
			innManager.update(proInn, sysUserCode);
		}
		return new AjaxResult(Constants.HTTP_OK, proInn);
	}
	
	@RequestMapping(value = "orders")
	public String getMainOrders(Model model, Page<ChannelMainOrder> page, SearchChannelOrderBean searchBean){
		initPage(page,20);
		int days = Integer.parseInt(ResourceBundleUtil.getString("report.active.days"));
		searchBean.setDays(days);
		page.setOrderBy("pay_time");
		page.setOrder(Page.DESC);
		//权限验证
		List<WgOtaInfo> channels = CacheUtil.getWgOtaInfos();
		channelMainOrderService.getPage(page, searchBean);
		Double totalInPrice = channelMainOrderService.getTotalInComePrice(searchBean);
		Double totalSalePrice = channelMainOrderService.getTotalSalePrice(searchBean);
		Map<String, Object> accountMap = Maps.newConcurrentMap();
		if(searchBean.getInnIds() > 0){//查询客栈的小站  账户信息
			accountMap = innManager.getAccountMap(searchBean.getInnIds());
		}
		model.addAttribute("page", page);
		model.addAttribute("searchBean", searchBean);
		model.addAttribute("accountMap", accountMap);
		model.addAttribute("channels", channels);
		model.addAttribute("totalInPrice", totalInPrice);
		model.addAttribute("totalSalePrice", totalSalePrice);
		model.addAttribute("currentPage", "apiChannel");
		model.addAttribute("currentBtn", "channelOrder");
		return "api/orders";
	}
	
	/**
	 * 查询客栈信息
	 * @param model
	 * @param inn
	 * @return
	 */
	@RequestMapping(value = "searchInn", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResult searchInn(Model model, Inn inn){
		InnAdmin admin = innManager.findByMobile(inn.getMobile());
		Inn proInn = null;
		if(admin != null){
			proInn = admin.getInn();
		}else{
			return new AjaxResult(Constants.HTTP_400, "该账号不存在!");
		}
		if("1".equals(proInn.getInMarket())){
			return new AjaxResult(Constants.HTTP_400, "该账号所在客栈已经在库存中了，如需修改请搜索编辑！");
		}
		return new AjaxResult(Constants.HTTP_OK, proInn);
	}
	
	/**
	 * 将客栈移除第三方代销库存
	 * @param model
	 * @param inn
	 * @return
	 */
	@RequestMapping(value = "removeStock")
	@ResponseBody
	public AjaxResult removeInnFromStock(Model model, Inn inn){
		Inn proInn = innManager.findById(inn.getId());
		proInn.setInMarket(Constants.INN_NOT_JOIN_MARKET);
		proInn.setPricePolicy(null);
		proInn.setTotalCommissionRatio(null);
		proInn.setJoinMarketTime(null);
		String sysUserCode = SpringSecurityUtil.getCurrentUserName();
		innManager.removeUpdate(proInn, sysUserCode);
		return new AjaxResult(Constants.HTTP_OK, proInn);
	}
	
	/**
	 * 结算订单
	 * @param model
	 * @param mainOrder
	 * @return
	 */
	@RequestMapping(value = "balanceOrder")
	@ResponseBody
	public AjaxResult editMainOrder(Model model, ChannelMainOrder mainOrder){
		//权限验证
		String sysUser = SpringSecurityUtil.getCurrentUserName();
		ChannelMainOrder proMainOrder = channelMainOrderService.findById(mainOrder.getId());
		proMainOrder.setIsBalance(mainOrder.getIsBalance());
		if(Constants.ORDER_IS_BALANCE.equals(mainOrder.getIsBalance())){
			channelMainOrderService.update(proMainOrder, sysUser);
		}else{
			channelMainOrderService.removeUpdate(proMainOrder, sysUser);
		}
		return new AjaxResult(Constants.HTTP_OK, proMainOrder);
	}
	/**
	 * 结算订单
	 * @param model
	 * @param mainOrder
	 * @return
	 */
	@RequestMapping(value = "getOrder")
	@ResponseBody
	public AjaxResult getOrder(Model model, ChannelMainOrder mainOrder){
		//权限验证
		ChannelMainOrder proMainOrder = channelMainOrderService.findByOrderNo(mainOrder.getChannelOrderNo());
		return new AjaxResult(Constants.HTTP_OK, proMainOrder);
	}

}
