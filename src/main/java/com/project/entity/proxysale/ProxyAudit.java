package com.project.entity.proxysale;

import com.project.entity.account.User;

import javax.persistence.*;
import java.util.Date;

/**代销审核记录
 * Created by Administrator on 2015/8/28.
 */
@Entity
@Table(name = "tomato_proxysale_audit")
public class ProxyAudit {
    //价格审核
    public static final Integer AUDIT_PRICE = 1;
    //合同审核
    public static final Integer AUDIT_CONTRACT = 2;
    //状态否决
    public static final String STATUS_REJECTED = "REJECTED";
    //状态通过
    public static final String STATUS_CHECKED = "CHECKED";
    public static final String STATUS_UNCHECK = "UNCHECK";
    public static final String STATUS_REPEAT = "REPEAT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "inn_id")
    private Integer innId;//客栈ID

    @Column(name = "record_no")
    private String recordNo;//审核编号（合同记录为合同编号，价格记录为价格编号）

    @Column
    private String status; //审核状态  CHECKED通过    REJECTED否决

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "auditor")
    private User auditor;//审核人

    @Column(name = "audit_time")
    private Date auditTime;//审核时间

    @Column
    private Short pattern;//价格模式  JINGPIN/NORMAL 仅价格审核记录有此字段

    @Column
    private Integer type;//审核记录类型     1-价格   2-合同

    @Column
    private String reason;//失败原因

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getAuditor() {
        return auditor;
    }

    public void setAuditor(User auditor) {
        this.auditor = auditor;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Short getPattern() {
        return pattern;
    }

    public void setPattern(Short pattern) {
        this.pattern = pattern;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
