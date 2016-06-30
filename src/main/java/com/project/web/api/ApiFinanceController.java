package com.project.web.api;

import com.project.bean.finance.BillDetail;
import com.project.bean.vo.AjaxBase;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.service.api.ApiFinanceService;
import com.project.service.finance.FinanceOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 绿番茄对外提供的HTTP接口服务
 * Created by sam on 2016/4/14.
 */
@Controller
@RequestMapping(value = "/api/finance/")
public class ApiFinanceController {
    @Resource
    private ApiFinanceService apiFinanceService;
    @Resource
    private FinanceOrderService financeOrderService;

    /**
     * 查询代销客栈未结算账期结算信息
     *
     * @param appKey 用于校验请求是否合法
     * @param innId  PMS客栈ID
     * @return
     */
    @RequestMapping(value = "settlementInfo", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult getSettlementInfo(String appKey, Integer innId) {
        if (!apiFinanceService.checkInnId(innId, appKey)) {
            return new AjaxResult(Constants.HTTP_400, "非法请求");
        }
        return new AjaxResult(Constants.HTTP_OK, apiFinanceService.getInnUnSettlementInfo(innId));
    }


    /**
     * 查询代销客栈往期对账列表
     *
     * @param appKey   用于校验请求是否合法
     * @param innId    PMS客栈ID
     * @param pageSize 页容量
     * @param pageNo   当前页数
     * @return
     */
    @RequestMapping(value = "billList", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult getBillList(String appKey, Integer innId, Integer pageSize, Integer pageNo) {
        if (!apiFinanceService.checkInnId(innId, appKey)) {
            return new AjaxResult(Constants.HTTP_400, "非法请求");
        }
        try {
            Map<String, Object> map = apiFinanceService.findInnSettlementList(innId, pageSize, pageNo);
            return new AjaxResult(Constants.HTTP_OK, map);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "获取客栈历史结算列表失败:" + e);
        }
    }

    /**
     * 查询代销客栈指定账期账单详情
     *
     * @param appKey         用于校验请求是否合法
     * @param innId          PMS客栈ID
     * @param settlementTime 账期
     * @param priceStrategy  价格模式
     * @param billType       账单类型 1：正常订单，2：赔付订单，3：退款订单
     * @param pageSize       页容量
     * @param pageNo         当前页数
     * @return
     */
    @RequestMapping(value = "billDetail", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult getBillDetail(String appKey, Integer innId, String settlementTime, Short priceStrategy, Integer billType, Integer pageSize, Integer pageNo) {
        if (!apiFinanceService.checkInnId(innId, appKey)) {
            return new AjaxResult(Constants.HTTP_400, "非法请求");
        }
        try {
            Map<String, Object> map = apiFinanceService.findApiParentOrder(innId, settlementTime, priceStrategy, billType, pageSize, pageNo);
            return new AjaxResult(Constants.HTTP_OK, map);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "获取客栈历史账单列表失败:" + e);
        }

    }


    /**
     * 查询代销客栈指定账期的账单详情
     *
     * @param appKey
     * @param innId
     * @param settlementTime
     * @return
     */
    @RequestMapping(value = "exportBill", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult exportBill(String appKey, Integer innId, String settlementTime) {
        if (!apiFinanceService.checkInnId(innId, appKey)) {
            return new AjaxResult(Constants.HTTP_400, "非法请求");
        }
        try {
            BillDetail billDetail1 = apiFinanceService.findBillDetail(innId, settlementTime);
            return new AjaxResult(Constants.HTTP_OK, billDetail1);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "获取客栈历史账单列表失败:" + e);
        }
    }

    /**
     * 代销客栈确认本账期账单
     *
     * @param innId
     * @param appKey
     * @param settlementTime
     * @return
     */
    @RequestMapping(value = "billConfirm", method = RequestMethod.POST)
    @ResponseBody
    public AjaxBase billConfirm(Integer innId, String appKey, String settlementTime) {
        if (!apiFinanceService.checkInnId(innId, appKey)) {
            return new AjaxBase(Constants.HTTP_400, "非法请求");
        }
        try {
            financeOrderService.updateFinanceInnSettlementStatus(innId, settlementTime);
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "确认失败:" + e);
        }
        return new AjaxBase(Constants.HTTP_OK, "确认成功");
    }

}


