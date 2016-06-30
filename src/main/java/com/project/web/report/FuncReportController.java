package com.project.web.report;


import com.project.bean.report.FuncReportSearchBean;
import com.project.entity.inn.InnFuncReport;
import com.project.entity.plug.InnPlugFunc;
import com.project.entity.wg.WgOtaInfo;
import com.project.service.inn.InnFuncReportManager;
import com.project.service.inn.InnManager;
import com.project.service.plug.InnPlugFuncManager;
import com.project.service.wg.WgOtaInfoManager;
import com.project.utils.time.DateUtil;
import com.project.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表-功能使用统计.
 *
 * @author X
 */
@Controller
@RequestMapping(value = "/funcReport")
public class FuncReportController extends BaseController {

    @Autowired
    private InnFuncReportManager innFuncReportManager;
    @Autowired
    private InnPlugFuncManager innPlugFuncManager;
    @Autowired
    private WgOtaInfoManager wgOtaInfoManager;
    @Autowired
    private InnManager innManager;

    /******
     * help method
     ***/
    public static String[] getDateStrArray(String fromDate, String toDate, String format) {
        Date startDate = DateUtil.parse(fromDate);
        int differ = DateUtil.getDifferDay(fromDate, toDate) + 1;
        String[] item = new String[differ];
        for (int i = 0; i < differ; i++) {
            item[i] = DateUtil.format(startDate, format);
            startDate = DateUtil.addDay(startDate, 1);
        }
        return item;
    }

    @RequestMapping(value = "index")
    public String index(Model model, FuncReportSearchBean searchBean) {
        String to = searchBean.getToDate();
        String from = searchBean.getFromDate();
        String[] items = getDateStrArray(from, to, "yyyy-MM-dd");
        Map<String, InnFuncReport> reportMap = new HashMap<>();
        innFuncReportManager.getMapDetailNew(reportMap, from, to);
        List<InnPlugFunc> plugFuncs = innPlugFuncManager.getAll();
        List<WgOtaInfo> otas = wgOtaInfoManager.getAll();
        //获取插件使用情况
        model.addAttribute("reportMap", reportMap);
        model.addAttribute("searchBean", searchBean);
        model.addAttribute("items", items);
        model.addAttribute("plugFuncs", plugFuncs);
        model.addAttribute("otas", otas);
        model.addAttribute("currentPage", "funcReport");
        return "report/funcReport";
    }

}
