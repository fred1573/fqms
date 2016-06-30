package com.project.entity.proxysale;

import javax.persistence.*;
import java.util.Date;

/**
 * 关房任务对象
 * Created by sam on 2016/4/12.
 */
@Entity
@Table(name = "tomato_proxysale_close_task")
public class CloseTask {
    @Transient
    public static final String ALL_CLOSE = "0";
    @Transient
    public static final String AREA_CLOSE = "1";
    @Transient
    public static final String INN_CLOSE = "2";
    // 主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 创建时间
    @Column(name = "date_created")
    private Date dateCreated = new Date();
    // 最后修改时间
    @Column(name = "date_updated")
    private Date dateUpdated = new Date();
    // 创建人
    @Column(name = "creator")
    private String creator;
    // 最后修改人
    @Column(name = "modifior")
    private String modifior;
    // 接口类型(1:OMS关房,2:分销商锁房)
    @Column(name = "off_type")
    private String offType;
    // 区域ID
    @Column(name = "area_id")
    private Integer areaId;
    // 区域ID
    @Column(name = "inn_id")
    private Integer innId;
    // 关房开始日期
    @Column(name = "begin_date")
    private String beginDate;
    // 关房结束日期
    @Column(name = "end_date")
    private String endDate;
    // 执行次数
    @Column(name = "execute_time")
    private int executeTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifior() {
        return modifior;
    }

    public void setModifior(String modifior) {
        this.modifior = modifior;
    }

    public String getOffType() {
        return offType;
    }

    public void setOffType(String offType) {
        this.offType = offType;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(int executeTime) {
        this.executeTime = executeTime;
    }
}
