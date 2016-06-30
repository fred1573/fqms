package com.project.service.operation;

import com.project.core.orm.Page;
import com.project.entity.operation.OperationActivity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/5/17.
 */
@Service
@Transactional
public interface OperationActivityService {
    void save(OperationActivity operationActivity, MultipartFile file, String operate, Integer id, HttpSession session, String publishTime);

    /**
     * 按页查询活动
     *
     * @param page
     * @return
     */
    Page<OperationActivity> getActivities(Page<OperationActivity> page, String activityName);

    /**
     * 获取所有活动
     *
     * @return
     */
    List<OperationActivity> findActivities();

    /**
     * 获取客栈活动参与状态
     *
     * @param innId
     * @return
     */
    List<Map<String, Object>> findStatus(Integer innId);

    /**
     * 封装接口返回数据
     *
     * @param innId
     * @param pageNo
     * @return
     */
    List<Map<String, Object>> packActivity(Integer innId, Integer pageNo);

    /**
     * 结束活动
     *
     * @param id
     */
    void finishActivity(Integer id);

    /**
     * 活动下的客栈分页
     *
     * @param page
     * @return
     */
    Page<Map<String, Object>> getInnWithActivity(Page<Map<String, Object>> page, Integer activityId, String innName);

    List<Map<String, Object>> getInnWithActivity(Integer activityId);

    /**
     * 更新客栈活动状态
     *
     * @param activityId
     * @param innId
     * @param status
     */
    void updateInnStatus(Integer activityId, Integer innId, String status);

    /**
     * 一键同意
     *
     * @param activityId
     */
    void updateInnStatusAll(Integer activityId);

    /**
     * 统计参与活动客栈
     *
     * @param activityId
     * @return
     */
    Map<String, Object> statisticInn(Integer activityId);

    /**
     * 导出客栈execl
     *
     * @param activityId
     * @param request
     */
    void exportExecl(Integer activityId, HttpServletRequest request);

    /**
     * 保存客栈审核信息
     *
     * @param activityId
     * @param innId
     */
    void applicationActivity(Integer activityId, Integer innId);

    /**
     * 获取总记录条数
     *
     * @return
     */
    List<Integer> getPageNum();

    /**
     * 根据Id获取活动对象
     *
     * @param id
     * @return
     */
    OperationActivity getActivityById(Integer id);

    /**
     * 定时任务结束活动
     *
     * @param id
     */
    void finishActivityJob(Integer id);
}
