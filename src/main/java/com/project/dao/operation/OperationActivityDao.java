package com.project.dao.operation;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.operation.OperationActivity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/5/17.
 */
@Component("operationActivityDao")
public class OperationActivityDao extends HibernateDao<OperationActivity, Integer> {
    /**
     * 分页查询活动列表
     *
     * @param page
     * @return
     */
    public Page<OperationActivity> getActivities(Page<OperationActivity> page, String activityName) {
        StringBuilder stringBuilder = new StringBuilder("select * from tomato_operation_activity");
        if (StringUtils.isNotBlank(activityName)) {
            stringBuilder.append(" where activity_name like '%" + activityName + "%'");
        }
        stringBuilder.append(" ORDER BY status DESC,publish_time DESC,id DESC");
        return findPageWithSql(page, stringBuilder.toString());
    }

    /**
     * 获取所有活动
     *
     * @return
     */
    public List<OperationActivity> findAllActivities() {
        String sql = "SELECT * FROM tomato_operation_activity ";
        return findWithSql(sql);
    }

    /**
     * 获取客栈活动情况
     *
     * @param innId
     * @return
     */
    public List<Map<String, Object>> findStatus(Integer innId) {
        String sql = "SELECT t2.activity_id AS activity,t2.status AS status,t2.recommend AS recommend FROM tomato_proxysale_inn t1 RIGHT  JOIN activity_inn t2 ON t1.inn=t2.inn_id WHERE t1.inn=?";
        return findListMapWithSql(sql, innId);
    }

    /**
     * 结束活动
     *
     * @param id
     */
    public void updateActivity(Integer id) {
        String sql = "UPDATE tomato_operation_activity SET status=0 WHERE id=?";
        executeUpdateWithSql(sql, id);
    }

    /**
     * 结束活动中间表
     *
     * @param id
     */
    public void updateActivityInn(Integer id) {
        String sql = "UPDATE activity_inn SET status=0 WHERE activity_id=?";
        executeUpdateWithSql(sql, id);
    }

    /**
     * 活动下的客栈导表
     *
     * @return
     */
    public List<Map<String, Object>> getInnWithActivity(Integer activityId) {
        String sql = "SELECT\n" +
                "\tt4.name AS area,t3.name AS region,t1.inn_name AS innname,t1.inn AS pmsid,t2.status AS status\n" +
                "FROM\n" +
                "\ttomato_proxysale_inn t1\n" +
                "RIGHT JOIN activity_inn t2 ON t1.inn = t2.inn_id\n" +
                "RIGHT JOIN tomato_inn_region t3 ON t1.region = t3. ID\n" +
                "RIGHT JOIN tomato_base_area t4 ON t1.area = t4. ID\n" +
                "WHERE\n" +
                "\tt2.activity_id = ?\n" +
                "GROUP BY\n" +
                "\tt1.id,t4.name,t3.name,t2.status";
        return findListMapWithSql(sql, activityId);
    }

    /**
     * 活动下的客栈分页
     *
     * @param page
     * @return
     */
    public Page<Map<String, Object>> getInnWithActivity(Page<Map<String, Object>> page, Integer activityId, String innName) {
        String sql = "SELECT\n" +
                "\tt4.name AS area,t3.name AS region,t1.inn_name AS innname,t1.inn AS pmsid,t2.status AS status\n" +
                "FROM\n" +
                "\ttomato_proxysale_inn t1\n" +
                "RIGHT JOIN activity_inn t2 ON t1.inn = t2.inn_id\n" +
                "RIGHT JOIN tomato_inn_region t3 ON t1.region = t3. ID\n" +
                "RIGHT JOIN tomato_base_area t4 ON t1.area = t4. ID\n" +
                "WHERE\n" +
                "\tt2.activity_id = ?\n";
        if (StringUtils.isNotBlank(innName)) {
            sql += " and t1.inn_name like '%" + innName + "%'";
        }
        sql += "GROUP BY\n" +
                "\tt1.id,t4.name,t3.name,t2.status";

        return findListMapPageWithSql(page, sql, activityId);
    }

    /**
     * 更新客栈活动状态
     */
    public void updateInnStatus(Integer activityId, Integer innId, String status) {
        String sql = "UPDATE activity_inn SET status=? WHERE inn_id=? AND activity_id=?";
        executeUpdateWithSql(sql, status, innId, activityId);
    }

    /**
     * 活动一键同意
     *
     * @param activityId
     */
    public void updateInnStatusAll(Integer activityId) {
        String sql = "UPDATE activity_inn SET status='2' WHERE  activity_id=? and status='1'";
        executeUpdateWithSql(sql, activityId);
    }

    /**
     * 按活动id查询所有未处理的申请客栈
     *
     * @param activityId
     * @return
     */
    public List<Map<String, Object>> findInnIdsWithAvtivity(Integer activityId) {
        String sql = "SELECT inn_id AS id from activity_inn WHERE status='1' and activity_id=?";
        return findListMapWithSql(sql, activityId);
    }

    /**
     * 统计参与活动客栈
     *
     * @param activityId
     * @return
     */
    public Map<String, Object> statisticInn(Integer activityId) {
        String sql = "SELECT count(inn_id) AS count FROM activity_inn WHERE activity_id=?";
        return findMapWithSql(sql, activityId);
    }

    /**
     * 查询指定活动下的指定客栈
     *
     * @param activityId
     * @param innId
     * @return
     */
    public Map<String, Object> getActivityInn(Integer activityId, Integer innId) {
        String sql = "SELECT * from activity_inn WHERE inn_id=? and activity_id=?";
        return findMapWithSql(sql, innId, activityId);
    }

    /**
     * 添加参加活动的客栈信息
     *
     * @param activityId
     * @param innId
     */
    public void saveActivityInn(Integer activityId, Integer innId) {
        String sql = "INSERT INTO activity_inn(inn_id,activity_id,status,recommend) VALUES(?,?,?,?)";
        executeUpdateWithSql(sql, innId, activityId, '1', false);
    }

    /**
     * 更新客栈状态为审核
     *
     * @param activityId
     * @param innId
     */
    public void updateActivityInn(Integer activityId, Integer innId) {
        String sql = "UPDATE activity_inn SET status='1' WHERE inn_id=? and activity_id=?";
        executeUpdateWithSql(sql, innId, activityId);
    }

    /**
     * 获取总记录数
     *
     * @return
     */
    public Map<String, Object> getTotalRecords() {
        String sql = "select count(id) as count FROM tomato_operation_activity ";
        return findMapWithSql(sql);
    }

    /**
     * 更新活动对象
     * @param operationActivity
     */
    public void updateOperationActivity(OperationActivity operationActivity) {
        StringBuffer sql = new StringBuffer("UPDATE tomato_operation_activity SET operate_time=now()");
        String activityName = operationActivity.getActivityName();
        if (StringUtils.isNotBlank(activityName)) {
            sql.append(",activity_name='" + activityName + "'");
        }
        String coverPicture = operationActivity.getCoverPicture();
        if (StringUtils.isNotBlank(coverPicture)) {
            sql.append(",cover_picture='" + coverPicture + "'");
        }
        String dateLine = operationActivity.getDateLine();
        if (StringUtils.isNotBlank(dateLine)) {
            sql.append(",date_line='" + dateLine + "'");
        }
        String startTime = operationActivity.getStartTime();
        if (StringUtils.isNotBlank(startTime)) {
            sql.append(",start_time='" + startTime + "'");
        }
        String endTime = operationActivity.getEndTime();
        if (StringUtils.isNotBlank(endTime)) {
            sql.append(",end_time='" + endTime + "'");
        }
        String content = operationActivity.getContent();
        if (StringUtils.isNotBlank(content)) {
            sql.append(",content='" + content + "'");
        }
        String require = operationActivity.getRequire();
        if (StringUtils.isNotBlank(require)) {
            sql.append(",require='" + require + "'");
        }
        String operateUser = operationActivity.getOperateUser();
        if (StringUtils.isNotBlank(operateUser)) {
            sql.append(",operate_user='" + operateUser + "'");
        }
        Date publishTime = operationActivity.getPublishTime();
        if (publishTime != null) {
            sql.append(",publish_time='" + publishTime + "'");
        }
        String status = operationActivity.getStatus();
        if (StringUtils.isNotBlank(status)) {
            sql.append(",status='" + status + "'");
        }
        Boolean recommend = operationActivity.getRecommend();
        if (recommend != null) {
            sql.append(",recommend=" + recommend);
        }
        sql.append(" where id=" + operationActivity.getId());
        executeUpdateWithSql(sql.toString());
    }
}
