package com.project.entity.operation;

/**
 * Created by admin on 2016/5/17.
 */

import com.project.entity.proxysale.ProxyInn;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 运营活动
 */
@Entity
@Table(name = "tomato_operation_activity")
public class OperationActivity {
    /* COMMENT ON COLUMN "public"."tomato_operation_activity"."activity_name" IS '活动名称';
     COMMENT ON COLUMN "public"."tomato_operation_activity"."cover_picture" IS '封面图';
     COMMENT ON COLUMN "public"."tomato_operation_activity"."date_line" IS '截止日期';
     COMMENT ON COLUMN "public"."tomato_operation_activity"."start_time" IS '活动开始日期';
     COMMENT ON COLUMN "public"."tomato_operation_activity"."end_time" IS '活动截止日期';
     COMMENT ON COLUMN "public"."tomato_operation_activity"."content" IS '内容';
     COMMENT ON COLUMN "public"."tomato_operation_activity"."require" IS '要求';
     COMMENT ON COLUMN "public"."tomato_operation_activity"."operate_user" IS '最后操作人';

     COMMENT ON COLUMN "public"."tomato_operation_activity"."operate_time" IS '最后操作时间';*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //活动名称
    @Column(name = "activity_name")
    private String activityName;
    //封面图
    @Column(name = "cover_picture")
    private String coverPicture;
    //截止日期
    @Column(name = "date_line")
    private String dateLine;
    //活动开始日期
    @Column(name = "start_time")
    private String startTime;
    //活动截止日期
    @Column(name = "end_time")
    private String endTime;
    //内容
    @Column(name = "content")
    private String content;
    //要求
    @Column(name = "require")
    private String require;
    //最后操作人
    @Column(name = "operate_user")
    private String operateUser;
    //最后操作时间
    @Column(name = "operate_time")
    private Date operateTime;
    //发布时间
    @Column(name = "publish_time")
    private Date publishTime;
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "activity_inn",
            joinColumns = {@JoinColumn(name = "activity_id")},
            inverseJoinColumns = {@JoinColumn(name = "inn_id")})
    private Set<ProxyInn> proxyInns = new HashSet<>();
    //活动状态(0:结束)
    @Column(name = "status")
    private String status;
    //是否推荐
    @Column(name="recommend")
    private Boolean recommend;

    public Boolean getRecommend() {
        return recommend;
    }

    public void setRecommend(Boolean recommend) {
        this.recommend = recommend;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Set<ProxyInn> getProxyInns() {
        return proxyInns;
    }

    public void setProxyInns(Set<ProxyInn> proxyInns) {
        this.proxyInns = proxyInns;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public String getDateLine() {
        return dateLine;
    }

    public void setDateLine(String dateLine) {
        this.dateLine = dateLine;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
