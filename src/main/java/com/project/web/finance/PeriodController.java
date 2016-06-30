package com.project.web.finance;

import com.project.bean.vo.AjaxBase;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.entity.finance.FinanceAccountPeriod;
import com.project.service.finance.PeriodService;
import com.project.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 账期管理控制器
 * Created by sam on 2015/12/30.
 */
@Controller
@RequestMapping(value = "/period/")
public class PeriodController extends BaseController {
    private static final String CURRENT_PAGE = "finance";
    private static final int PAGE_SIZE = 10;
    @Resource
    private PeriodService periodService;

    /**
     * 跳转到账期管理页面
     *
     * @param model
     * @param page
     * @return
     */
    @RequestMapping("list")
    public String periodList(Model model, Page<FinanceAccountPeriod> page) {
        initPage(page, PAGE_SIZE);
        page = periodService.selectProxysalePeriod(page);
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "period");
        model.addAttribute(page);
        return "/finance/period_list";
    }

    /**
     * 按账期抓取订单任务
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @RequestMapping("create")
    @ResponseBody
    public AjaxBase updateFinanceOrderByPeriod(String beginDate, String endDate) {
        try {
            periodService.updateFinanceOrderByPeriod(beginDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            return new AjaxBase(Constants.HTTP_500, "账期创建失败：" + e);
        }
        return new AjaxBase(Constants.HTTP_OK, "账期添加成功");
    }

    @RequestMapping("updateFinanceOrder")
    @ResponseBody
    public AjaxBase updateFinanceOrder(String beginDate, String endDate) {
        try {
            periodService.updateFinanceOrder(beginDate, endDate);
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "账期创建失败：" + e.getMessage());
        }
        return new AjaxBase(Constants.HTTP_OK, "账期添加成功");
    }

    @RequestMapping("innInfo/update")
    @ResponseBody
    public AjaxBase updateInnInfo() {
        try {
            periodService.updateInnInfo();
            return new AjaxBase(Constants.HTTP_OK, "更新账单成功");
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "更新账单失败");
        }
    }
}
