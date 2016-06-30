package com.project.web.finance;

import com.project.bean.vo.AjaxResult;
import com.project.bean.vo.SpecialOrderVo;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.entity.finance.*;
import com.project.service.finance.*;
import com.project.utils.FinanceHelper;
import com.project.web.BaseController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author frd
 */
@Controller
@RequestMapping("/finance/income")
public class FinanceIncomeController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceIncomeController.class);
    private static final String CURRENT_PAGE = "finance";
    private static final String CURRENT_BTN_NORMAL = "normal";
    private static final String CURRENT_BTN_DEBIT = "debit";
    private static final String CURRENT_BTN_REFUND = "refund";
    private static final String CURRENT_BTN_REPLENISHMENT = "replenishment";
    private static final int PAGE_SIZE = 15;

    @Resource
    private PeriodService periodService;
    @Resource
    private FinanceHelper financeHelper;
    @Resource
    private FinanceOrderService financeOrderService;
    @Resource
    private FinanceManualOrderService financeManualOrderService;
    @Resource
    private FinanceInnChannelSettlementService financeInnChannelSettlementService;
    @Resource
    private FinanceSpecialOrderService financeSpecialOrderService;
    @Resource
    private FinanceIncomeExportService financeIncomeExportService;

    /**
     * 跳转到进账核对列表页面，分页查询渠道结算列表
     *
     * @param model
     * @param page           分页对象
     * @param settlementTime 结算月份
     * @param channelName    渠道名称
     * @param auditStatus    是否核单
     * @param isArrival      是否收到渠道商款项
     * @return
     */
    @RequestMapping("/list")
    public String incomeList(Model model, Page<FinanceChannelSettlement> page, String settlementTime, String channelName, String auditStatus, Boolean isArrival) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "income");
        List<FinanceAccountPeriod> financeAccountPeriodList = periodService.findAllFinanceAccountPeriod();
        // 查询对账周期
        model.addAttribute("financeAccountPeriodList", financeAccountPeriodList);
        if (StringUtils.isBlank(settlementTime)) {
            settlementTime = financeHelper.getSettlementTime(financeAccountPeriodList);
        }
        page = financeOrderService.getFinanceParentOrderByChannel(page, settlementTime, channelName, auditStatus, isArrival, true);
        model.addAttribute(page);
        // 统计全部渠道
        model.addAttribute("settlementCountAllMap", financeOrderService.getFinanceChannelSettlementCount(settlementTime, null));
        // 统计已付款渠道
        model.addAttribute("settlementCountArrivalMap", financeOrderService.findChannelSettlementIncomeAmount(settlementTime));
        // 统计未付款渠道
//        model.addAttribute("settlementCountUnArrivalMap", financeOrderService.getFinanceChannelSettlementCount(settlementTime, false));
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("channelName", channelName);
        model.addAttribute("auditStatus", auditStatus);
        model.addAttribute("isArrival", isArrival);
        return "/finance/income/income_list";
    }

    /**
     * 根据渠道结算对象ID更新渠道结算的实收金额以及备注
     *
     * @param id           渠道结算对象ID
     * @param incomeAmount 实收金额
     * @param remarks      备注
     */
    @RequestMapping("/update")
    @ResponseBody
    public AjaxResult updateIncomeAmount(Integer id, BigDecimal incomeAmount, String remarks) {
        try {
            financeOrderService.updateChannelSettlementIncome(id, incomeAmount, remarks);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "操作失败：" + e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "操作成功");
    }


    /**
     * 查看无订单赔付列表
     *
     * @param page
     * @param channelId
     * @param settlementTime
     * @param orderId
     * @return
     */
    @RequestMapping("/manualOrders")
    public ModelAndView manualOrderList(Page<FinanceManualOrder> page, @RequestParam("channelId") Integer channelId, @RequestParam("settlementTime") String settlementTime,
                                        @RequestParam(value = "orderId", required = false) String orderId) {
        ModelAndView mav = new ModelAndView("finance/income/manual_orders");
        initPage(page, PAGE_SIZE);
        page.setOrder(Page.DESC);
        page.setOrderBy("create_time");
        mav.addObject("currentPage", CURRENT_PAGE);
        mav.addObject("currentBtn", "income");
        mav.addObject("page", financeManualOrderService.list(page, channelId, settlementTime, orderId));
        mav.addObject("amountMap", financeManualOrderService.getManualOrderAmount(channelId, settlementTime, orderId));
        mav.addObject("channelId", channelId);
        mav.addObject("settlementTime", settlementTime);
        return mav;
    }

    /**
     * 获取存在暂收金额的客栈信息
     *
     * @param page
     * @param channelId
     * @param settlementTime
     * @return
     */
    @RequestMapping("/fqTemp")
    public ModelAndView fqTempInnList(Page<Map<String,Object>> page, @RequestParam("channelId") Integer channelId, @RequestParam("channelName") String channelName, @RequestParam("settlementTime") String settlementTime
    ) {
        ModelAndView mav = new ModelAndView("finance/income/fq_temp");
        initPage(page, PAGE_SIZE);
        page.setOrder(Page.DESC);
        page.setOrderBy("inn_id");
        mav.addObject("currentPage", CURRENT_PAGE);
        mav.addObject("currentBtn", "income");
        mav.addObject("channelName", channelName);
        mav.addObject("page", financeInnChannelSettlementService.findFqTempInn(page, channelId, settlementTime));
        mav.addObject("channelId", channelId);
        mav.addObject("settlementTime", settlementTime);
        return mav;
    }

    /**
     * 导出渠道下的暂收详情
     *
     * @param settlementTime
     * @param channelId
     * @return
     */
    @RequestMapping(value = "/fqTemp/export")
    @ResponseBody
    public AjaxResult exportFqTempInn(HttpServletRequest request, String settlementTime, Integer channelId, String channelName) {
        try {
            financeIncomeExportService.exportFqTemp(request, channelId, settlementTime, channelName);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "导出暂收失败" + e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "导出成功");
    }

    /**
     * 添加一条无订单赔付
     */
    @RequestMapping(value = "/manualOrder/add", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult addManualOrder(ManualOrderForm manualOrderForm) {
        try {
            financeManualOrderService.add(manualOrderForm);
        } catch (Exception e) {
            LOGGER.error("添加无订单赔付失败, {}", e.getMessage());
            return new AjaxResult(Constants.HTTP_500, "添加无订单赔付失败, " + e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "");
    }

    /**
     * 编辑无订单赔付
     */
    @RequestMapping(value = "/manualOrder/edit", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult editManualOrder(
            @RequestParam("id") Integer id,
            @RequestParam("orderId") String orderId,
            @RequestParam("refund") BigDecimal refund,
            @RequestParam("remark") String remark) {
        try {
            financeManualOrderService.edit(id, orderId, refund, remark);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "");
    }

    /**
     * 删除一条无订单赔付
     */
    @RequestMapping(value = "/manualOrder/del/{id}", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult deleteManualOrder(@PathVariable("id") Integer id) {
        try {
            financeManualOrderService.delete(id);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "");
    }

    /**
     * 进账二级页面，分销商下以客栈分组的结算数据
     */
    @RequestMapping(value = "/innChannel")
    public ModelAndView incomeInnChannelList(
            Page<FinanceInnChannelSettlement> page,
            @RequestParam("channelId") Integer channelId,
            @RequestParam("settlementTime") String settlementTime,
            @RequestParam(value = "innName", required = false) String innName,
            @RequestParam(value = "channelName", required = false) String channelName) {
        ModelAndView mav = new ModelAndView("finance/income/income_inn_channel_list");
        initPage(page, PAGE_SIZE);
        mav.addObject("currentPage", CURRENT_PAGE);
        mav.addObject("currentBtn", "income");
        mav.addObject("page", financeInnChannelSettlementService.financeInnChannelSettlementWithRequire(page, settlementTime, channelId, innName, null));
        mav.addObject("amountMap", financeInnChannelSettlementService.getInnChannelOrderAmount(settlementTime, channelId, innName));
        mav.addObject("settlementTime", settlementTime);
        mav.addObject("channelId", channelId);
        mav.addObject("channelName", channelName);
        return mav;
    }

    /**
     * 跳转到进账核对详情页面
     *
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    public String incomeDetail(Model model, Page<FinanceParentOrder> page, Integer channelId, String settlementTime, String auditStatus, Short priceStrategy, String channelOrderNo, Integer innId) {
        initPage(page, PAGE_SIZE);
        page.setOrder(Page.DESC);
        page.setOrderBy("order_time");
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "income");
        model.addAttribute("subBtn", CURRENT_BTN_NORMAL);
        page = financeOrderService.findChannelIncomeOrderList(page, innId, channelId, settlementTime, channelOrderNo, auditStatus, null, priceStrategy, true);
        model.addAttribute(page);
        model.addAttribute("orderCount", financeOrderService.getChannelOrderCount(innId, channelId, settlementTime, channelOrderNo, auditStatus, priceStrategy));
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("channelId", channelId);
        model.addAttribute("innId", innId);
        model.addAttribute("channelOrderNo", channelOrderNo);
        model.addAttribute("priceStrategy", priceStrategy);
        model.addAttribute("auditStatus", auditStatus);
        // 查询对账周期
        Map<String, String> financeTimeMap = financeHelper.getFinanceTimeMap();
        model.addAttribute("financeTimeMap", financeTimeMap);
        return "/finance/income/income_detail";
    }

    /**
     * 赔付订单列表
     */
    @RequestMapping("/debit")
    public ModelAndView debitOrders(Page<FinanceSpecialOrder> page, @RequestParam("settlementTime") String settlementTime,
                                    @RequestParam("channelId") Integer channelId,
                                    @RequestParam(value = "auditStatus", required = false) String auditStatus,
                                    @RequestParam(value = "priceStrategy", required = false) Short priceStrategy,
                                    @RequestParam(value = "channelOrderNo", required = false) String channelOrderNo,
                                    @RequestParam("innId") Integer innId) {
        ModelAndView mav = new ModelAndView("/finance/income/income_debit");
        initPage(page, PAGE_SIZE);
        mav.addObject("currentPage", CURRENT_PAGE);
        mav.addObject("currentBtn", "income");
        mav.addObject("subBtn", CURRENT_BTN_DEBIT);
        mav.addObject("orderStatistic", financeSpecialOrderService.debitOrderStatistic(settlementTime, channelId, innId));
        //封装vo对象，以得到父订单数据
        mav.addObject("page", wrapSpecialOrderVO(financeSpecialOrderService.findDebitOrders(page, settlementTime, channelId, innId, channelOrderNo, auditStatus, priceStrategy)));
        mav.addObject("settlementTime", settlementTime);
        mav.addObject("channelId", channelId);
        mav.addObject("innId", innId);
        mav.addObject("channelOrderNo", channelOrderNo);
        mav.addObject("priceStrategy", priceStrategy);
        mav.addObject("auditStatus", auditStatus);
        return mav;
    }

    /**
     * 退款订单列表
     */
    @RequestMapping("/refund")
    public ModelAndView refundOrders(Page<FinanceSpecialOrder> page, @RequestParam String settlementTime,
                                     @RequestParam Integer channelId,
                                     @RequestParam Integer innId,
                                     @RequestParam(value = "channelOrderNo", required = false) String channelOrderNo,
                                     @RequestParam(value = "auditStatus", required = false) String auditStatus,
                                     @RequestParam(value = "priceStrategy", required = false) Short priceStrategy) {
        ModelAndView mav = new ModelAndView("/finance/income/income_refund");
        initPage(page, PAGE_SIZE);
        mav.addObject("currentPage", CURRENT_PAGE);
        mav.addObject("currentBtn", "income");
        mav.addObject("subBtn", CURRENT_BTN_REFUND);
        mav.addObject("orderStatistic", financeSpecialOrderService.refundOrderStatistic(settlementTime, channelId, innId));
        //封装vo对象，以得到父订单数据
        mav.addObject("page", wrapSpecialOrderVO(financeSpecialOrderService.findRefundOrders(page, settlementTime, channelId, innId, channelOrderNo, auditStatus, priceStrategy)));
        mav.addObject("settlementTime", settlementTime);
        mav.addObject("channelId", channelId);
        mav.addObject("innId", innId);
        mav.addObject("channelOrderNo", channelOrderNo);
        mav.addObject("priceStrategy", priceStrategy);
        mav.addObject("auditStatus", auditStatus);
        return mav;
    }

    private Page<SpecialOrderVo> wrapSpecialOrderVO(Page<FinanceSpecialOrder> page) {
        Page<SpecialOrderVo> pageVo = new Page<>();
        initPage(pageVo, PAGE_SIZE);
        pageVo.setTotalCount(page.getTotalCount());
        List<SpecialOrderVo> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(page.getResult())) {
            for (Object obj : page.getResult()) {
                SpecialOrderVo vo = new SpecialOrderVo();
                FinanceSpecialOrder specialOrder = (FinanceSpecialOrder) obj;
                vo.setFinanceSpecialOrder(specialOrder);
                vo.setFinanceParentOrder(specialOrder.getFinanceParentOrder());
                result.add(vo);
            }
        }
        pageVo.setResult(result);
        return pageVo;
    }

    /**
     * 补款订单列表
     */
    @RequestMapping("/replenishment")
    public ModelAndView replenishmentOrders(Page<FinanceSpecialOrder> page, @RequestParam String settlementTime,
                                            @RequestParam Integer channelId,
                                            @RequestParam Integer innId,
                                            @RequestParam(value = "channelOrderNo", required = false) String channelOrderNo,
                                            @RequestParam(value = "auditStatus", required = false) String auditStatus,
                                            @RequestParam(value = "priceStrategy", required = false) Short priceStrategy) {
        ModelAndView mav = new ModelAndView("/finance/income/income_replenishment");
        initPage(page, PAGE_SIZE);
        mav.addObject("currentPage", CURRENT_PAGE);
        mav.addObject("currentBtn", "income");
        mav.addObject("subBtn", CURRENT_BTN_REPLENISHMENT);

        mav.addObject("orderStatistic", financeSpecialOrderService.replenishmentOrderStatistic(settlementTime, channelId, innId));
        mav.addObject("page", wrapSpecialOrderVO(financeSpecialOrderService.findReplenishmentOrders(page, settlementTime, channelId, innId, channelOrderNo, auditStatus, priceStrategy)));
        mav.addObject("settlementTime", settlementTime);
        mav.addObject("channelId", channelId);
        mav.addObject("innId", innId);
        mav.addObject("channelOrderNo", channelOrderNo);
        mav.addObject("priceStrategy", priceStrategy);
        mav.addObject("auditStatus", auditStatus);
        return mav;
    }

    /**
     * 导出客栈结算清单到excel
     */
    @RequestMapping("/export/inns")
    @ResponseBody
    public void exportInns(HttpServletRequest request, @RequestParam String settlementTime, @RequestParam Integer channelId) {
        try {
            financeIncomeExportService.exportInnSettlement(request, settlementTime, channelId);
        } catch (Exception e) {
            LOGGER.error("导出进账结算清单时异常", e);
        }
    }

    /**
     * 异步批量生成渠道,客栈的进账单
     *
     * @param request
     * @param settlementTime
     */
    @ResponseBody
    @RequestMapping("/export/channels")
    public void batchExportOut(HttpServletRequest request, String settlementTime) {
        try {
            financeIncomeExportService.createIncomeChannelFinanceExcel(request, settlementTime);
        } catch (Exception e) {
            LOGGER.error("导出进账结算清单时异常", e);
        }
    }
}
