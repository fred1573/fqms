package com.project.web.finance;

import com.project.bean.vo.AjaxBase;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.entity.finance.FinanceAccountPeriod;
import com.project.entity.finance.FinanceParentOrder;
import com.project.entity.finance.FinanceSpecialOrder;
import com.project.service.finance.BillCheckService;
import com.project.service.finance.FinanceArrearInnService;
import com.project.service.finance.FinanceOrderService;
import com.project.service.finance.PeriodService;
import com.project.utils.FinanceHelper;
import com.project.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 账单核对相关业务的控制器对象
 * Created by sam on 2016/3/18.
 */
@Controller
@RequestMapping(value = "/finance/")
public class FinanceOrderController extends BaseController {
    private static final String CURRENT_PAGE = "finance";
    private static final int PAGE_SIZE = 15;

    @Resource
    private PeriodService periodService;
    @Resource
    private FinanceHelper financeHelper;
    @Resource
    private FinanceOrderService financeOrderService;
    @Resource
    private BillCheckService billCheckService;
    @Resource
    private FinanceArrearInnService financeArrearInnService;
    /**
     * 查询指定结算月份的账单统计情况
     *
     * @param model
     * @param settlementTime 结算周期
     * @return
     */
    @RequestMapping("order/list")
    public String orderList(Model model, String settlementTime) {
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "order");
        List<FinanceAccountPeriod> financeAccountPeriodList = periodService.findAllFinanceAccountPeriod();
        // 查询对账周期
        model.addAttribute("financeAccountPeriodList", financeAccountPeriodList);
        if (StringUtils.isBlank(settlementTime)) {
            settlementTime = financeHelper.getSettlementTime(financeAccountPeriodList);
        }
        List<Map<String, Object>> data = billCheckService.findFinanceChannelSettlement(settlementTime);
        Map<String, Object> total = billCheckService.findTotalChannelSettlement(settlementTime);
        model.addAttribute("data", data);
        model.addAttribute("totalOrder", total);
        model.addAttribute("settlementTime", settlementTime);
        return "/finance/order/order_list";
    }

    /**
     * 进入普通账单核对详情页
     *
     * @param model
     * @param page
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @param auditStatus    核单状态
     * @param priceStrategy  价格模式
     * @param keyWord        模糊搜索关键字（客栈名称/订单号）
     * @param orderStatus    订单状态
     * @return
     */
    @RequestMapping("order/detail")
    public String orderDetail(Model model, Page<FinanceParentOrder> page, Integer channelId, String settlementTime, String auditStatus, Short priceStrategy, Short isBalance, String keyWord, Integer orderStatus) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "order");
        model.addAttribute("subBtn", "normal");
        // 查询对账周期
        Map<String, String> financeTimeMap = financeHelper.getFinanceTimeMap();
        model.addAttribute("financeTimeMap", financeTimeMap);
        page = billCheckService.findFinanceParentOrder(page, channelId, settlementTime, auditStatus, priceStrategy, isBalance, keyWord, orderStatus);
        if (StringUtils.isBlank(settlementTime)) {
            throw new RuntimeException("账期不能为空");
        }
        //获取核单状态下已存在的订单状态
        String status = billCheckService.getExistOrderStatus(channelId, settlementTime, auditStatus);
        Map<String, Object> total = billCheckService.findTotalChannelOrder(channelId, settlementTime, orderStatus);
        model.addAttribute("status", status);
        model.addAttribute(page);
        model.addAttribute("channelId", channelId);
        model.addAttribute("isBalance", isBalance);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("auditStatus", auditStatus);
        model.addAttribute("priceStrategy", priceStrategy);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("totalOrder", total);
        return "/finance/order/order_detail";
    }

    /**
     * 分页查询赔付账单列表
     *
     * @param model
     * @param page
     * @param statusKey      特殊单的状态
     * @param channelId      分销商ID
     * @param settlementTime 账期
     * @param priceStrategy  价格模式
     * @param auditStatus    核单状态
     * @param isBalance      结算状态，0:未结算,1:已结算
     * @param keyWord        搜索关键字
     * @return
     */
    @RequestMapping("order/special/{statusKey}")
    public String specialOrderDetail(Model model, Page<FinanceSpecialOrder> page, @PathVariable("statusKey") String statusKey, Integer channelId, String settlementTime, String auditStatus, Short priceStrategy, Short isBalance, String keyWord) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "order");
        model.addAttribute("subBtn", statusKey);
        page = billCheckService.findSpecialOrderList(page, statusKey, channelId, settlementTime, auditStatus, priceStrategy, isBalance, keyWord);
        model.addAttribute(page);
        String status = FinanceHelper.getStatusByKey(statusKey);
        Map<String, Object> total = billCheckService.findTotalChannelOrder(channelId, settlementTime, Integer.parseInt(status));
        model.addAttribute("totalOrder", total);
        model.addAttribute("channelId", channelId);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("auditStatus", auditStatus);
        model.addAttribute("priceStrategy", priceStrategy);
        model.addAttribute("isBalance", isBalance);
        model.addAttribute("keyWord", keyWord);
        return "/finance/order/" + statusKey + "_order_detail";
    }

    /**
     * 生成进、出账单
     *
     * @return
     */
    @RequestMapping("order/settlement")
    @ResponseBody
    public AjaxBase orderSettlement(String settlementTime) {
        try {
            //清除正常订单的特殊订单
      /*      financeOrderService.cleanSpecialOrder(settlementTime);*/
            // 创建或更新指定账期的客栈结算记录
            financeOrderService.createFinanceInnSettlementList(settlementTime);
            // 创建或更新指定账期的客栈分销商结算记录
            financeOrderService.createFinanceInnChannelSettlementList(settlementTime);
            // 创建或更新指定账期的分销商结算记录
            financeOrderService.createFinanceChannelSettlementList(settlementTime);
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "进、出账单生成失败" + e);
        }
        return new AjaxBase(Constants.HTTP_OK, "进、出账单生成成功");
    }


    /**
     * 根据订单ID查询订单详情
     *
     * @param id
     * @return
     */
    @RequestMapping("order/getOrder")
    @ResponseBody
    public AjaxResult getOrder(String id) {
        try {
            Map<String, Object> dataMap = billCheckService.findBillDetailInfo(id);
            if (dataMap != null) {
                return new AjaxResult(Constants.HTTP_OK, dataMap);
            }
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "获取账单详情失败" + e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_500, "获取账单详情失败");
    }

    /**
     * 修改账单
     *
     * @param jsonData
     * @return
     */
    @RequestMapping("order/updateOrder")
    @ResponseBody
    public AjaxBase updateOrder(String jsonData) {
        try {
            billCheckService.updateOrder(jsonData);
            return new AjaxBase(Constants.HTTP_OK, "更新账单成功");
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "更新账单失败，" + e);
        }
    }

    @RequestMapping("out/levelArrears")
    @ResponseBody
    public AjaxBase levelArrears(String jsonData) {
        try {
            financeArrearInnService.FinanceLevelArrears(jsonData);
            return new AjaxBase(Constants.HTTP_OK, "平账操作成功");
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "平账操作失败:"+e.getMessage());
        }
    }
}
