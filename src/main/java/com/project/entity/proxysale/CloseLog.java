package com.project.entity.proxysale;

import com.project.enumeration.CloseType;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 关房记录对象
 * Created by 番茄桑 on 2015/9/9.
 */
@Entity
@Table(name = "tomato_proxysale_close_log")
public class CloseLog {
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
    private Integer creator;

    // 最后修改人
    @Column(name = "modifior")
    private Integer modifior;

    // 关房类型
    @Column(name = "close_type")
    private CloseType closeType;

    // 区域ID，当closeType为AREA时有值
    @Column(name = "area_id")
    private Integer areaId;

    // 客栈ID，当closeType为INN时有值
    @Column(name = "inn_id")
    private Integer innId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "closeLog")
    private Set<CloseDate> closeDates = new HashSet<>();

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

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getModifior() {
        return modifior;
    }

    public void setModifior(Integer modifior) {
        this.modifior = modifior;
    }

    public CloseType getCloseType() {
        return closeType;
    }

    public void setCloseType(CloseType closeType) {
        this.closeType = closeType;
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

    public Set<CloseDate> getCloseDates() {
        return closeDates;
    }

    public void setCloseDates(Set<CloseDate> closeDates) {
        this.closeDates = closeDates;
    }
}
