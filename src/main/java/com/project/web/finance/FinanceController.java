package com.project.web.finance;

import com.project.bean.finance.AjaxChannelReconciliation;
import com.project.bean.finance.AjaxInnOrder;
import com.project.bean.finance.AjaxInnSettlement;
import com.project.bean.vo.AjaxBase;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.entity.finance.*;
import com.project.service.direct.ProxySaleOrderService;
import com.project.service.finance.FinanceArrearInnService;
import com.project.service.finance.FinanceInnChannelSettlementService;
import com.project.service.finance.FinanceOrderService;
import com.project.service.finance.PeriodService;
import com.project.service.ota.OtaInfoService;
import com.project.utils.FinanceHelper;
import com.project.utils.NumberUtil;
import com.project.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用于财务对账的控制器
 * Created by 番茄桑 on 2015/8/13.
 */
@Controller
@RequestMapping(value = "/finance/")
public class FinanceController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceController.class);
    private static final String CURRENT_PAGE = "finance";
    private static final int PAGE_SIZE = 15;
    @Resource
    private FinanceHelper financeHelper;
    @Resource
    private FinanceOrderService financeOrderService;
    @Resource
    private OtaInfoService otaInfoService;
    @Resource
    private PeriodService periodService;
    @Resource
    private ProxySaleOrderService proxySaleOrderService;
    @Resource
    private FinanceInnChannelSettlementService financeInnChannelSettlementService;
    @Resource
    private FinanceArrearInnService financeArrearInnService;

    /**
     * 根据渠道ID，确认渠道
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/channel/arrival")
    @ResponseBody
    public AjaxResult channelArrival(Integer id, Integer channelId, String settlementTime) {
        try {
            financeOrderService.updateFinanceChannelSettlementStatus(id, channelId, settlementTime);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "操作失败：" + e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "操作成功");
    }


    /**
     * 上传对账单
     *
     * @param file
     */
    @RequestMapping("/upload/income")
    @ResponseBody
    public AjaxChannelReconciliation uploadIncome(MultipartFile file, Integer channelId, String settlementTime) {
        try {
            return financeOrderService.channelReconciliation(file, channelId, settlementTime);
        } catch (Exception e) {
            new AjaxChannelReconciliation(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxChannelReconciliation(Constants.HTTP_500, "上传失败");
    }

    /**
     * 跳转到出账核对列表页面
     *
     * @param model
     * @param page             分页对象
     * @param innName          客栈名称（支持模糊匹配）
     * @param settlementTime   结算周期
     * @param confirmStatus    是否确认
     * @param settlementStatus 结算状态
     * @param isTagged         是否标记
     * @param isMatch          账实是否相符
     * @return
     */
    @RequestMapping("out/list")
    public String outList(Model model, Page<FinanceInnSettlement> page, String innName, String settlementTime, String confirmStatus, String settlementStatus, Boolean isTagged, Boolean isMatch, String status) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "out");
        List<FinanceAccountPeriod> financeAccountPeriodList = periodService.findAllFinanceAccountPeriod();
        // 查询对账周期
        if (StringUtils.isBlank(settlementTime)) {
            settlementTime = financeHelper.getSettlementTime(financeAccountPeriodList);
        }
        page = financeOrderService.findFinanceInnSettlementList(page, innName, settlementTime, confirmStatus, settlementStatus, isTagged, true, isMatch, status);
        model.addAttribute(page);
        // 统计该结算周期的全部收入
        model.addAttribute("allMap", financeOrderService.getFinanceInnSettlementCount(settlementTime, null, status));
        // 统计已付款渠道
        model.addAttribute("unSettlementMap", financeOrderService.getFinanceInnSettlementCount(settlementTime, settlementStatus, status));
        // 通过OMS接口，获取渠道对象集合
        Map<String, String> otaMap = proxySaleOrderService.getChannelInfoFromOMS();

        // 统计该结算周期未结算
        model.addAttribute("innName", innName);
        model.addAttribute("otaMap", otaMap);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("confirmStatus", confirmStatus);
        model.addAttribute("settlementStatus", settlementStatus);
        model.addAttribute("isTagged", isTagged);
        model.addAttribute("isMatch", isMatch);
        model.addAttribute("financeAccountPeriodList", financeAccountPeriodList);
        model.addAttribute("status", status);
        return "/finance/out/out_list";
    }

    //跳转到特殊订单客栈页面
    @RequestMapping("out/special/list")
    public String outSpecialList(Model model, Page<FinanceInnSettlement> page, String innName, String settlementTime, String confirmStatus, String settlementStatus, Boolean isTagged, Boolean isMatch, String status) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "out");
        List<FinanceAccountPeriod> financeAccountPeriodList = periodService.findAllFinanceAccountPeriod();
        // 查询对账周期
        if (StringUtils.isBlank(settlementTime)) {
            settlementTime = financeHelper.getSettlementTime(financeAccountPeriodList);
        }
        page = financeOrderService.findFinanceInnSettlementList(page, innName, settlementTime, confirmStatus, settlementStatus, isTagged, true, isMatch, status);
        model.addAttribute(page);
        // 统计该结算周期的全部收入
        model.addAttribute("allMap", financeOrderService.getFinanceInnSettlementCount(settlementTime, null, status));
        model.addAttribute("innName", innName);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("confirmStatus", confirmStatus);
        model.addAttribute("settlementStatus", settlementStatus);
        model.addAttribute("isTagged", isTagged);
        model.addAttribute("isMatch", isMatch);
        model.addAttribute("financeAccountPeriodList", financeAccountPeriodList);
        model.addAttribute("status", status);

        return "/finance/out/special/out_special_balance";
    }

    //跳转到延期客栈页面
    @RequestMapping("out/delay/list")
    public String outDelayList(Model model, Page<FinanceInnSettlement> page, String innName, String
            settlementTime, String confirmStatus, String settlementStatus, Boolean isTagged, Boolean isMatch, String status) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "out");
        List<FinanceAccountPeriod> financeAccountPeriodList = periodService.findAllFinanceAccountPeriod();
        // 查询对账周期
        if (StringUtils.isBlank(settlementTime)) {
            settlementTime = financeHelper.getSettlementTime(financeAccountPeriodList);
        }
        page = financeOrderService.findFinanceInnSettlementList(page, innName, settlementTime, confirmStatus, settlementStatus, isTagged, true, isMatch, status);
        model.addAttribute(page);
        // 统计该结算周期的全部收入
        model.addAttribute("allMap", financeOrderService.getFinanceInnSettlementCount(settlementTime, null, status));
        model.addAttribute("unBalanceMap", financeOrderService.selectUnbalanceFinanceInnSettlementCount(settlementTime, status).get(0));
        model.addAttribute("innName", innName);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("confirmStatus", confirmStatus);
        model.addAttribute("settlementStatus", settlementStatus);
        model.addAttribute("isTagged", isTagged);
        model.addAttribute("isMatch", isMatch);
        model.addAttribute("financeAccountPeriodList", financeAccountPeriodList);
        model.addAttribute("status", status);
        return "/finance/out/out_delay_balance";
    }


    /**
     * 根据客栈ID，确认已经结算
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/inn/settlement")
    @ResponseBody
    public AjaxResult innSettlement(Integer id, String settlementStatus, String message, String settlementTime, Integer innId) {
        try {
            financeOrderService.updateInnSettlementStatus(id, settlementStatus,settlementTime,innId);
            if (message.equals("1")) {
                financeOrderService.sendErrorMessage(innId, settlementTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new AjaxResult(Constants.HTTP_500, "操作失败");
        }
        return new AjaxResult(Constants.HTTP_OK, "操作成功");
    }

    /**
     * 根据ID和是否标注，修改客栈的标注状态
     *
     * @param id
     * @param isTagged
     * @return
     */
    @RequestMapping(value = "/inn/tag")
    @ResponseBody
    public AjaxResult innTag(Integer id, Boolean isTagged) {
        try {
            financeOrderService.updateFinanceInnSettlementTag(id, isTagged);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "操作失败");
        }
        return new AjaxResult(Constants.HTTP_OK, "操作成功");
    }

    /**
     * 导出客栈结算总表
     *
     * @param response
     * @param exportSettlementTime
     */
    @RequestMapping("/export/out")
    public void exportOut(HttpServletResponse response, Integer exportInnId, String exportSettlementTime) {
        try {
            financeOrderService.exportInnOrder(response, exportInnId, exportSettlementTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步批量生成客栈的出账单
     *
     * @param request
     * @param settlementTime
     */
    @ResponseBody
    @RequestMapping("batch/export/out")
    public void batchExportOut(HttpServletRequest request, String settlementTime, String status) {
        try {
            financeOrderService.batchExportInnOrder(request, settlementTime, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到文件下载页面
     *
     * @return
     */
    @RequestMapping("download/file")
    public String fileList(Model model, HttpServletRequest request) {
        File[] fileList = financeOrderService.getFileList(request);
        model.addAttribute("fileList", fileList);
        return "/finance/file_list";
    }

    /**
     * 删除指定文件
     *
     * @param request
     * @param fileName
     * @return
     */
    @ResponseBody
    @RequestMapping("delete/file")
    public AjaxBase deleteFile(HttpServletRequest request, String fileName) {
        try {
            financeOrderService.removeFile(request, fileName);
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "删除失败：" + e);
        }
        return new AjaxBase(Constants.HTTP_OK, "删除成功");
    }

    /**
     * 跳转到出账核对详情页面
     *
     * @param model
     * @return
     */
    @RequestMapping("out/detail")
    public String outDetail(Model model, Page<FinanceParentOrder> page, Integer innId, String settlementTime, Integer channelId, String channelOrderNo, String auditStatus, Boolean isArrival, Short priceStrategy, String status) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "out");
        page = financeOrderService.findChannelIncomeOrderList(page, innId, channelId, settlementTime, channelOrderNo, auditStatus, isArrival, priceStrategy, true);
        model.addAttribute(page);
        model.addAttribute("channelList", otaInfoService.list());
        model.addAttribute("innOrderCount", financeOrderService.getInnOrderCount(innId, channelId, settlementTime, channelOrderNo, auditStatus, priceStrategy, isArrival));
        model.addAttribute("innId", innId);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("channelId", channelId);
        model.addAttribute("channelOrderNo", channelOrderNo);
        model.addAttribute("auditStatus", auditStatus);
        model.addAttribute("isArrival", isArrival);
        model.addAttribute("priceStrategy", priceStrategy);
        // 查询对账周期
        Map<String, String> financeTimeMap = financeHelper.getFinanceTimeMap();
        model.addAttribute("financeTimeMap", financeTimeMap);
        model.addAttribute("status", status);
        return "/finance/out/out_detail";
    }

    /**
     * 按渠道统计客栈订单信息
     *
     * @param model
     * @param innId
     * @param settlementTime
     * @return
     */
    @RequestMapping("out/channel/detail")
    public String statisticOrderWithChannel(Model model, Integer innId, String settlementTime, String status, Boolean isMatch) {
        //按渠道统计客栈订单信息

        if (null == innId || null == settlementTime) {
            throw new RuntimeException("客栈ID，结算账期不能为空");
        }
        List<FinanceInnChannelSettlement> mapList = financeInnChannelSettlementService.selectChannelOrder(innId, settlementTime, isMatch);
        List<Map<String, Object>> totalMap = financeInnChannelSettlementService.statisticChannelOrderTotal(innId, settlementTime);
        model.addAttribute("mapList", mapList);
        model.addAttribute("total", totalMap.get(0));
        model.addAttribute("innId", innId);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("status", status);
        return "/finance/out/out_channel_detail";
    }

    /**
     * 按渠道统计客栈订单信息
     *
     * @param model
     * @param innId
     * @param settlementTime
     * @return
     */
    @RequestMapping("out/special/channel/detail")
    public String statisticSpecialOrderWithChannel(Model model, Integer innId, String settlementTime, String status) {
        //按渠道统计客栈订单信息

        if (null == innId || null == settlementTime) {
            throw new RuntimeException("客栈ID，结算账期不能为空");
        }
        List<FinanceInnChannelSettlement> mapList = financeInnChannelSettlementService.selectChannelOrder(innId, settlementTime, null);
        List<Map<String, Object>> totalMap = financeInnChannelSettlementService.statisticChannelOrderTotal(innId, settlementTime);
        model.addAttribute("mapList", mapList);
        model.addAttribute("total", totalMap.get(0));
        model.addAttribute("innId", innId);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("status", status);
        return "/finance/out/special/out_special_channel_detail";
    }

    /**
     * 挂账结算分销商统计
     *
     * @return
     */
    @RequestMapping("out/arrears/channel")
    public String statisticArrearsChannel(Model model, Integer innId, String settlementTime, String status, Boolean isMatch, String arrearsStatus) {
        if (null == innId || null == settlementTime) {
            throw new RuntimeException("客栈ID，结算账期不能为空");
        }
        List<FinanceInnChannelSettlement> mapList = financeInnChannelSettlementService.selectChannelOrder(innId, settlementTime, isMatch);
        Map<String, Object> totalMap = financeInnChannelSettlementService.statisticArrearsChannel(settlementTime, innId, isMatch);
        model.addAttribute("mapList", mapList);
        model.addAttribute("total", totalMap);
        model.addAttribute("innId", innId);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("status", status);
        model.addAttribute("arrearsStatus", arrearsStatus);
        return "/finance/out/arrears/out_arrears_channel";

    }

    /**
     * 延期客栈渠道明细
     *
     * @param model
     * @param innId
     * @param settlementTime
     * @return
     */
    @RequestMapping("out/delay/channel")
    public String statisticDelayOrderWithChannel(Model model, Integer innId, String settlementTime, String status) {
        FinanceInnSettlement financeInnSettlement = financeOrderService.findFinanceInnSettlement(innId, settlementTime);
        boolean b = financeInnSettlement.getIsSpecial();
        //判断延期客栈结算是否含有特殊订单
        if (financeInnSettlement.getIsSpecial()) {
            //特殊结算
            if (null == innId || null == settlementTime) {
                throw new RuntimeException("客栈ID，结算账期不能为空");
            }
            List<FinanceInnChannelSettlement> mapList = financeInnChannelSettlementService.selectChannelOrder(innId, settlementTime, null);
            List<Map<String, Object>> totalMap = financeInnChannelSettlementService.statisticChannelOrderTotal(innId, settlementTime);
            model.addAttribute("mapList", mapList);
            model.addAttribute("total", totalMap.get(0));
            model.addAttribute("innId", innId);
            model.addAttribute("settlementTime", settlementTime);
            model.addAttribute("status", status);
            return "/finance/out/special/out_special_channel_detail";
        } else {
            //正常结算
            if (null == innId || null == settlementTime) {
                throw new RuntimeException("客栈ID，结算账期不能为空");
            }
            List<FinanceInnChannelSettlement> mapList = financeInnChannelSettlementService.selectChannelOrder(innId, settlementTime, null);
            List<Map<String, Object>> totalMap = financeInnChannelSettlementService.statisticChannelOrderTotal(innId, settlementTime);
            model.addAttribute("mapList", mapList);
            model.addAttribute("total", totalMap.get(0));
            model.addAttribute("innId", innId);
            model.addAttribute("status", status);
            model.addAttribute("settlementTime", settlementTime);
            return "/finance/out/out_channel_detail";
        }
    }

    /**
     * 按渠道统计特殊结算客栈订单
     * @param model
     * @param innId
     * @param settlementTime
     * @return
     */
    /**
     * 发送客栈的结算账单
     *
     * @param settlementTime
     * @return
     */
    @RequestMapping(value = "/inn/sendBill")
    @ResponseBody
    public AjaxResult sendBill(String settlementTime) {
        try {
            financeOrderService.sendInnBill(settlementTime);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "发送账单【" + settlementTime + "】失败:" + e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "发送【" + settlementTime + "】账单成功");
    }

    /**
     * 一键结算
     *
     * @param settlementTime
     * @return
     */
    @RequestMapping(value = "/inn/batchSettlement")
    @ResponseBody
    public AjaxResult batchSettlement(String settlementTime) {
        try {
            financeOrderService.batchSettlement(settlementTime);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "一键结算【" + settlementTime + "】的客栈失败:" + e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "一键结算【" + settlementTime + "】的客栈成功");
    }

    /**
     * 跳转到操作记录列表页面
     *
     * @param model
     * @return
     */
    @RequestMapping("operate/list")
    public String operateList(Model model, Page<FinanceOperationLog> page, String keyWord, String settlementTime, String startDate, String endDate, String operateType) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "operate");
        page = financeOrderService.findFinanceOperationLogList(page, keyWord, settlementTime, startDate, endDate, operateType);
        model.addAttribute(page);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("operateType", operateType);
        // 查询对账周期
        Map<String, String> financeTimeMap = financeHelper.getFinanceTimeMap();
        model.addAttribute("financeTimeMap", financeTimeMap);
        return "/finance/operate_list";
    }

    /**
     * 为PMS提供的接口，根据客栈ID查询该客栈的结算记录列表
     *
     * @param innId
     * @return
     */
    @RequestMapping(value = "/api/pms/list", method = RequestMethod.POST)
    @ResponseBody
    public AjaxBase getInnSettlementTimeList(String appKey, Integer innId, Integer pageSize, Integer pageNo) {
//        String property = System.getProperty("spring.profiles.active");
//        // 非生产环境处理跨域
//        if (!"production".equals(property)) {
//            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
//        }
        LOGGER.info("appKey=" + appKey + ",innId=" + innId + ",pageSize=" + pageSize + ",pageNo=" + pageNo);
        if (!financeOrderService.checkInnId(innId, appKey)) {
            LOGGER.error("appKey验证失败，innId=" + innId + ",appKey=" + appKey + "。");
            return new AjaxBase(Constants.HTTP_400, "非法请求");
        }
        try {
            Page<FinanceInnSettlement> financeInnSettlementList = financeOrderService.getFinanceInnSettlementList(innId, pageSize, pageNo);
            if (financeInnSettlementList != null) {
                List<FinanceInnSettlement> list = (List<FinanceInnSettlement>) financeInnSettlementList.getResult();
                return new AjaxInnSettlement(Constants.HTTP_OK, "获取客栈历史结算列表成功", list, financeInnSettlementList.getTotalCount());
            }
        } catch (Exception e) {
            new AjaxInnSettlement(Constants.HTTP_500, "获取客栈历史结算列表失败");
        }
        return new AjaxInnSettlement(Constants.HTTP_400, "获取客栈历史结算列表失败");
    }

    /**
     * 为PMS提供的接口，根据客栈ID和结算月份查询该客栈的订单详情记录
     *
     * @param innId
     * @param settlementTime
     * @return
     */
    @RequestMapping(value = "/api/pms/detail", method = RequestMethod.POST)
    @ResponseBody
    public AjaxBase getInnOrderList(String appKey, Integer innId, String settlementTime, Integer pageSize, Integer pageNo, Short priceStrategy, Boolean isPage) {
//        String property = System.getProperty("spring.profiles.active");
//        // 非生产环境处理跨域,线上测试也不加
//        if (!"production".equals(property) && !"staging".equals(property)) {
//            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
//        }
        if (!financeOrderService.checkInnId(innId, appKey)) {
            LOGGER.error("appKey验证失败，innId=" + innId + ",appKey=" + appKey + "。");
            return new AjaxBase(Constants.HTTP_400, "非法请求");
        }
        try {
            Page<FinanceParentOrder> innOrderList = financeOrderService.findInnOrderList(innId, settlementTime, pageSize, pageNo, priceStrategy, isPage);
            if (innOrderList != null) {
                List<FinanceParentOrder> list = (List<FinanceParentOrder>) innOrderList.getResult();
                // 由于PMS展示时，佣金是番茄收入+渠道收入，所以对佣金的值进行封装，用订单金额-客栈结算金额
                for (FinanceParentOrder financeParentOrder : list) {
                    BigDecimal totalAmount = NumberUtil.wrapNull(financeParentOrder.getTotalAmount());
                    BigDecimal innSettlementAmount = NumberUtil.wrapNull(financeParentOrder.getInnSettlementAmount());
                    financeParentOrder.setFqSettlementAmount(totalAmount.subtract(innSettlementAmount));
                }
                return new AjaxInnOrder(Constants.HTTP_OK, "获取客栈订单详情列表成功", list, innOrderList.getTotalCount());
            }
        } catch (Exception e) {
            return new AjaxInnOrder(Constants.HTTP_500, "获取客栈订单详情列表失败");
        }
        return new AjaxInnOrder(Constants.HTTP_400, "获取客栈订单详情列表失败");
    }

    /**
     * 为PMS提供的接口，根据客栈ID和结算月份查询该客栈的订单详情记录
     *
     * @param innId
     * @param settlementTime
     * @return
     */
    @RequestMapping(value = "/api/pms/confirm", method = RequestMethod.POST)
    @ResponseBody
    public AjaxBase innConfirm(Integer innId, String appKey, String settlementTime) {
//        String property = System.getProperty("spring.profiles.active");
//        // 非生产环境处理跨域
//        if (!"production".equals(property)) {
//            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
//        }
        if (!financeOrderService.checkInnId(innId, appKey)) {
            LOGGER.error("appKey验证失败，innId=" + innId + ",appKey=" + appKey + "。");
            return new AjaxBase(Constants.HTTP_400, "非法请求");
        }
        try {
            financeOrderService.updateFinanceInnSettlementStatus(innId, settlementTime);
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "确认失败");
        }
        return new AjaxBase(Constants.HTTP_OK, "确认成功");
    }

    /**
     * 用于更新指定账期的订单
     */
    @RequestMapping("grab")
    @ResponseBody
    public AjaxBase grab(String settlementTime) {
        // 获取结算月份
        LOGGER.info("开始执行" + settlementTime + "抓取结算订单的定时任务");
        List<FinanceParentOrder> financeOrderFromOMS = financeHelper.getCheckOutFinanceOrderFromOMS(settlementTime);
        if (!CollectionUtils.isEmpty(financeOrderFromOMS)) {
            for (FinanceParentOrder financeParentOrder : financeOrderFromOMS) {
                financeParentOrder.setSettlementTime(settlementTime);
                financeOrderService.repairFinanceOrder(financeParentOrder);
            }
        }
        LOGGER.info(settlementTime + "抓取结算订单的定时任务执行完成");
        return new AjaxBase(Constants.HTTP_OK, "账期【" + settlementTime + "】的订单同步完成");
    }

    /**
     * 生成指定账期的客栈渠道结算数据
     *
     * @param settlementTime
     * @return
     */
    @RequestMapping("createInnChannel")
    @ResponseBody
    public AjaxBase createInnChannel(String settlementTime) {
        LOGGER.info("开始执行" + settlementTime + "生成客栈渠道对账数据");
        financeOrderService.createFinanceInnChannelSettlementList(settlementTime);
        LOGGER.info(settlementTime + "生成客栈渠道对账数据执行完成");
        return new AjaxBase(Constants.HTTP_OK, "账期【" + settlementTime + "】的订单同步完成");
    }

    /**
     * 按渠道操作客栈
     *
     * @param model
     * @param page
     * @param innName
     * @param settlementTime
     * @param channelId
     * @return
     */
    @RequestMapping("inn/channelSettlement")
    public String getInnChannelSettlement(Model model, Page<FinanceInnChannelSettlement> page, String innName, String settlementTime, Integer channelId, Boolean isMatch) {
        initPage(page, PAGE_SIZE);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "out");
        Map<String, Object> statistic = financeInnChannelSettlementService.findInnChannelSettlementStatus(settlementTime, channelId);
        Page<FinanceInnChannelSettlement> Page = financeInnChannelSettlementService.financeInnChannelSettlementWithRequire(page, settlementTime, channelId, innName, isMatch);
        model.addAttribute(Page);
        // 通过OMS接口，获取渠道对象集合
        Map<String, String> otaMap = proxySaleOrderService.getChannelInfoFromOMS();
        model.addAttribute("channelId", channelId);
        model.addAttribute("otaMap", otaMap);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("isMatch", isMatch);
        model.addAttribute("innName", innName);
        model.addAttribute("allMap", statistic);
        return "/finance/out/out_channel_list";
    }

    /**
     * 自动填充实付金额
     *
     * @param settlementTime
     * @param innId
     * @return
     */
    @RequestMapping("inn/fill")
    public String fillRealPay(Model model, String settlementTime, Integer innId, RedirectAttributes redirectAttributes, String status, String ret) {
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("channelId", innId);
        model.addAttribute("status", status);
        financeInnChannelSettlementService.fillRealPay(settlementTime, innId);
        redirectAttributes.addAttribute("innId", innId);
        redirectAttributes.addAttribute("settlementTime", settlementTime);
        if (null != ret && ret.equals("arrears")) {
            return "redirect:/finance/out/arrears/channel";
        }
        if (null != ret && ret.equals("special")) {
            return "redirect:/finance/out/special/channel/detail";
        }
        return "redirect:/finance/out/channel/detail";
    }

    /**
     * 导出渠道客栈信息
     *
     * @param response
     * @param settlementTime
     * @param channelId
     */
    @RequestMapping("out/channel")
    public void exportWithChannel(HttpServletResponse response, String settlementTime, Integer channelId) {
        financeInnChannelSettlementService.exportInnOrder(response, channelId, settlementTime);
    }

    /**
     * 异步导出渠道客栈信息
     *
     * @param request
     * @param settlementTime
     * @param channelId
     */
    @ResponseBody
    @RequestMapping("batch/out/channel")
    public void batchExportWithChannel(HttpServletRequest request, String settlementTime, Integer channelId, String channelName) {
        try {
            financeInnChannelSettlementService.batchExportInnOrder(request, channelId, settlementTime, channelName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置实付金额
     *
     * @param jsonData
     * @return
     */
    @RequestMapping("inn/updatePayment")
    @ResponseBody
    public AjaxBase updatePayment(String jsonData) {
        try {
            financeInnChannelSettlementService.updateRealPay(jsonData);
            return new AjaxBase(Constants.HTTP_OK, "更新实付金额成功");
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "更新实付金额失败，原因：" + e);
        }
    }

    /**
     * 导出批量代付表
     *
     * @param settlementTime
     */
    @RequestMapping("export/pay")
    public void exportWithPay(HttpServletResponse response, String settlementTime) {
        if (StringUtils.isBlank(settlementTime)) {
            throw new RuntimeException("账期不能为空");
        }
        try {
            financeInnChannelSettlementService.createFinanceExcelWithPay(response, settlementTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 特殊结算下的普通订单
     *
     * @param page
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    @RequestMapping("out/specialBalance/normal")
    public String specialBalanceNormal(Model model, Page<FinanceParentOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String status, String type, String statusType, String arrearsStatus) {

        Page<FinanceParentOrder> list = financeOrderService.findSpecialInnNormalOrder(page, innId, channelId, settlementTime, channelOrderNo);
        model.addAttribute("page", page);
        List<Map<String, Object>> mapList = financeOrderService.selectSpecialInnNormalOrderCount(innId, channelId, settlementTime, channelOrderNo);
        model.addAttribute("innOrderCount", mapList.get(0));
        model.addAttribute("innId", innId);
        model.addAttribute("channelId", channelId);
        model.addAttribute("status", status);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("statusType", statusType);
        model.addAttribute("arrearsStatus", arrearsStatus);
        if (StringUtils.isNotBlank(type)) {
            if (type.equals("arrears")) {
                return "finance/out/arrears/out_arrears_normal";
            }
        }
        return "finance/out/special/out_special_normal";
    }

    /**
     * 特殊结算下的赔付订单
     *
     * @param model
     * @param page
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @param status
     * @return
     */
    @RequestMapping("out/specialBalance/recovery")
    public String specialBalanceRecovery(Model model, Page<FinanceSpecialOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String status, String contactsStatus, String type, String statusType, String arrearsStatus) {
        Page<FinanceSpecialOrder> pageList = financeOrderService.findFinanceSpecialOrder(page, innId, channelId, settlementTime, channelOrderNo, statusType, contactsStatus);
        List<Map<String, Object>> mapList = financeOrderService.selectSpecialInnRecoveryOrderCount(innId, channelId, settlementTime, channelOrderNo);
        model.addAttribute("page", pageList);
        model.addAttribute("innOrderCount", mapList.get(0));
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("innId", innId);
        model.addAttribute("channelId", channelId);
        model.addAttribute("statusType", statusType);
        model.addAttribute("arrearsStatus", arrearsStatus);
        model.addAttribute("status", status);
        if (StringUtils.isNotBlank(type)) {
            if (type.equals("arrears")) {
                return "finance/out/arrears/out_arrears_payment";
            }
        }
        return "finance/out/special/out_special_payment";
    }

    /**
     * 特殊结算下的退款订单
     *
     * @param model
     * @param page
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @param status
     * @return
     */
    @RequestMapping("out/specialBalance/refund")
    public String specialBalanceRefund(Model model, Page<FinanceSpecialOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String status, String contactsStatus, String type, String statusType, String arrearsStatus) {
        Page<FinanceSpecialOrder> pageList = financeOrderService.findFinanceSpecialOrder(page, innId, channelId, settlementTime, channelOrderNo, statusType, contactsStatus);
        List<Map<String, Object>> mapList = financeOrderService.selectSpecialInnRefundOrderCount(innId, channelId, settlementTime, channelOrderNo, contactsStatus);
        model.addAttribute("page", pageList);
        model.addAttribute("innOrderCount", mapList.get(0));
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("innId", innId);
        model.addAttribute("channelId", channelId);
        model.addAttribute("status", status);
        model.addAttribute("statusType", statusType);
        model.addAttribute("arrearsStatus", arrearsStatus);
        if (StringUtils.isNotBlank(type)) {
            if (type.equals("arrears")) {
                return "finance/out/arrears/out_arrears_refund";
            }
        }
        return "finance/out/special/out_special_refund";
    }

    /**
     * 特殊结算下的退款订单
     *
     * @param model
     * @param page
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @param status
     * @return
     */
    @RequestMapping("out/specialBalance/replenishment")
    public String specialBalanceReplenishment(Model model, Page<FinanceSpecialOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String status, String contactsStatus, String type, String statusType, String arrearsStatus) {
        Page<FinanceSpecialOrder> pageList = financeOrderService.findFinanceSpecialOrder(page, innId, channelId, settlementTime, channelOrderNo, statusType, contactsStatus);
        List<Map<String, Object>> mapList = financeOrderService.selectSpecialInnReplenishmentOrderCount(innId, channelId, settlementTime, channelOrderNo);
        model.addAttribute("page", pageList);
        model.addAttribute("innOrderCount", mapList.get(0));
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("innId", innId);
        model.addAttribute("status", status);
        model.addAttribute("channelId", channelId);
        model.addAttribute("statusType", statusType);
        model.addAttribute("arrearsStatus", arrearsStatus);
        if (StringUtils.isNotBlank(type)) {
            if (type.equals("arrears")) {
                return "finance/out/arrears/out_arrears_replenishment";
            }
        }
        return "finance/out/special/out_special_replenishment";
    }

    /**
     * 查询挂账客栈
     *
     * @param settlementTime
     * @param page
     * @param arrearsStatus
     * @return
     */
    @RequestMapping("out/arrears")
    public String findArrearFinanceInnSettlement(Model model, String
            settlementTime, Page<FinanceInnSettlement> page, String status, String arrearsStatus, String innName) {
        if (StringUtils.isNotBlank(arrearsStatus) && arrearsStatus.equals("4")) {
            page = financeArrearInnService.getTotalArrearsPage(page, settlementTime, FinanceInnSettlement.ARREARS_TAG, innName);
            model.addAttribute("page", page);
            model.addAttribute("allMap", financeOrderService.selectTotalArrearFinanceInnSettlement(settlementTime, FinanceInnSettlement.ARREARS_TAG));
        } else {
            page = financeOrderService.findArrearFinanceInnSettlement(settlementTime, page, arrearsStatus, innName);
            model.addAttribute("page", page);
            model.addAttribute("allMap", financeOrderService.selectArrearFinanceInnSettlement(settlementTime, arrearsStatus));
        }

        List<FinanceAccountPeriod> financeAccountPeriodList = periodService.findAllFinanceAccountPeriod();
        // 查询对账周期
        if (StringUtils.isBlank(settlementTime)) {
            settlementTime = financeHelper.getSettlementTime(financeAccountPeriodList);
        }
        model.addAttribute("financeAccountPeriodList", financeAccountPeriodList);

        model.addAttribute("status", status);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("arrearsStatus", arrearsStatus);
        //平账
        if (StringUtils.isNotBlank(arrearsStatus)) {
            if (arrearsStatus.equals("1")) {
                return "finance/out/arrears/out_arrears_level";
            }
            //部分平账
            if (arrearsStatus.equals("2")) {
                return "finance/out/arrears/out_arrears_partial";
            }
            //挂账
            if (arrearsStatus.equals("3")) {
                return "finance/out/arrears/out_arrears";
            }
            if (arrearsStatus.equals("4")) {
                return "finance/out/arrears/out_arrears";
            }
            else {
                return null;
            }
        }
        return null;
    }


    /**
     * 查询累计挂账客栈
     *
     * @param settlementTime
     * @param page
     * @param arrearsStatus
     * @return
     */
    @RequestMapping("out/total/arrears")
    public String findTotalArrearFinanceInnSettlement(Model model, String
            settlementTime, Page<FinanceInnSettlement> page, String status, String arrearsStatus, String innName) {
        page = financeOrderService.findTotalArrearFinanceInnSettlement(settlementTime, page, FinanceInnSettlement.ARREARS_TAG, innName);
        List<FinanceAccountPeriod> financeAccountPeriodList = periodService.findAllFinanceAccountPeriod();
        // 查询对账周期
        if (StringUtils.isBlank(settlementTime)) {
            settlementTime = financeHelper.getSettlementTime(financeAccountPeriodList);
        }
        model.addAttribute("financeAccountPeriodList", financeAccountPeriodList);
        model.addAttribute("page", page);
        model.addAttribute("status", status);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("arrearsStatus", arrearsStatus);
        if (StringUtils.isNotBlank(arrearsStatus)) {
            return "finance/out/arrears/out_arrears";
        }
        return null;
    }

    /**
     * 查询往期挂账记录
     *
     * @return
     */
    @RequestMapping("out/past/arrears")
    public String findPastArrears(Model model, Integer innId, String status, String settlementTime, Boolean isMatch, String arrearsStatus) {
        List<FinanceInnSettlement> pastFinanceInnSettlement = financeArrearInnService.findPastArrears(innId, settlementTime);
        model.addAttribute("pastArrears", pastFinanceInnSettlement);
        model.addAttribute("status", status);
        model.addAttribute("isMatch", isMatch);
        model.addAttribute("settlementTime", settlementTime);
        model.addAttribute("arrearsStatus", arrearsStatus);
        return "finance/out/arrears/out_past_arrears";
    }
}
