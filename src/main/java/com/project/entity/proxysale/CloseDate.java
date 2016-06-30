package com.project.entity.proxysale;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * 关房日期对象
 * Created by 番茄桑 on 2015/9/9.
 */
@Entity
@Table(name = "tomato_proxysale_close_date")
public class CloseDate {
    // 主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 关房开始日期
    @Column(name = "close_begin_date")
    private String closeBeginDate;

    // 关房结束日期
    @Column(name = "close_end_date")
    private String closeEndDate;

    // 状态，0有效
    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "log_id")
    @JsonIgnore
    private CloseLog closeLog;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof CloseDate) {
            CloseDate closeDate = (CloseDate) obj;
            if (closeDate.closeBeginDate.equals(closeBeginDate) && closeDate.closeEndDate.equals(closeEndDate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.closeBeginDate.hashCode() + this.closeEndDate.hashCode();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCloseBeginDate() {
        return closeBeginDate;
    }

    public void setCloseBeginDate(String closeBeginDate) {
        this.closeBeginDate = closeBeginDate;
    }

    public String getCloseEndDate() {
        return closeEndDate;
    }

    public void setCloseEndDate(String closeEndDate) {
        this.closeEndDate = closeEndDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CloseLog getCloseLog() {
        return closeLog;
    }

    public void setCloseLog(CloseLog closeLog) {
        this.closeLog = closeLog;
    }
}
