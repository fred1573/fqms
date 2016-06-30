package com.project.web.finance;

import com.project.bean.finance.BarAndLineData;
import com.project.bean.finance.DataForm;
import com.project.bean.finance.PieData;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.service.direct.ProxySaleOrderService;
import com.project.service.finance.DataService;
import com.project.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 订单数据统计分析的控制器类
 * Created by sam on 2015/12/23.
 */
@Controller
@RequestMapping(value = "/data/")
public class DataController extends BaseController {
    private static final String CURRENT_PAGE = "data";
    @Resource
    private DataService dataService;
    @Resource
    private ProxySaleOrderService proxySaleOrderService;

    /**
     * 数据统计列表页
     *
     * @param model    上下文
     * @param dataForm 过滤条件
     * @return
     */
    @RequestMapping("statistics")
    public String statisticsList(Model model, DataForm dataForm) {
        Map<String, Object> statisticsSaleData = dataService.getStatisticsSaleData(dataForm);
        Map<String, Object> statisticsTableData = dataService.getStatisticsTableData(dataForm);
        // 通过OMS接口，获取渠道对象集合
        Map<String, String> otaMap = proxySaleOrderService.getChannelInfoFromOMS();
        model.addAttribute("otaMap", otaMap);
        model.addAttribute("statisticsSaleData", statisticsSaleData);
        model.addAttribute("statisticsTableData", statisticsTableData);
        model.addAttribute("dataForm", dataForm);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "statistics");
        return "/finance/data/statistics_list";
    }

    /**
     * 数据分析列表页
     *
     * @param model    上下文
     * @param dataForm 过滤条件
     * @return
     */
    @RequestMapping("analysis")
    public String analysisList(Model model, DataForm dataForm) {
        int allOrderAmount = dataService.getAllOrderAmount(dataForm);
        // 通过OMS接口，获取渠道对象集合
        Map<String, String> otaMap = proxySaleOrderService.getChannelInfoFromOMS();
        model.addAttribute("otaMap", otaMap);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "analysis");
        model.addAttribute("dataForm", dataForm);
        model.addAttribute("allOrderAmount", allOrderAmount);
        return "/finance/data/analysis_list";
    }

    /**
     * 获取数据统计图表的数据
     *
     * @param dataForm 过滤条件
     * @return
     */
    @RequestMapping("getBarData")
    @ResponseBody
    public BarAndLineData getBarData(DataForm dataForm) {
        return dataService.getStatisticsBarData(dataForm);
    }

    /**
     * 获取饼状图统计表的数据,按状态
     * @param dataForm
     * @return
     */
    @RequestMapping("getPieData")
    @ResponseBody
    public PieData getPieData(DataForm dataForm){
        return dataService.getPieData(dataForm);
    }

    /**获取饼状图统计表的数据，按时间段
     *
     * @param dataForm
     * @return
     */
    @RequestMapping("getPieDataByTime")
    @ResponseBody
    public PieData getPieDataByTime(DataForm dataForm){
        return dataService.getPieByTime(dataForm);
    }


    @RequestMapping("repair")
    @ResponseBody
    public AjaxResult repair(){
        dataService.repairData();
        return new AjaxResult(Constants.HTTP_OK, "操作成功");
    }

}
