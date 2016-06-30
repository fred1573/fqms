package com.project.web.operation;

import com.project.bean.AjaxOperateActivity;
import com.project.bean.vo.AjaxBase;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.entity.operation.OperationActivity;
import com.project.service.operation.OperationActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/5/17.
 */
@Controller
public class OperationActivityController {
    @Resource
    private OperationActivityService operationActivityService;
    private static final String CURRENT_PAGE = "activity";

    /**
     * 新增活动
     *
     * @param operationActivity
     * @return
     */
    @RequestMapping("/activity/add")
    public String save(MultipartFile file, OperationActivity operationActivity, String operate, Integer id, HttpSession session, String publish) {

        operationActivityService.save(operationActivity, file, operate, id, session, publish);
        return "redirect:/activity/list";
    }

    /**
     * 跳转到活动展示页面
     *
     * @return
     */
    @RequestMapping("/activity/list")
    public String getActivityList(Page<OperationActivity> page, Model model, String activityName) {
        model.addAttribute("currentPage", CURRENT_PAGE);
        Page<OperationActivity> page1 = operationActivityService.getActivities(page, activityName);
        model.addAttribute("currentPage", "activity");
        model.addAttribute("page", page1);
        return "/activities/list";
    }

    /**
     * 跳转到添加页面
     *
     * @return
     */
    @RequestMapping("/activity/to/add")
    public String toAddPage(Integer id, Model model, String operate) {
        model.addAttribute("id", id);
        model.addAttribute("operate", operate);
        if (null != id) {
            OperationActivity operationActivity = operationActivityService.getActivityById(id);
            if (null != operationActivity) {
                model.addAttribute("activity", operationActivity);
            }
        }
        return "/activities/add_activity";
    }

    /**
     * 获取活动接口
     *
     * @param pageNo
     * @param innId
     * @return
     */
    @RequestMapping("/activity/all")
    @ResponseBody
    public AjaxBase apiGetActivties(Integer pageNo, Integer innId) {
        List<Object> objects = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            result = operationActivityService.packActivity(innId, pageNo);
            List<Integer> list = operationActivityService.getPageNum();
            return new AjaxOperateActivity(Constants.HTTP_OK, "请求成功", result, list.get(0), list.get(1));
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, "获取运营活动请求失败" + e.getMessage());
        }
    }

    /**
     * 客栈申请加入活动
     *
     * @param activityId
     * @param innId
     * @return
     */
    @RequestMapping(" /activity/application")
    @ResponseBody
    public AjaxResult applicationActivity(Integer activityId, Integer innId) {
        try {
            operationActivityService.applicationActivity(activityId, innId);
            return new AjaxResult(Constants.HTTP_OK, "参加活动成功");
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "参加活动失败");
        }
    }

    /**
     * 结束活动
     *
     * @param id
     * @return
     */
    @RequestMapping("/activity/finish")
    public String finishActivity(Integer id) {
        operationActivityService.finishActivity(id);
        return "redirect:/activity/list";
    }

    /**
     * 活动下的客栈详情页
     *
     * @param page
     * @param activityId
     * @return
     */
    @RequestMapping("/activity/inn")
    public String activityInn(Page<Map<String, Object>> page, Integer activityId, Model model, String innName) {
        page = operationActivityService.getInnWithActivity(page, activityId, innName);
        Map<String, Object> objectMap = operationActivityService.statisticInn(activityId);
        model.addAttribute("page", page);
        model.addAttribute("count", objectMap.get("count"));
        model.addAttribute("activityId", activityId);
        return "activities/enter_activity";
    }

    /**
     * 是否同意客栈加入活动
     *
     * @return
     */
    @RequestMapping("/activity/isAgree")
    public String isAgree(Integer activityId, Integer innId, String status) {
        operationActivityService.updateInnStatus(activityId, innId, status);
        return "redirect:/activity/inn?activityId=" + activityId;
    }

    /**
     * 一键客栈加入活动
     *
     * @return
     */
    @RequestMapping("/activity/agreeAll")
    public String agreeAll(Integer activityId) {
        operationActivityService.updateInnStatusAll(activityId);
        return "redirect:/activity/inn?activityId=" + activityId;
    }

    /**
     * 导出execl
     *
     * @param request
     * @param activityId
     */
    @RequestMapping("/activity/export")
    @ResponseBody
    public void exportExcel(HttpServletRequest request, Integer activityId) {
        operationActivityService.exportExecl(activityId, request);
    }


}
