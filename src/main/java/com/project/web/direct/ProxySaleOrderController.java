package com.project.web.direct;

import com.alibaba.fastjson.JSONObject;
import com.project.bean.direct.ProxySaleOrderForm;
import com.project.bean.direct.ProxySaleOrderVo;
import com.project.bean.vo.AjaxBase;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.entity.proxysale.CancelOrderLog;
import com.project.service.direct.CancelOrderLogService;
import com.project.service.direct.ProxySaleOrderService;
import com.project.utils.PageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 代销订单请求处理类
 * Created by sam on 2015/11/18.
 */
@Controller
@RequestMapping(value = "/proxySaleOrder/")
public class ProxySaleOrderController {
    private static final String CURRENT_PAGE = "proxySaleOrder";
    @Resource
    private ProxySaleOrderService proxySaleOrderService;
    @Resource
    private CancelOrderLogService cancelOrderLogService;

    /**
     * 跳转到代销订单列表查询页面
     *
     * @param model
     * @param proxySaleOrderForm
     * @return
     */
    @RequestMapping("list")
    public String list(Model model, ProxySaleOrderForm proxySaleOrderForm) {
        // 请求OMS代销订单查询接口
        JSONObject result = proxySaleOrderService.findProxySaleOrderFromOMS(proxySaleOrderForm, true);
        // 获取订单列表
        List<ProxySaleOrderVo> orderList = proxySaleOrderService.findProxySaleOrderVoList(result);
        // 获取分页对象
        PageUtil page = proxySaleOrderService.getPage(result, proxySaleOrderForm.getRows(), proxySaleOrderForm.getPage());
        // 获取订单的统计信息
        Map<String, String> totalInfo = proxySaleOrderService.findOrderTotalInfo(result);
        // 通过OMS接口，获取渠道对象集合
        Map<String, String> otaMap = proxySaleOrderService.getChannelInfoFromOMS();
        Map<String, String> childOtaMap = proxySaleOrderService.getChildChannelInfoFromOMS(proxySaleOrderForm.getChannelId());
        model.addAttribute("otaMap", otaMap);
        model.addAttribute("childOtaMap", childOtaMap);
        model.addAttribute("totalInfo", totalInfo);
        model.addAttribute("orderList", orderList);
        model.addAttribute("pageUtil", page);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("directOrderForm", proxySaleOrderForm);
        return "/direct/proxy_sale_order_list";
    }

    /**
     * 跳转到信用住订单列表查询页面
     *
     * @param model
     * @return
     */
    @RequestMapping("creditList")
    public String creditOrderList(Model model, ProxySaleOrderForm proxySaleOrderForm) {
        // 请求OMS信用住订单查询接口
        proxySaleOrderForm.setOtaId(Constants.OMS_PROXY_CREDIT_PID);
        proxySaleOrderForm.setChannelId(Constants.OMS_PROXY_CREDIT_PID);
        JSONObject result = proxySaleOrderService.findProxySaleOrderFromOMS(proxySaleOrderForm, true);
        // 获取订单列表
        List<ProxySaleOrderVo> orderList = proxySaleOrderService.findProxySaleOrderVoList(result);
        // 获取分页对象
        PageUtil page = proxySaleOrderService.getPage(result, proxySaleOrderForm.getRows(), proxySaleOrderForm.getPage());
        // 获取订单的统计信息
        Map<String, String> totalInfo = proxySaleOrderService.findOrderTotalInfo(result);
        model.addAttribute("totalInfo", totalInfo);
        model.addAttribute("orderList", orderList);
        model.addAttribute("pageUtil", page);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("directOrderForm", proxySaleOrderForm);
        return "/direct/proxy_sale_credit_order_list";
    }




    /**
     * 将代销订单按照查询条件筛选后，导出Excel
     *
     * @param response           响应对象
     * @param proxySaleOrderForm 查询过滤条件
     */
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletResponse response, ProxySaleOrderForm proxySaleOrderForm) {
        proxySaleOrderService.exportExcel(response, proxySaleOrderForm);
    }

    /**
     * OMS取消订单
     */
    @RequestMapping("cancelOrder")
    @ResponseBody
    public AjaxBase cancelOrder(String remark, String channelOrderNo, String channelId) {
        try {  //OMS取消订单
            proxySaleOrderService.cancelOrder(remark, channelOrderNo, channelId);
            //保存备注日志
            cancelOrderLogService.save(remark, channelOrderNo);
            //记录操作日志
            cancelOrderLogService.SaveOperateLog(remark, channelOrderNo);
            return new AjaxBase(Constants.HTTP_OK, "修改订单成功");
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "修改订单失败，" + e);
        }

    }

    /**
     * 根据渠道订单号查询是否存在操作记录
     *
     * @return
     */
    @RequestMapping("cancelOrderLog")
    @ResponseBody
    public AjaxResult getCancelOrderLog(String channelOrderNo) {
        CancelOrderLog cancelOrderLog = cancelOrderLogService.findCancelOrderLogWithChannelNo(channelOrderNo);
        if (null != cancelOrderLog) {
            return new AjaxResult(Constants.HTTP_OK, cancelOrderLog);
        } else {
            return new AjaxResult(Constants.HTTP_OK, "0");
        }
    }


}
